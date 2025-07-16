package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.logic.placement.PlacementLogic;
import dev.mathops.db.logic.placement.PlacementStatus;
import dev.mathops.db.logic.tutorial.PrecalcTutorialLogic;
import dev.mathops.db.logic.tutorial.PrecalcTutorialStatus;
import dev.mathops.db.logic.course.PrerequisiteLogic;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
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
    public final Set<ERequirement> recommendedEligibility;

    /** The next step for the student. */
    public final ENextStep nextStep;

    /** Prerequisite logic. */
    public final PrerequisiteLogic prereqLogic;

    /** Placement status. */
    public final PlacementStatus placementStatus;

    /** Precalculus Tutorial Status. */
    private final PrecalcTutorialStatus precalcTutorialStatus;

    /** List of course IDs that the student can register for. */
    private final Set<String> canRegisterFor;

    /** List of course IDs that the student can register for that they don't already have. */
    private final Set<String> canRegisterForAndDoesNotHave;

    /** True if the student has a B- or higher in MATH 124. */
    private boolean bOrBetterIn124 = false;

    /** True if the student has a B- or higher in MATH 126. */
    private boolean bOrBetterIn126 = false;

    /**
     * Number of credits of core mathematics completed (placement results do not count toward completion of these
     * credits).
     */
    private double creditsOfCoreCompleted;

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
     * Gets the latest responses to the profile for the student.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @param pageId    the page ID
     * @return a map from question number to student response
     * @throws SQLException if there is an error accessing the database
     */
    public static Map<Integer, RawStmathplan> getMathPlanResponses(final Cache cache, final String studentId,
                                                                   final String pageId) throws SQLException {

        final List<RawStmathplan> latest = RawStmathplanLogic.queryLatestByStudentPage(cache, studentId, pageId);

        final Map<Integer, RawStmathplan> map = new HashMap<>(latest.size());
        for (final RawStmathplan response : latest) {
            map.put(response.surveyNbr, response);
        }

        return map;
    }

    /**
     * Constructs a new {@code MathPlanStudentData}.
     *
     * @param cache           the data cache
     * @param theStudent      the student record
     * @param logic           the math plan logic
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

        // Populate "what student has told us"
        this.majorProfileResponses = getMathPlanResponses(cache, studentId, MathPlanConstants.MAJORS_PROFILE);

        // Populate "existing work on record"
        this.viewedExisting = getMathPlanResponses(cache, studentId, MathPlanConstants.EXISTING_PROFILE)
                .containsKey(MathPlanConstants.ONE);

        this.checkedOnlyRecommendation = getMathPlanResponses(cache, studentId, MathPlanConstants.ONLY_RECOM_PROFILE)
                .containsKey(MathPlanConstants.ONE);

        // Populate "intentions" and "plan" responses
        this.intentionsResponses = getMathPlanResponses(cache, studentId, MathPlanConstants.INTENTIONS_PROFILE);

        // Populate "what student has done"
        this.completedCourses = logic.getCompletedCourses(studentId);
        this.liveTransferCredit = logic.getStudentTransferCredit(cache, studentId, true);

        this.placementCredit = RawMpeCreditLogic.queryByStudent(cache, studentId);

        // Gather math courses required by all selected majors (if any)
        this.majors = accumulateMajors(this.student.programCode, this.majorProfileResponses);

        this.prereqLogic = new PrerequisiteLogic(cache, studentId);

        this.placementStatus = new PlacementLogic(cache, studentId, this.student.aplnTerm, now).status;

        final LocalDate today = now.toLocalDate();
        this.precalcTutorialStatus = new PrecalcTutorialLogic(cache, studentId, today, this.prereqLogic).status;

        // Count the number of core mathematics course credits completed with a grade of C (2.000) or better.
        countCoreCredits(logic);

        // Build the set of courses that the student can register for right now
        this.canRegisterFor = getCoursesStudentCanRegisterFor();
        final int count = this.canRegisterFor.size();
        this.canRegisterForAndDoesNotHave = new HashSet<>(count);
        for (final String course : this.canRegisterFor) {
            if (!testForCompletedOrTransfer(course, 1.0f)) {
                this.canRegisterForAndDoesNotHave.add(course);
            }
        }

        // Ready to construct plan...
        this.recommendedEligibility = determineRecommendedEligibility();
        this.nextStep = createPlan();

        if (writeChanges) {
            recordPlan(cache, logic, now, studentId, loginSessionTag);
        }
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
     * Gets the list of majors.
     *
     * @return the list of majors
     */
    public List<Major> getMajors() {

        return this.majors;
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
     * Gets the set of courses (through Calculus I) that the student is eligible to register for right now but that they
     * don't already have credit for.
     *
     * @return the set of course IDs ({@code null} if none)
     */
    public Set<String> getCanRegisterForAndDoesNotHave() {

        return this.canRegisterForAndDoesNotHave;
    }

    /**
     * Gets the number of core credits the student has completed.
     *
     * @return the number of completed credits
     */
    public double getCreditsOfCoreCompleted() {

        return this.creditsOfCoreCompleted;
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

        final MajorsCurrent majors = MajorsCurrent.INSTANCE;

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
     * Determines the course for which it is "ideal" that the student be eligible in their first semester,
     */
    private Set<ERequirement> determineRecommendedEligibility() {

        final Set<ERequirement> set = EnumSet.noneOf(ERequirement.class);

        // Add all that are NOT "AUCC" - if that comes out to zero records, add AUCC at the end

        boolean hasPure160 = false;
        for (final Major major : this.majors) {
            if (major.idealEligibility.size() == 1 && major.idealEligibility.contains(ERequirement.M_160)) {
                hasPure160 = true;
            }
            set.addAll(major.idealEligibility);
        }

        if (hasPure160) {
            // If there is a major that NEEDS 160 exclusively, treat that as the only Calc requirement for the purpose
            // of making recommendations,
            set.remove(ERequirement.M_141);
            set.remove(ERequirement.M_155);
            set.remove(ERequirement.M_156);
        } else if (set.contains(ERequirement.M_156)) {
            // 156 only occurs in combination with 160, so if it's here, one selected course had that combination,
            // but if we get here, the student does NOT have an exclusive MATH 160 major selected, so we should base
            // their recommendations on 156 (and possibly 160)
            set.remove(ERequirement.M_141);
            set.remove(ERequirement.M_155);
        }

        set.remove(ERequirement.AUCC);
        if (set.isEmpty()) {
            set.add(ERequirement.AUCC);
        }

        return set;
    }

    /**
     * Creates the student's personalized plan.
     */
    private ENextStep createPlan() {

        final ENextStep result;

        if (this.recommendedEligibility.contains(ERequirement.M_160)) {
            if (this.recommendedEligibility.contains(ERequirement.M_155)) {
                // Base the plan on becoming eligible for MATH 155 or MATH 160.
                result = makePlan155Or160();
            } else if (this.recommendedEligibility.contains(ERequirement.M_156)) {
                // Base the plan on becoming eligible for MATH 156 or MATH 160.
                result = makePlan156Or160();
            } else {
                // Base the plan on becoming eligible for MATH 160.
                result = makePlan160();
            }
        } else if (this.recommendedEligibility.contains(ERequirement.M_156)) {
            // This catches combinations that have "156 or 155" - we want the student to get ready for 156 to keep
            // all their options open
            result = makePlan156();
        } else if (this.recommendedEligibility.contains(ERequirement.M_155)) {
            result = makePlan155();

            // Below here does not need Calculus (no majors need MATh 141 at the moment)
        } else if (this.recommendedEligibility.contains(ERequirement.M_126)) {
            if (this.recommendedEligibility.contains(ERequirement.M_124)) {
                result = makePlan124_126();
            } else {
                result = makePlan126();
            }
        } else if (this.recommendedEligibility.contains(ERequirement.M_124)) {
            if (this.recommendedEligibility.contains(ERequirement.M_125)) {
                result = makePlan124_125();
            } else {
                result = makePlan124();
            }
        } else if (this.recommendedEligibility.contains(ERequirement.M_125)) {
            result = makePlan125();
        } else if (this.recommendedEligibility.contains(ERequirement.M_118)) {
            result = makePlan118();
        } else if (this.recommendedEligibility.contains(ERequirement.M_117)) {
            result = makePlan117();
        } else {
            result = ENextStep.MSG_PLACEMENT_NOT_NEEDED;
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 155 or MATh 160.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan155Or160() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M155)
            || this.canRegisterFor.contains(RawRecordConstants.M160)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);
            boolean has124 = placedOutOf.contains(RawRecordConstants.MATH124)
                             || placementCreditFor.contains(RawRecordConstants.MATH124)
                             || testForCompletedOrTransfer(RawRecordConstants.M124, 1.0f);
            boolean has125 = placedOutOf.contains(RawRecordConstants.MATH125)
                             || placementCreditFor.contains(RawRecordConstants.MATH125)
                             || testForCompletedOrTransfer(RawRecordConstants.M125, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
                has124 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
                has124 = true;
                has125 = true;
            }

            if (has125) {
                if (has124) {
                    result = ENextStep.MSG_ALREADY_ELIGIBLE;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_124;
                }
            } else if (has124) {
                result = ENextStep.MSG_PLACE_OUT_125;
            } else if (has118) {
                result = ENextStep.MSG_PLACE_OUT_124_125;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118_124_125;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118_124_125;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 156 or MATh 160.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan156Or160() {

        // At the moment, the requirements for 156 and 160 are the same
        return makePlan160();
    }

    /**
     * Generates the student's plan if they should become eligible for MATh 160.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan160() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M160)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);
            boolean has125 = placedOutOf.contains(RawRecordConstants.MATH125)
                             || placementCreditFor.contains(RawRecordConstants.MATH125)
                             || testForCompletedOrTransfer(RawRecordConstants.M125, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
                has125 = true;
            }

            if (this.bOrBetterIn126) {
                if (this.bOrBetterIn124) {
                    result = ENextStep.MSG_ALREADY_ELIGIBLE;
                    // Else: Need the B in 124
                } else if (has118) {
                    result = ENextStep.MSG_PLACE_OUT_124;
                } else if (has117) {
                    result = ENextStep.MSG_PLACE_OUT_118_124;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_117_118_124;
                }
            } else if (this.bOrBetterIn124) {
                // Needs the B in 126
                if (has125) {
                    result = ENextStep.MSG_PLACE_OUT_126;
                } else if (has118) {
                    result = ENextStep.MSG_PLACE_OUT_125_126;
                } else if (has117) {
                    result = ENextStep.MSG_PLACE_OUT_118_125_126;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_117_118_125_126;
                }
                // Else: Needs the B in 124 and 126
            } else if (has118) {
                if (has125) {
                    result = ENextStep.MSG_PLACE_OUT_124_126;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_124_125_126;
                }
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118_124_125_126;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118_124_125_126;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 156.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan156() {

        // At the moment, the requirements for 156 and 160 are the same
        return makePlan160();
    }

    /**
     * Generates the student's plan if they should become eligible for MATh 155.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan155() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M155)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);
            boolean has124 = placedOutOf.contains(RawRecordConstants.MATH124)
                             || placementCreditFor.contains(RawRecordConstants.MATH124)
                             || testForCompletedOrTransfer(RawRecordConstants.M124, 1.0f);
            boolean has125 = placedOutOf.contains(RawRecordConstants.MATH125)
                             || placementCreditFor.contains(RawRecordConstants.MATH125)
                             || testForCompletedOrTransfer(RawRecordConstants.M125, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
                has124 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
                has124 = true;
                has125 = true;
            }

            if (has125) {
                if (has124) {
                    result = ENextStep.MSG_ALREADY_ELIGIBLE;
                } else if (has118) {
                    result = ENextStep.MSG_PLACE_OUT_124;
                } else if (has117) {
                    result = ENextStep.MSG_PLACE_OUT_118_124;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_117_118_124;
                }
            } else if (has124) {
                if (has118) {
                    result = ENextStep.MSG_PLACE_OUT_125;
                } else if (has117) {
                    result = ENextStep.MSG_PLACE_OUT_118_125;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_117_118_125;
                }
            } else if (has118) {
                result = ENextStep.MSG_PLACE_OUT_124_125;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118_124_125;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118_124_125;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 124 and MATH 126.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan124_126() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M124)
            && this.canRegisterFor.contains(RawRecordConstants.M126)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);
            boolean has125 = placedOutOf.contains(RawRecordConstants.MATH125)
                             || placementCreditFor.contains(RawRecordConstants.MATH125)
                             || testForCompletedOrTransfer(RawRecordConstants.M125, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
                has125 = true;
            }

            if (has118) {
                if (has125) {
                    result = ENextStep.MSG_ALREADY_ELIGIBLE;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_125;
                }
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118_125;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118_125;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 126 (but 124 is not needed).
     *
     * @return the student's next steps
     */
    private ENextStep makePlan126() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M126)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);
            boolean has125 = placedOutOf.contains(RawRecordConstants.MATH125)
                             || placementCreditFor.contains(RawRecordConstants.MATH125)
                             || testForCompletedOrTransfer(RawRecordConstants.M125, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
                has125 = true;
            }

            if (has125) {
                result = ENextStep.MSG_ALREADY_ELIGIBLE;
            } else if (has118) {
                result = ENextStep.MSG_PLACE_OUT_125;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118_125;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118_125;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 124 and MATH 125 (but not 126).
     *
     * @return the student's next steps
     */
    private ENextStep makePlan124_125() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M124)
            && this.canRegisterFor.contains(RawRecordConstants.M125)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
            }

            if (has118) {
                result = ENextStep.MSG_ALREADY_ELIGIBLE;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 124 (but not 125 or 126).
     *
     * @return the student's next steps
     */
    private ENextStep makePlan124() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M124)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
            }

            if (has118) {
                result = ENextStep.MSG_ALREADY_ELIGIBLE;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 125 (but not 124 or 126).
     *
     * @return the student's next steps
     */
    private ENextStep makePlan125() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M125)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);
            boolean has118 = placedOutOf.contains(RawRecordConstants.MATH118)
                             || placementCreditFor.contains(RawRecordConstants.MATH118)
                             || testForCompletedOrTransfer(RawRecordConstants.M118, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
                has118 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
                has118 = true;
            }

            if (has118) {
                result = ENextStep.MSG_ALREADY_ELIGIBLE;
            } else if (has117) {
                result = ENextStep.MSG_PLACE_OUT_118;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117_118;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 118.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan118() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M118)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            final Set<String> placedOutOf = this.placementStatus.placedOutOf;
            final Set<String> placementCreditFor = this.placementStatus.earnedCreditFor;
            boolean has117 = placedOutOf.contains(RawRecordConstants.MATH117)
                             || placementCreditFor.contains(RawRecordConstants.MATH117)
                             || testForCompletedOrTransfer(RawRecordConstants.M117, 1.0f);

            if (testForCompletedOrTransfer(RawRecordConstants.M120, 1.0f)) {
                has117 = true;
            }
            if (testForCompletedOrTransfer(RawRecordConstants.M127, 1.0f)) {
                has117 = true;
            }

            if (has117) {
                result = ENextStep.MSG_ALREADY_ELIGIBLE;
            } else {
                result = ENextStep.MSG_PLACE_OUT_117;
            }
        }

        return result;
    }

    /**
     * Generates the student's plan if they should become eligible for MATH 117.
     *
     * @return the student's next steps
     */
    private ENextStep makePlan117() {

        final ENextStep result;

        if (this.canRegisterFor.contains(RawRecordConstants.M117)) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else {
            result = ENextStep.MSG_PLACE_INTO_117;
        }

        return result;
    }

    /**
     * Records the plan in the profile response table so advisers can view it quickly without having to rebuilt it for
     * each advisee.
     *
     * @param cache           the data cache
     * @param logic           the logic object
     * @param now             the date/time to consider "now"
     * @param stuId           the student ID
     * @param loginSessionTag the login session tag
     * @throws SQLException if there is an error accessing the database
     */
    public void recordPlan(final Cache cache, final MathPlanLogic logic, final ZonedDateTime now,
                           final String stuId, final long loginSessionTag) throws SQLException {

        // Record only after student has checked the "only a recommendation" box
        final Map<Integer, RawStmathplan> done = getMathPlanResponses(cache, stuId,
                MathPlanConstants.ONLY_RECOM_PROFILE);

        if (!done.isEmpty()) {
            // NOTE: Historic data has 4 responses (pre-arrival, semester 1, semester 2, beyond).  This has been
            // simplified to record just what the student should do before arrival to be ready for semester 1
            final String value1 = this.nextStep.planText;
            final String value2 = "(none)";
            final String value3 = "(none)";
            final String value4 = "(none)";

            final Map<Integer, RawStmathplan> existing = getMathPlanResponses(cache, stuId,
                    MathPlanConstants.PLAN_PROFILE);

            final RawStmathplan exist1 = existing.get(MathPlanConstants.ONE);
            final RawStmathplan exist2 = existing.get(MathPlanConstants.TWO);
            final RawStmathplan exist3 = existing.get(MathPlanConstants.THREE);
            final RawStmathplan exist4 = existing.get(MathPlanConstants.FOUR);

            final boolean shouldInsertNew =
                    exist1 == null
                    || exist1.stuAnswer == null || !exist1.stuAnswer.equals(value1) || exist2 == null
                    || exist2.stuAnswer == null || !exist2.stuAnswer.equals(value2) || exist3 == null
                    || exist3.stuAnswer == null || !exist3.stuAnswer.equals(value3) || exist4 == null
                    || exist4.stuAnswer == null || !exist4.stuAnswer.equals(value4);

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

                logic.storeMathPlanResponses(cache, this.student, MathPlanConstants.PLAN_PROFILE, questions,
                        answers,
                        now, loginSessionTag);
            }
        }
    }

    /**
     * Tests if the student has a B- or higher in MATH 124.
     *
     * @return true if the student has a B- or higher in MATH 124
     */
    public boolean hasBOrBetterIn124() {

        return this.bOrBetterIn124;
    }

    /**
     * Tests if the student has a B- or higher in MATH 126.
     *
     * @return true if the student has a B- or higher in MATH 126
     */
    public boolean hasBOrBetterIn126() {

        return this.bOrBetterIn126;
    }
}
