package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.old.logic.PlacementLogic;
import dev.mathops.db.old.logic.PlacementStatus;
import dev.mathops.db.old.logic.PrecalcTutorialLogic;
import dev.mathops.db.old.logic.PrecalcTutorialStatus;
import dev.mathops.db.old.logic.PrerequisiteLogic;
import dev.mathops.db.old.logic.mathplan.MathPlanLogic;
import dev.mathops.db.old.rawlogic.RawMpeCreditLogic;
import dev.mathops.db.old.rawlogic.RawStmathplanLogic;
import dev.mathops.db.old.rawrecord.RawCourse;
import dev.mathops.db.old.rawrecord.RawMpeCredit;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStudent;
import dev.mathops.db.rec.LiveCsuCredit;
import dev.mathops.db.rec.LiveTransferCredit;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All data related to a student used by this site. Student data is queried once and cached for a period of time and
 * re-used on subsequent page loads, since this data is unlikely to change rapidly. If an update is made to the data by
 * the site (like the list of majors of interest), the cache is updated at the same time.
 */
public final class MathPlanStudentData {

    /** Retain data for 60 seconds (adjust as needed). */
    private static final long RETENTION_MS = 60 * 1000;

    /** The expiration timestamp of this cached record. */
    private final long expiry;

    /** The student record. */
    public final RawStudent student;

    /** The student's responses to the set of majors of interest. */
    private final Map<Integer, RawStmathplan> majorProfileResponses;

    /** True if student has viewed their existing record. */
    public final boolean viewedExisting;

    /** True if student has checked the "This is only a recommendation" checkbox. */
    public final boolean checkedOnlyRecommendation;

    /** The student's responses to their intentions. */
    private final Map<Integer, RawStmathplan> intentionsResponses;

    /** The courses the student has already completed at CSU. */
    private final List<LiveCsuCredit> completedCourses;

    /** The student's live transfer credit. */
    private final List<LiveTransferCredit> liveTransferCredit;

    /** The student's placement credit. */
    private final List<RawMpeCredit> placementCredit;

    /** The student's selected majors. */
    private final List<Major> majors;

    /** The course we will recommend the student try to become eligible for. */
    public final EEligibility recommendedEligibility;

    /** Prerequisite logic. */
    public final PrerequisiteLogic prereqLogic;

    /** Placement status. */
    public final PlacementStatus placementStatus;

    /** Precalculus Tutorial Status. */
    private final PrecalcTutorialStatus precalcTutorialStatus;

    /** List of course IDs that the student can register for. */
    private final Set<String> canRegisterFor;

    /** True if the student has a B- or higher in MATH 124. */
    private boolean bOrBetterIn124 = false;

    /** True if the student has a B- or higher in MATH 126. */
    private boolean bOrBetterIn126 = false;

    /**
     * Number of credits of core mathematics completed (placement results do not count toward completion of these
     * credits).
     */
    private double creditsOfCoreCompleted;

    /** The list of possible next steps for the student, in order of preference. */
    private final List<ENextStep> nextSteps;

    /**
     * Tests whether a student has completed the Math Plan, and returns the student ID if so.
     *
     * @param cache the data cache
     * @param pidm  the student's PIDM
     * @return the student ID if the student has completed the Math Plan; null if not
     * @throws SQLException if there is an error accessing the database
     */
    public static String hasCompletedMathPlan(final Cache cache, final int pidm) throws SQLException {

        final Integer pidmObj = Integer.valueOf(pidm);

        final List<RawStmathplan> responses = RawStmathplanLogic.queryLatestByStudentPage(cache, pidmObj, "WLCM5");
        String studentId = null;
        if (!responses.isEmpty()) {
            studentId = ((RawStmathplan) responses.getFirst()).stuId;
        }

        return studentId;
    }

