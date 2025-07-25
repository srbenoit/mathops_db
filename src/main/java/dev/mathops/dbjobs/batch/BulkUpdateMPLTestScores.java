package dev.mathops.dbjobs.batch;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.mathplan.MathPlanLogic;
import dev.mathops.db.logic.mathplan.StudentMathPlan;
import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.majors.MajorsCurrent;
import dev.mathops.db.logic.mathplan.majors.MajorsDeactivated;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.old.rawlogic.RawMpscorequeueLogic;
import dev.mathops.db.old.rawlogic.RawStmathplanLogic;
import dev.mathops.db.old.rawlogic.RawStudentLogic;
import dev.mathops.db.old.rawrecord.RawMpscorequeue;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStudent;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A job that searches for all students with a "WLCM5" MathPlan outcome, and then make sures that student has a
 * corresponding "MPL" test score in SORTEST.  If the SORTEST record is missing, a record is inserted.
 *
 * <p>
 * The logic for an individual student is as follows:
 * <pre>
 * IF
 *     the student has any MATH PLAN 'WLCM5' response records
 * THEN
 *     IF
 *         the student's program on record is any that needs only AUCC-1B
 *     THEN
 *         the student should have a "MPL" test score of "1" to indicate Math Placement is not needed
 *     ELSE IF
 *         the student's Math Plan recommendation is any AUCC-1B course
 *     THEN
 *         the student should have a "MPL" test score of "1" to indicate Math Placement is not needed
 *     ELSE IF
 *         the student has completed the Math Placement tool
 *     THEN
 *         the student should have a "MPL" test score of "1" to indicate Math Placement is not needed
 *     ELSE IF
 *         the student has any MATH transfer credit that clears 1B or satisfies the prerequisite for MATH 117
 *     THEN
 *         the student should have a "MPL" test score of "1" to indicate Math Placement is not needed
 *     ELSE
 *         the student should have a "MPL" test score of "2" to indicate Math Placement is needed
 *     END IF
 * ELSE
 *     the student should not have any "MPL" test score at all, to indicate the Math Plan is not yet complete
 * END IF
 * </pre>
 */
public final class BulkUpdateMPLTestScores {

    /** Majors that need only core courses. */
    private static final Set<String> MAJORS_NEEDING_ONLY_AUCC;

    /** Majors that need more than just core courses. */
    private static final Set<String> MAJORS_NEEDING_MORE;

    /** Debug flag - true to skip (but print) updates; false to actually perform updates. */
    private static final boolean DEBUG = true;

    /** The test code. */
    private static final String TEST_CODE = "MPL";

    /** A commonly used integer. */
    private static final Integer ONE = Integer.valueOf(1);

    /** The database profile through which to access the database. */
    private final Profile profile;

    static {
        MAJORS_NEEDING_ONLY_AUCC = new HashSet<>(100);
        MAJORS_NEEDING_MORE = new HashSet<>(100);

        for (final Major major : MajorsCurrent.INSTANCE.getMajors()) {
            if (major.requirements == ERequirement.CORE_ONLY) {
                MAJORS_NEEDING_ONLY_AUCC.addAll(major.programCodes);
            } else {
                MAJORS_NEEDING_MORE.addAll(major.programCodes);
            }
        }
        for (final Major major : MajorsDeactivated.INSTANCE.getMajors()) {
            if (major.requirements == ERequirement.CORE_ONLY) {
                MAJORS_NEEDING_ONLY_AUCC.addAll(major.programCodes);
            } else {
                MAJORS_NEEDING_MORE.addAll(major.programCodes);
            }
        }
    }

