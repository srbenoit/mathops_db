package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.TemporalUtils;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.logic.mathplan.types.EMathPlanStatus;
import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.majors.Majors;
import dev.mathops.db.old.rawlogic.RawStmathplanLogic;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStudent;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Business logic to manage the "Math Plan".  This class is the entry point for all functions in this package.
 */
public enum MathPlanLogic {
    ;

    /** Program codes for majors that are ignored. */
    private static final List<String> IGNORED_MAJORS = Arrays.asList("GUES-CEUG", "FESV-DD-BS", "CSOR");

    /** A map key. */
    private static final Integer ONE = Integer.valueOf(1);

    /**
     * Tests whether a program code should be ignored.
     *
     * @param code the program code
     * @return true if the program code should be ignored
     */
    public static boolean isProgramCodeIgnored(final String code) {

        return IGNORED_MAJORS.contains(code) || code.endsWith("-GR") || code.endsWith("-MS") || code.endsWith("-MA")
               || code.endsWith("-MFA") || code.endsWith("-UG") || code.endsWith("-CT") || code.endsWith("-SI")
               || code.endsWith("-MAS") || code.endsWith("-M") || code.endsWith("-ME") || code.endsWith("-MM")
               || code.endsWith("-MBA") || code.endsWith("-DVM") || code.endsWith("-MTM") || code.endsWith("-MOT")
               || code.endsWith("-MCS") || code.endsWith("-MAGR") || code.endsWith("-MAPD") || code.endsWith("-MPSM")
               || code.endsWith("-MPSP") || code.endsWith("-MACC") || code.endsWith("-MACP") || code.endsWith("-MCIS")
               || code.endsWith("-MCMM") || code.endsWith("-MPPA") || code.endsWith("-MFIN") || code.endsWith("-MIOP")
               || code.endsWith("-MFWC") || code.endsWith("-MSM") || code.endsWith("-MSW") || code.endsWith("-MASW")
               || code.endsWith("-MED") || code.endsWith("-DOT") || code.endsWith("-MCL") || code.endsWith("-MCIN")
               || code.endsWith("-MNRS") || code.endsWith("-PHD");
    }

    /**
     * Constructs a "Math Plan" for a student.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @param majors    the student's set of majors of interest
     * @return the constructed math plan, which is based on student responses to
     * @throws SQLException if there is an error accessing the database
     */
    public static StudentMathPlan generatePlan(final Cache cache, final String studentId,
                                               final Collection<Major> majors) throws SQLException {

        final StudentStatus stuStatus = new StudentStatus(cache, studentId);
        final Requirements requirements = new Requirements(majors, stuStatus);

        return new StudentMathPlan(majors, stuStatus, requirements);
    }