    /**
     * Constructs a new {@code MathPlanStudentData}.
     *
     * @param cache           the data cache
     * @param theStudent      the student record
     * @param logic           the logic object to use to populate student data
     * @param now             the date/time to consider "now"
     * @param loginSessionTag the login session tag
     * @param writeChanges    {@code true} to write profile value changes (used when the student is accessing the site);
     *                        {@code false} to skip writing changes (used when an administrator or adviser is acting as
     *                        a student)
     * @throws SQLException if there is an error accessing the database
     */
    public MathPlanStudentData(final Cache cache, final RawStudent theStudent, final MathPlanLogic logic,
                               final ZonedDateTime now, final long loginSessionTag, final boolean writeChanges)
            throws SQLException {

        this.expiry = System.currentTimeMillis() + RETENTION_MS;

        this.student = theStudent;
        final String studentId = theStudent.stuId;

        // Log.info(Res.fmt(Res.BUILDING_STU_DATA, this.studentId));

        // Populate "what student has told us"
        this.majorProfileResponses =
                MathPlanLogic.getMathPlanResponses(cache, studentId, MathPlanConstants.MAJORS_PROFILE);

        // Populate "existing work on record"
        this.viewedExisting = MathPlanLogic
                .getMathPlanResponses(cache, studentId, MathPlanConstants.EXISTING_PROFILE)
                .containsKey(Integer.valueOf(1));

        this.checkedOnlyRecommendation = MathPlanLogic
                .getMathPlanResponses(cache, studentId, MathPlanConstants.ONLY_RECOM_PROFILE)
                .containsKey(Integer.valueOf(1));

        // Populate "intentions"
        this.intentionsResponses = MathPlanLogic.getMathPlanResponses(cache, studentId,
                MathPlanConstants.INTENTIONS_PROFILE);

        // Populate "what student has done"
        this.completedCourses = logic.getCompletedCourses(studentId);
        this.liveTransferCredit = logic.getStudentTransferCredit(cache, studentId, true);

        this.placementCredit = RawMpeCreditLogic.queryByStudent(cache, studentId);

        // Gather math courses required by all selected majors (if any)
        this.majors = accumulateMajors(this.student.programCode, this.majorProfileResponses);

        this.prereqLogic = new PrerequisiteLogic(cache, studentId);

        this.placementStatus = new PlacementLogic(cache, studentId, this.student.aplnTerm, now).status;

        this.precalcTutorialStatus = new PrecalcTutorialLogic(cache, studentId, now.toLocalDate(),
                this.prereqLogic).status;

        // Count the number of core mathematics course credits completed with a grade of C (2.000) or better.
        countCoreCredits(logic);

        // Build the set of courses that the student can register for right now
        this.canRegisterFor = getCoursesStudentCanRegisterFor();

        // Ready to construct plan...
        this.nextSteps = new ArrayList<>(5);
        this.recommendedEligibility = createPlan(cache, logic);
//        if (writeChanges) {
//            recordPlan(cache, logic, now, studentId, loginSessionTag);
//        }
//
//        recordAnalytics();
    }

    /**
     * Counts the number of core mathematics credits the student has completed with a grade of C (2.000) or higher
     * (placing out of a core course does not count, but earning credit for the course on the Challenge exam does
     * count).
     *
     * <p>
     * Note that we skip 550 (placement credit) sections in the completed courses list since those are also reflected as
     * rows with placed='C' in the placement credit list (where dt_cr_refused is null). So we do count such placement
     * records. This avoids the delay that the 550 section can have in getting onto the student's transcript.
     *
     * @param logic the site logic
     */
    private void countCoreCredits(final MathPlanLogic logic) {

        final List<String> coreCourses = Arrays.asList(MathPlanConstants.M_101, MathPlanConstants.M_105,
                MathPlanConstants.S_100, MathPlanConstants.M_117, MathPlanConstants.M_118, MathPlanConstants.M_120,
                MathPlanConstants.M_124, MathPlanConstants.M_125, MathPlanConstants.M_126, MathPlanConstants.M_127,
                MathPlanConstants.S_100, MathPlanConstants.S_201, MathPlanConstants.S_204, MathPlanConstants.F_200,
                MathPlanConstants.M_141, MathPlanConstants.M_155, MathPlanConstants.M_160, MathPlanConstants.M_161,
                MathPlanConstants.M_255);

        final Map<String, RawCourse> courseMap = logic.getCourses();
        final Collection<String> markedAsCompleted = new ArrayList<>(3);

        for (final LiveCsuCredit completed : this.completedCourses) {
            final String courseId = completed.courseId;
            if (markedAsCompleted.contains(courseId)) {
                continue;
            }

            // NOTE: Section 550 still satisfies core AUCC 1B.

            if (completed.getGradeGpa() != null && completed.getGradeGpa().doubleValue() > 1.9
                && coreCourses.contains(courseId)) {

                final RawCourse course = courseMap.get(courseId);
                if (course != null && course.nbrCredits != null) {
                    markedAsCompleted.add(courseId);
                    this.creditsOfCoreCompleted += course.nbrCredits.doubleValue();
                }
            }
        }

        for (final LiveTransferCredit transfer : this.liveTransferCredit) {
            final String id = transfer.courseId;

            // Transfer IDs look like "MATH117" or "MATH1++1B"

            if (markedAsCompleted.contains(id)) {
                continue;
            }

            if (transfer.transferred.endsWith("++1B")) {
                this.creditsOfCoreCompleted += transfer.credits.doubleValue();
            } else {
                final Float gpa = transfer.getGradeGpa();
                if (gpa != null && gpa.doubleValue() > 1.9 && coreCourses.contains(id)) {
                    markedAsCompleted.add(id);
                    this.creditsOfCoreCompleted += transfer.credits.doubleValue();
                }
            }
        }
    }