    /**
     * Constructs a new {@code BulkUpdateMPLTestScores}.
     */
    public BulkUpdateMPLTestScores() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        this.profile = config.getCodeProfile(Contexts.BATCH_PATH);
    }

    /**
     * Executes the job.
     *
     * @return the report
     */
    public String execute() {

        final Collection<String> report = new ArrayList<>(10);

        if (this.profile == null) {
            final String msg = "Unable to create production profile.";
            Log.warning(msg);
            report.add(msg);
        } else {
            final Cache cache = new Cache(this.profile);

            try {
                execute(cache, report);
            } catch (final SQLException ex) {
                final String exMsg = ex.getMessage();
                final String msg = HtmlBuilder.concat("EXCEPTION: ", exMsg);
                Log.warning(msg);
                report.add(msg);
            }
        }

        final HtmlBuilder htm = new HtmlBuilder(1000);
        htm.addln("<pre>");
        for (final String rep : report) {
            htm.addln(rep);
        }
        htm.addln("</pre>");

        return htm.toString();
    }

    /**
     * Executes the job.
     *
     * @param cache  the data cache
     * @param report a list of strings to which to add report output lines
     * @throws SQLException if there is an error accessing the database
     */
    public void execute(final Cache cache, final Collection<? super String> report) throws SQLException {

        // Determine the list of students who should have MPL test scores of some kind
        final String msg1 = "Scanning student MathPlan status...";
        Log.fine(msg1);
        report.add(msg1);

        final List<RawStmathplan> allStMathPlan = RawStmathplanLogic.queryAll(cache);
        final int size = allStMathPlan.size();
        final String sizeStr = Integer.toString(size);

        final String msg2 = HtmlBuilder.concat("    Found ", sizeStr, " MathPlan responses");
        report.add(msg2);
        Log.fine(msg2);

        final Map<String, RawStmathplan> latest1 = new HashMap<>(25000);

        // Find the most recent "WLCM5" row with survey_nbr='1', use that as date/time of Math Plan completion - we
        // scan for the most recent such record.

        for (final RawStmathplan row : allStMathPlan) {
            if ("WLCM5".equals(row.version) && ONE.equals(row.surveyNbr)) {
                final LocalDateTime when = row.getWhen();
                final RawStmathplan existing1 = latest1.get(row.stuId);
                if (existing1 == null) {
                    latest1.put(row.stuId, row);
                } else {
                    final LocalDateTime existingWhen = existing1.getWhen();
                    if (existingWhen == null || when.isAfter(existingWhen)) {
                        latest1.put(row.stuId, row);
                    }
                }
            }
        }

        final int size1 = latest1.size();
        final String size1Str = Integer.toString(size1);
        final String msg3 = HtmlBuilder.concat("    Found ", size1Str, " 'WLCM5' question 1 responses");
        Log.fine(msg3);
        report.add(msg3);

        final Collection<String> stuIds = new HashSet<>(25000);
        final Set<String> keys1 = latest1.keySet();
        stuIds.addAll(keys1);

        final int sizeAll = stuIds.size();
        final String sizeAllStr = Integer.toString(sizeAll);
        final String msg4 = HtmlBuilder.concat("    Found ", sizeAllStr, " distinct students with responses");
        Log.fine(msg4);
        report.add(msg4);

        // Compare results with SORTEST table
        Log.fine(CoreConstants.EMPTY);
        final String msg5 = "Scanning SORTEST table...";
        Log.fine(msg5);
        report.add(msg5);

        final Login login = this.profile.getLogin(ESchema.LIVE);

        final DbConnection liveConn = login.checkOutConnection();
        final LocalDateTime now = LocalDateTime.now();
        try {
            int count1 = 0;
            int count2 = 0;

            for (final String stuId : stuIds) {

                RawStudent student = RawStudentLogic.query(cache, stuId, false);
                if (student == null) {
                    final String msg = HtmlBuilder.concat("   WARNING: Student ", stuId, " needed to be retrieved");
                    Log.fine(msg);
                    report.add(msg);
                    student = RawStudentLogic.query(cache, stuId, true);
                }

                if (student == null) {
                    final String msg = HtmlBuilder.concat("   ERROR: Student ", stuId, " not found!");
                    Log.warning(msg);
                    report.add(msg);
                } else if (student.pidm == null) {
                    final String msg = HtmlBuilder.concat("   ERROR: Student ", stuId, " has no PIDM!");
                    Log.warning(msg);
                    report.add(msg);
                } else {
                    final List<RawMpscorequeue> existing = RawMpscorequeueLogic.querySORTESTByStudent(liveConn,
                            student.pidm);

                    RawMpscorequeue mostRecent = null;
                    for (final RawMpscorequeue test : existing) {
                        if (TEST_CODE.equals(test.testCode)) {
                            if (mostRecent == null || mostRecent.testDate.isBefore(test.testDate)) {
                                mostRecent = test;
                            }
                        }
                    }

                    String wantValue = null;
                    String wantNote = null;

                    if (student.programCode != null && MathPlanLogic.isProgramCodeIgnored(student.programCode)) {
                        wantValue = "1";
                        wantNote = " (not needed, program " + student.programCode + " is ignored)";
                    } else {
                        final StudentMathPlan plan = MathPlanLogic.queryPlan(cache, stuId);

                        if (student.programCode != null && isProgramOnlyAUCC(student, report)) {
                            wantValue = "1";
                            if (plan.nextSteps.placementNeeded) {
                                // Student has declared a major that only needs AUCC, but in the Math Plan, they
                                // indicated courses that would require higher math.
                                wantNote = " (not needed, AUCC only for declared major)";
                            } else {
                                wantNote = " (" + plan.nextSteps.nextStep + ")";
                            }
                        } else if (latest1.containsKey(stuId)) {
                            if (plan.stuStatus.isPlacementCompleted()) {
                                wantNote = " (not needed, already done)";
                                wantValue = "1";
                            } else if (plan.nextSteps.placementNeeded) {
                                wantValue = "2";
                                wantNote = " (placement needed)";
                            } else {
                                wantValue = "1";
                                wantNote = " (not needed)";
                            }
                        }
                    }

                    boolean doInsert = false;
                    if (wantValue == null) {
                        if (mostRecent != null) {
                            final String msg = HtmlBuilder.concat("Student ", stuId,
                                    " who has not completed MathPlan has a MPL score of ", mostRecent.testScore);
                            Log.warning(msg);
                            report.add(msg);
                        }
                    } else if (mostRecent == null) {
                        // Insert the new score
                        doInsert = true;
                    } else if (!wantValue.equals(mostRecent.testScore)) {
                        // Score has changed - insert a new score
                        doInsert = true;
                    }

                    if (doInsert) {
                        // Score has changed - insert a new score
                        if (DEBUG) {
                            final String msg = HtmlBuilder.concat("   Need to insert MPL=", wantValue, wantNote,
                                    " test score for ", stuId);
                            Log.fine(msg);
                            report.add(msg);
                        } else {
                            final String msg = HtmlBuilder.concat("   Inserting MPL=", wantValue, wantNote,
                                    " test score for ", stuId);
                            Log.fine(msg);
                            report.add(msg);

                            final RawMpscorequeue toInsert = new RawMpscorequeue(student.pidm, TEST_CODE, now,
                                    wantValue);
                            if (!RawMpscorequeueLogic.insertSORTEST(liveConn, toInsert)) {
                                final String msg6 = HtmlBuilder.concat("   ERROR: Failed to insert MPL=", wantValue,
                                        " test score for ", stuId);
                                Log.warning(msg6);
                                report.add(msg6);
                            }
                        }
                        if ("2".equals(wantValue)) {
                            ++count2;
                        } else {
                            ++count1;
                        }
                    }
                }
            }

            final String count1Str = Integer.toString(count1);
            final String msg6 = HtmlBuilder.concat("    Found ", count1Str,
                    " to update to score 1 (placement not needed)");
            Log.fine(msg6);
            report.add(msg6);

            final String count2Str = Integer.toString(count2);
            final String msg7 = HtmlBuilder.concat("    Found ", count2Str, " to update to score 2 (placement needed)");
            Log.fine(msg7);
            report.add(msg7);
        } finally {
            login.checkInConnection(liveConn);
        }
    }

    /**
     * Tests whether the student's program code is one of those that needs only AUCC-1B.  NOTE: this method is only
     * called for program codes that are not in the "ignored" list.
     *
     * @param student the student record
     * @return true if this code only needs AUCC 1B
     */
    private boolean isProgramOnlyAUCC(final RawStudent student, final Collection<? super String> report) {

        final boolean auccOnly;

        final String programCode = student.programCode;

        if (programCode == null || programCode.isBlank()) {
            auccOnly = false;
        } else if (MAJORS_NEEDING_ONLY_AUCC.contains(programCode)) {
            auccOnly = true;
        } else if (MAJORS_NEEDING_MORE.contains(programCode)) {
            auccOnly = false;
        } else {
            final int ddIndex = programCode.indexOf("-DD-");

            if (ddIndex == -1) {
                final String msg = HtmlBuilder.concat("Unrecognized program code: ", programCode, ", student ",
                        student.stuId, " college is ", student.college, " and department is ", student.dept);
                Log.warning(msg);
                report.add(msg);
                auccOnly = false;
            } else {
                final String newCode = programCode.substring(0, ddIndex) + programCode.substring(ddIndex + 3);

                if (MAJORS_NEEDING_ONLY_AUCC.contains(newCode)) {
                    auccOnly = true;
                } else if (MAJORS_NEEDING_MORE.contains(newCode)) {
                    auccOnly = false;
                } else {
                    final String msg = HtmlBuilder.concat("Unrecognized program code: ", newCode, ", student ",
                            student.stuId, " college is ", student.college, " and department is ", student.dept);
                    Log.warning(msg);
                    report.add(msg);
                    auccOnly = false;
                }
            }
        }

        return auccOnly;
    }

    /**
     * Main method to execute the batch job.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();
        final BulkUpdateMPLTestScores job = new BulkUpdateMPLTestScores();

        final String log = job.execute();
        Log.fine(log);
    }
}