    /**
     * Determines a student's status with respect to the Math Plan.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @return the student's status
     * @throws SQLException if there is an error accessing the database
     */
    public static EMathPlanStatus getStatus(final Cache cache, final String studentId) throws SQLException {

        final EMathPlanStatus result;

        final StudentData studentData = cache.getStudent(studentId);

        final Map<Integer, RawStmathplan> q1 = studentData.getLatestMathPlanResponsesByPage(
                MathPlanConstants.MAJORS_PROFILE);

        if (q1.isEmpty()) {
            result = EMathPlanStatus.NOT_STARTED;
        } else {
            final Map<Integer, RawStmathplan> q2 = studentData.getLatestMathPlanResponsesByPage(
                    MathPlanConstants.ONLY_RECOM_PROFILE);
            if (q2.isEmpty()) {
                result = EMathPlanStatus.SUBMITTED_MAJORS;
            } else {
                final Map<Integer, RawStmathplan> q3 = studentData.getLatestMathPlanResponsesByPage(
                        MathPlanConstants.EXISTING_PROFILE);
                if (q3.isEmpty()) {
                    result = EMathPlanStatus.CHECKED_ONLY_RECOMMENDATION;
                } else {
                    final Map<Integer, RawStmathplan> q4 = studentData.getLatestMathPlanResponsesByPage(
                            MathPlanConstants.INTENTIONS_PROFILE);
                    if (q4.isEmpty()) {
                        result = EMathPlanStatus.REVIEWED_EXISTING;
                    } else {
                        result = q4.containsKey(ONE) ? EMathPlanStatus.PLAN_COMPLETED_PLACEMENT_NOT_NEEDED
                                : EMathPlanStatus.PLAN_COMPLETED_PLACEMENT_NEEDED;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Queries for the list of majors for which a student has expressed interest, adds in the student's current declared
     * major, and reconstructs the "Math Plan" for a student.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @return the constructed math plan, which is based on student responses to (null if unable to construct a plan)
     */
    public static StudentMathPlan queryPlan(final Cache cache, final String studentId) {

        final StudentData studentData = cache.getStudent(studentId);

        final List<Major> majors = new ArrayList<>(10);
        final Collection<Integer> mainKeys = new ArrayList<>(10);
        boolean good = true;

        try {
            final Map<Integer, RawStmathplan> planResponses = studentData.getLatestMathPlanResponsesByPage(
                    MathPlanConstants.MAJORS_PROFILE);

            for (final Integer key : planResponses.keySet()) {
                final int code = key.intValue();
                final Major major = Majors.getMajorByNumericCode(code);
                if (major == null) {
                    Log.warning("No major found with code ", key, " (student ", studentId, ")");
                } else if (!majors.contains(major) && !mainKeys.contains(key)) {
                    majors.add(major);
                    mainKeys.add(key);
                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to query Math Plan 'majors' responses.", ex);
            good = false;
        }

        try {
            final RawStudent student = studentData.getStudentRecord();
            if (student != null) {
                final String declaredCode = student.programCode;
                if (declaredCode != null) {
                    if (!isProgramCodeIgnored(declaredCode)) {
                        final Major declared = Majors.getMajorByProgramCode(declaredCode);
                        if (declared == null) {
                            Log.warning("Failed to identify declared major '", declaredCode, "' for student ",
                                    studentId);
                        } else if (!majors.contains(declared)) {
                            final int code = declared.questionNumbers[0];
                            final Integer key = Integer.valueOf(code);
                            if (!mainKeys.contains(key)) {
                                majors.add(declared);
                                mainKeys.add(key);
                            }
                        }
                    }
                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to look up declared major.", ex);
            good = false;
        }

        StudentMathPlan result = null;

        if (good) {
            try {
                final StudentStatus stuStatus = new StudentStatus(cache, studentId);
                final Requirements requirements = majors.isEmpty() ? new Requirements() : new Requirements(majors,
                        stuStatus);

                result = new StudentMathPlan(majors, stuStatus, requirements);
            } catch (final SQLException ex) {
                Log.warning("Failed to calculate student status.", ex);
            }
        }

        return result;
    }

    /**
     * Stores a set of Math Plan responses.
     *
     * @param cache           the data cache
     * @param student         the student record
     * @param version         the page ID or "version" (typically one of the WLCM# IDs)
     * @param questions       the list of question numbers "survey_nbr"
     * @param answers         the corresponding list of answers (the same length as {@code questions}
     * @param now             the current date/time
     * @param loginSessionTag the login session tag to store in the response records
     * @throws SQLException if there is an error accessing the database
     */
    public static void storeMathPlanResponses(final Cache cache, final RawStudent student, final String version,
                                              final List<Integer> questions, final List<String> answers,
                                              final ZonedDateTime now, final long loginSessionTag) throws SQLException {

        final int numQuestions = questions.size();
        final int numAnswers = answers.size();
        final int numResponses = Math.min(numQuestions, numAnswers);

        final LocalDate examDt = now.toLocalDate();
        final LocalTime examTm = now.toLocalTime();
        final int minutes = TemporalUtils.minuteOfDay(examTm);
        final Integer finish = Integer.valueOf(minutes);

        final Long tag = Long.valueOf(loginSessionTag);
        final String appTerm = student.aplnTerm == null ? null : student.aplnTerm.shortString;

        // Query for any existing answers with the same date and finish time
        final List<RawStmathplan> latest = RawStmathplanLogic.queryLatestByStudentPage(cache, student.stuId, version);
        final Iterator<RawStmathplan> iter = latest.iterator();
        while (iter.hasNext()) {
            final RawStmathplan test = iter.next();
            if (examDt.equals(test.examDt) && finish.equals(test.finishTime)) {
                continue;
            }
            iter.remove();
        }

        for (int i = 0; i < numResponses; ++i) {
            final Integer question = questions.get(i);

            // See if there is an existing answer at the same time
            RawStmathplan existing = null;
            for (final RawStmathplan test : latest) {
                if (test.surveyNbr.equals(question)) {
                    existing = test;
                    break;
                }
            }

            final String answer = answers.get(i);
            if (answer == null) {
                // Old record had answer, new does not, so delete old record
                if (existing != null) {
                    RawStmathplanLogic.delete(cache, existing);
                }
            } else {
                final RawStmathplan rec = new RawStmathplan(student.stuId, student.pidm, appTerm, version, examDt,
                        question, answer, finish, tag);
                RawStmathplanLogic.insert(cache, rec);
            }
        }

        final StudentData studentData = cache.getStudent(student.stuId);
        studentData.forgetMathPlanResponses();
    }

    /**
     * Records a summary of the plan in the profile response table so advisers can view it quickly without having to
     * rebuilt it for each advisee.
     *
     * @param cache           the data cache
     * @param plan            the student's math plan
     * @param now             the date/time to consider "now"
     * @param loginSessionTag the login session tag
     * @throws SQLException if there is an error accessing the database
     */
    public static void recordPlan(final Cache cache, final StudentMathPlan plan, final ZonedDateTime now,
                                  final long loginSessionTag) throws SQLException {

        // Record only after student has checked the "only a recommendation" box
        final Map<Integer, RawStmathplan> done = plan.stuStatus.onlyRecResponses;

        if (!done.isEmpty()) {
            // NOTE: Historic data has 4 responses (pre-arrival, semester 1, semester 2, beyond).  This has been
            // simplified to record just what the student should do before arrival to be ready for semester 1

            final String value1 = plan.nextSteps.nextStep.planText;
            final String value2 = "(none)";
            final String value3 = "(none)";
            final String value4 = "(none)";

            final Map<Integer, RawStmathplan> existing = plan.stuStatus.planSummaryResponses;

            final RawStmathplan exist1 = existing.get(MathPlanConstants.ONE);
            final RawStmathplan exist2 = existing.get(MathPlanConstants.TWO);
            final RawStmathplan exist3 = existing.get(MathPlanConstants.THREE);
            final RawStmathplan exist4 = existing.get(MathPlanConstants.FOUR);

            final boolean shouldInsertNew =
                    exist1 == null || exist1.stuAnswer == null || !exist1.stuAnswer.equals(value1)
                    || exist2 == null || exist2.stuAnswer == null || !exist2.stuAnswer.equals(value2)
                    || exist3 == null || exist3.stuAnswer == null || !exist3.stuAnswer.equals(value3)
                    || exist4 == null || exist4.stuAnswer == null || !exist4.stuAnswer.equals(value4);

            if (shouldInsertNew) {
                final List<Integer> questions = new ArrayList<>(4);
                final List<String> answers = new ArrayList<>(4);

                questions.add(MathPlanConstants.ONE);
                questions.add(MathPlanConstants.TWO);
                questions.add(MathPlanConstants.THREE);
                questions.add(MathPlanConstants.FOUR);
                answers.add(value1);
                answers.add(value2);
                answers.add(value3);
                answers.add(value4);

                final RawStudent student = plan.stuStatus.student;

                storeMathPlanResponses(cache, student, MathPlanConstants.PLAN_PROFILE, questions, answers, now,
                        loginSessionTag);
            }
        }
    }
}