    /**
     * Creates a list of courses for which the student is currently eligible to register. Courses up through Calculus II
     * are considered, since that represents all courses that could occur as the student's first course in a degree
     * program.
     *
     * @return the set of course IDs
     */
    private Set<String> getCoursesStudentCanRegisterFor() {

        final Set<String> result = new HashSet<>(10);

        // Build a list of courses that the student can register for

        // All students can register for MATH 101, MATH 1051
        result.add("M 101");
        result.add("M 105");
        result.add("STAT 100");
        result.add("STAT 201");
        result.add("STAT 204");

        boolean has160 = false;

        // First, get courses they can register for due to placement results
        for (final RawMpeCredit cr : this.placementCredit) {
            final String courseId = cr.course;

            if (RawRecordConstants.M100C.equals(courseId) || "M 100A".equals(courseId)) {
                result.add(RawRecordConstants.M117);
            } else if (RawRecordConstants.M117.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
            } else if (RawRecordConstants.M118.equals(courseId) || "M 120".equals(courseId)
                       || "M 120A".equals(courseId) || "M 121".equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add("M 141");
            } else if (RawRecordConstants.M124.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add("M 141");
                this.bOrBetterIn124 = true;
            } else if (RawRecordConstants.M125.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
            } else if (RawRecordConstants.M126.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
                result.add("M 160");
                this.bOrBetterIn126 = true;
            }
        }

        // Get courses they can register for due to transfer credit results
        for (final LiveTransferCredit cr : this.liveTransferCredit) {
            final String id = cr.courseId;
            final Float gpa = cr.getGradeGpa();

            if ("M 002".equals(id)) {
                result.add(RawRecordConstants.M117);
            } else if (RawRecordConstants.M117.equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
            } else if (RawRecordConstants.M118.equals(id) || "M 120".equals(id) || "M 120A".equals(id)
                       || "M 121".equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add("M 141");
            } else if (RawRecordConstants.M124.equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add("M 141");
                if (gpa != null && gpa.floatValue() > 2.9f) {
                    this.bOrBetterIn124 = true;
                }
            } else if (RawRecordConstants.M125.equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
            } else if (RawRecordConstants.M126.equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
                if (gpa != null && gpa.floatValue() > 2.9f) {
                    this.bOrBetterIn126 = true;
                }
            } else if ("M 160".equals(id)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
                result.add("M 160");
                has160 = true;
            }
        }

        // Get courses they can register for due to completed CSU courses results
        for (final LiveCsuCredit cr : this.completedCourses) {
            final String courseId = cr.courseId;

            if (RawRecordConstants.M117.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
            } else if (RawRecordConstants.M118.equals(courseId) || "M 120".equals(courseId)
                       || "M 120A".equals(courseId) || "M 121".equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add("M 141");
            } else if (RawRecordConstants.M124.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add("M 141");
                if (cr.getGradeGpa() != null && cr.getGradeGpa().floatValue() > 2.9f) {
                    this.bOrBetterIn124 = true;
                }
            } else if (RawRecordConstants.M125.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
            } else if (RawRecordConstants.M126.equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
                if (cr.getGradeGpa() != null && cr.getGradeGpa().floatValue() > 2.9f) {
                    this.bOrBetterIn126 = true;
                }
            } else if ("M 160".equals(courseId)) {
                result.add(RawRecordConstants.M117);
                result.add(RawRecordConstants.M118);
                result.add(RawRecordConstants.M124);
                result.add(RawRecordConstants.M125);
                result.add(RawRecordConstants.M126);
                result.add("M 141");
                result.add("M 155");
                result.add("M 160");
                has160 = true;
            }
        }

        if (this.bOrBetterIn124 && this.bOrBetterIn126) {
            result.add("M 160");
        }

        if (has160 && this.bOrBetterIn124) {
            result.add("M 161");
        }

        // Look for completed/transfer Precalculus courses and check grades as needed to register
        // for Calculus
        if (!result.contains("M 141")
            && testForCompletedOrTransfer(RawRecordConstants.M118, 2.0f)) {
            result.add("M 141");
        }

        if (!result.contains("M 155")
            && testForCompletedOrTransfer(RawRecordConstants.M124, 2.0f)
            && testForCompletedOrTransfer(RawRecordConstants.M125, 2.0f)) {
            result.add("M 155");
        }

        if (!result.contains("M 160")
            && testForCompletedOrTransfer(RawRecordConstants.M124, 3.0f)
            && testForCompletedOrTransfer(RawRecordConstants.M124, 3.0f)) {

            result.add("M 160");
        }

        if (!result.contains("M 161")
            && testForCompletedOrTransfer("M 160", 1.0f)
            && testForCompletedOrTransfer(RawRecordConstants.M124, 3.0f)) {

            result.add("M 161");
        }

        return result;
    }

    /**
     * Tests whether the student has completed or transferred a course with a certain minimum grade.
     *
     * @param course   the course
     * @param minGrade the minimum grade
     * @return {@code true} if student has transfer credit for the course with the required grade or has completed the
     *         course with the required grade
     */
    private boolean testForCompletedOrTransfer(final String course, final float minGrade) {

        boolean hasCourse = false;

        // Transfer credit is more likely, so try that first
        for (final LiveTransferCredit rec : this.liveTransferCredit) {
            final String id = rec.courseId;
            final Float gpa = rec.getGradeGpa();

            if (gpa != null && course.equals(id) && gpa.floatValue() >= minGrade) {
                hasCourse = true;
                break;
            }
        }

        if (!hasCourse) {
            for (final LiveCsuCredit rec : this.completedCourses) {
                final String courseId = rec.courseId;

                if (rec.getGradeGpa() != null && course.equals(courseId)
                    && rec.getGradeGpa().floatValue() >= minGrade) {
                    hasCourse = true;
                    break;
                }
            }
        }

        return hasCourse;
    }

    /**
     * Tests whether this entry has expired.
     *
     * @return {@code true} if the entry has expired
     */
    public boolean isExpired() {

        return this.expiry < System.currentTimeMillis();
    }

    /**
     * Gets the student's current profile responses.
     *
     * @return the profile responses
     */
    public Map<Integer, RawStmathplan> getMajorProfileResponses() {

        return this.majorProfileResponses;
    }

    /**
     * Gets the intentions profile responses in the student's math plan.
     *
     * @return the profile responses
     */
    public Map<Integer, RawStmathplan> getIntentions() {

        return this.intentionsResponses;
    }

    /**
     * Gets the student's completed course list.
     *
     * @return the completed course list
     */
    public List<LiveCsuCredit> getCompletedCourses() {

        return this.completedCourses;
    }

    /**
     * Gets the student's transfer credit list.
     *
     * @return the transfer credit list
     */
    public List<LiveTransferCredit> getLiveTransferCredit() {

        return this.liveTransferCredit;
    }

    /**
     * Counts transfer credits.
     *
     * @return the total number of transfer credits.
     */
    double countTransferCredits() {

        double total = 0.0;

        for (final LiveTransferCredit rec : this.liveTransferCredit) {
            if (rec.credits != null) {
                total += rec.credits.doubleValue();
            }
        }

        return total;
    }

    /**
     * Gets the student's placement credit list.
     *
     * @return the placement credit list
     */
    public List<RawMpeCredit> getPlacementCredit() {

        return this.placementCredit;
    }

    /**
     * Gets the set of courses (through Calculus I) that the student is eligible to register for right now.
     *
     * @return the set of course IDs ({@code null} if none)
     */
    public Set<String> getCanRegisterFor() {

        return this.canRegisterFor;
    }

    /**
     * Gets the number of core credits the student has completed.
     *
     * @return the number of completed credits
     */
    double getCreditsOfCoreCompleted() {

        return this.creditsOfCoreCompleted;
    }

    /**
     * Gets the list of next steps for the student.
     *
     * @return the list of next steps
     */
    public List<ENextStep> getNextSteps() {

        return this.nextSteps;
    }

    /**
     * Gathers the list of majors for which the student has expressed interest.
     *
     * @param declaredProgramCode the student's declared program code
     * @param curResponses        the current student responses (empty if not yet responded)
     * @return a list containing all math requirements for all selected majors
     */
    private static List<Major> accumulateMajors(final String declaredProgramCode,
                                                final Map<Integer, RawStmathplan> curResponses) {

        final Majors majors = Majors.INSTANCE;

        final List<Major> result = new ArrayList<>(10);

        final Major declaredMajor = majors.getMajor(declaredProgramCode);
        if (declaredMajor != null) {
            result.add(declaredMajor);
        }

        for (final Major major : majors.getMajors()) {
            boolean includedInResponses = false;
            for (final int q : major.questionNumbers) {
                final Integer qObj = Integer.valueOf(q);
                final RawStmathplan curResp = curResponses.get(qObj);
                if (curResp != null) {
                    includedInResponses = true;
                    break;
                }
            }
            if (includedInResponses && !result.contains(major)) {
                result.add(major);
            }
        }

        return result;
    }

    /**
     * Creates the student's personalized plan.
     *
     * @param cache the data cache
     * @param logic the site logic
     * @throws SQLException if there is an error accessing the database
     */
    private EEligibility createPlan(final Cache cache, final MathPlanLogic logic) throws SQLException {

        // Determine the ideal eligibility based on the students list of selected majors
        EEligibility highestMath = EEligibility.AUCC;
        for (final Major major : this.majors) {
            if (major.idealEligibility.level > highestMath.level) {
                highestMath = major.idealEligibility;
            }
        }

        // See if they are already eligible
        final boolean alreadyEligible = switch (highestMath) {
            case AUCC -> true;
            case M_117_120 -> this.canRegisterFor.contains(RawRecordConstants.M117);
            case M_118 -> this.canRegisterFor.contains(RawRecordConstants.M118);
            case M_125 -> this.canRegisterFor.contains(RawRecordConstants.M125);
            case M_155, M_155_160 -> this.canRegisterFor.contains(RawRecordConstants.M155);
            case M_156_160 -> this.canRegisterFor.contains(RawRecordConstants.M156);
            case M_160 -> this.canRegisterFor.contains(RawRecordConstants.M160);
        };

        if (highestMath.level > EEligibility.AUCC.level) {
            if (alreadyEligible) {
                this.nextSteps.add(ENextStep.MSG_ALREADY_ELIGIBLE);
            } else if (highestMath == EEligibility.M_117_120) {
                this.nextSteps.add(ENextStep.MSG_PLACE_INTO_117);
            } else if (highestMath == EEligibility.M_118) {
                if (this.canRegisterFor.contains(RawRecordConstants.M117)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117);
                } else {
                    this.nextSteps.add(ENextStep.MSG_PLACE_INTO_118);
                }
            } else if (highestMath == EEligibility.M_125) {
                if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_118);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M117)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117_118);
                } else {
                    this.nextSteps.add(ENextStep.MSG_PLACE_INTO_125);
                }
            } else if (highestMath == EEligibility.M_155 || highestMath == EEligibility.M_155_160) {
                if (this.canRegisterFor.contains(RawRecordConstants.M125)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_125);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_118_125);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M117)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117_118_125);
                } else {
                    this.nextSteps.add(ENextStep.MSG_PLACE_INTO_155);
                }
                // What remains is MATH 156 and MATH, and student does NOT have a B- or better in both 124 and 126
            } else if (this.bOrBetterIn124) {
                // What's needed is the B- in 126
                if (this.canRegisterFor.contains(RawRecordConstants.M126)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_126);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M125)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_125_126);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_118_125_126);
                } else {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117_118_125_126);
                }
            } else if (this.bOrBetterIn126) {
                // What's needed is the B- in 124
                if (this.canRegisterFor.contains(RawRecordConstants.M124)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_124);
                } else if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_118_124);
                } else {
                    this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117_118_124);
                }
                // Else: Needs both 124 and 126
            } else if (this.canRegisterFor.contains(RawRecordConstants.M126)) {
                this.nextSteps.add(ENextStep.MSG_PLACE_OUT_124_126);
            } else if (this.canRegisterFor.contains(RawRecordConstants.M125)) {
                this.nextSteps.add(ENextStep.MSG_PLACE_OUT_124_125_126);
            } else if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
                this.nextSteps.add(ENextStep.MSG_PLACE_OUT_118_124_125_126);
            } else {
                this.nextSteps.add(ENextStep.MSG_PLACE_OUT_117_118_124_125_126);
            }
        } else {
            this.nextSteps.add(ENextStep.MSG_PLACEMENT_NOT_NEEDED);
        }

        return highestMath;
    }
}
