package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawMpeCredit;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStmpe;
import dev.mathops.db.old.rawrecord.RawStudent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A container for the student's status, including all course credit and transfer credit on record, and all placement
 * earned.
 */
public final class StudentStatus {

    /** The student record (used to examine declared major). */
    public final RawStudent student;

    /**
     * The student's transfer credit.  NOTE: at this time, MATH 120 / MATH 127 credit is being stored here so we can
     * account for it.
     */
    public final List<RawFfrTrns> transferCredit;

    /** The student's placement credit. */
    public final List<RawMpeCredit> placementCredit;

    /** The student's completed precalculus courses. */
    public final List<RawStcourse> completedCourses;

    /** The student's latest responses to the "Majors" item. */
    public final Map<Integer, RawStmathplan> majorsResponses;

    /** The student's latest responses to the "Plan Summary" item. */
    public final Map<Integer, RawStmathplan> planSummaryResponses;

    /** The student's latest responses to the "Only a Recommendation" item. */
    public final Map<Integer, RawStmathplan> onlyRecResponses;

    /** The student's latest responses to the "Existing Work" item. */
    public final Map<Integer, RawStmathplan> existingWorkResponses;

    /** The student's latest responses to the "Intentions" item. */
    public final Map<Integer, RawStmathplan> intentionsResponses;

    /** The student's latest responses to the "Review Materials" item. */
    public final Map<Integer, RawStmathplan> reviewedResponses;

    /** The student's latest responses to the "Placement Outcomes" item. */
    public final Map<Integer, RawStmathplan> resultsResponses;

    /** The number of placement attempts on record. */
    public final int numPlacementAttempts;

    /**
     * Constructs a {@code StudentStatus}.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @throws SQLException if there is an error accessing the database
     */
    StudentStatus(final Cache cache, final String studentId) throws SQLException {

        final StudentData studentData = cache.getStudent(studentId);

        this.student = studentData.getStudentRecord();
        this.transferCredit = studentData.getTransferCredit();
        this.placementCredit = studentData.getPlacementCredit();
        this.completedCourses = studentData.getCompletedRegistrations();

        final List<RawStmpe> placementAttempts = studentData.getLegalPlacementAttempts();
        this.numPlacementAttempts = placementAttempts.size();

        this.majorsResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.MAJORS_PROFILE);
        this.planSummaryResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.PLAN_PROFILE);
        this.onlyRecResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.ONLY_RECOM_PROFILE);
        this.existingWorkResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.EXISTING_PROFILE);
        this.intentionsResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.INTENTIONS_PROFILE);
        this.reviewedResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.REVIEWED_PROFILE);
        this.resultsResponses = studentData.getLatestMathPlanResponsesByPage(MathPlanConstants.CHECKED_RESULTS_PROFILE);
    }

    /**
     * Gets a list of course IDs for which the student has completed the course or has transfer credit.
     *
     * @return the list of course IDs
     */
    public Collection<ECourse> getCompleted() {

        final Collection<String> courseIds = new ArrayList<>(10);
        for (final RawFfrTrns transfer : this.transferCredit) {
            courseIds.add(transfer.course);
        }
        for (final RawStcourse finished : this.completedCourses) {
            courseIds.add(finished.course);
        }

        return ECourse.courseIdListToECourseSet(courseIds);
    }

    /**
     * Tests whether the student has completed Math Placement at least one time.
     *
     * @return true if Math Placement has been completed
     */
    public boolean isPlacementCompleted() {

        return this.numPlacementAttempts > 0;
    }

    /**
     * Gets a list of course IDs for which the student has completed the course or has transfer credit with a grade of
     * B- or higher.
     *
     * @return the list of course IDs
     */
    public Collection<ECourse> getCompletedBOrHigher() {

        final Collection<String> courseIds = new HashSet<>(6);

        for (final RawFfrTrns transfer : this.transferCredit) {
            final String grade = transfer.grade;
            if (grade == null || grade.startsWith("A") || grade.startsWith("B")
                || grade.startsWith("TA") || grade.startsWith("TB")) {
                courseIds.add(transfer.course);
            }
        }
        for (final RawStcourse finished : this.completedCourses) {
            final String grade = finished.courseGrade;
            if (grade == null || grade.startsWith("A") || grade.startsWith("B")
                || grade.startsWith("TA") || grade.startsWith("TB")) {
                courseIds.add(finished.course);
            }
        }

        return ECourse.courseIdListToECourseSet(courseIds);
    }

    /**
     * Counts the number of credits of AUCC core that have been completed.
     *
     * @return the number of credits
     */
    public int getCreditsOfCoreCompleted() {

        // FIXME: We are not tracking credit count in FFR_TRNS - sometimes a different number of credits are used
        //  in transfer courses, and we really need to have that data to do an accurate count here.

        // FIXME: This does not track CSU completions of Calculus courses or non-MATH core courses.

        int credits = 0;

        final EnumSet<ECourse> done = EnumSet.noneOf(ECourse.class);

        for (final RawStcourse course : this.completedCourses) {
            final ECourse match = findMatch(course.course);

            if (match != null && !done.contains(match)) {
                credits += match.credits;
                done.add(match);
            }
        }

        for (final RawFfrTrns transfer : this.transferCredit) {
            final ECourse match = findMatch(transfer.course);

            if (match != null && !done.contains(match)) {
                credits += match.credits;
                done.add(match);
            }
        }

        return credits;
    }

    /**
     * Finds the {@code ECourse} corresponding to a course ID.
     *
     * @param id the course ID
     * @return the {@code ECourse}, null if none found
     */
    private static ECourse findMatch(final String id) {

        ECourse match = null;

        if (RawRecordConstants.M117.equals(id)) {
            match = ECourse.M_117;
        } else if (RawRecordConstants.M118.equals(id)) {
            match = ECourse.M_118;
        } else if (RawRecordConstants.M124.equals(id)) {
            match = ECourse.M_124;
        } else if (RawRecordConstants.M125.equals(id)) {
            match = ECourse.M_125;
        } else if (RawRecordConstants.M126.equals(id)) {
            match = ECourse.M_126;
        } else if (RawRecordConstants.M120.equals(id)) {
            match = ECourse.M_120;
        } else if (RawRecordConstants.M127.equals(id)) {
            match = ECourse.M_127;
        }

        return match;
    }

    /**
     * Tests whether a specific precalculus course has been completed.
     *
     * @param course the course
     * @return true if the course was completed
     */
    public boolean hasCompleted(final ECourse course) {

        final Collection<ECourse> completed = getCompleted();
        return completed.contains(course);
    }

    /**
     * Tests whether a specific precalculus course has been completed with a B- or higher grade.
     *
     * @param course the course
     * @return true if the course was completed with a B- or higher grade
     */
    public boolean hasCompletedWithB(final ECourse course) {

        final Collection<ECourse> completed = getCompletedBOrHigher();
        return completed.contains(course);
    }

    /**
     * Tests whether the student is eligible for a specific course.
     *
     * @param course the course
     * @return true if the student has met the prerequisites to take the course
     */
    public boolean isEligible(final ECourse course) {

        boolean eligible = false;

        if (course == ECourse.M_101) {
            eligible = true;
        } else if (course == ECourse.M_117 || course == ECourse.M_120) {
            eligible = isEligibleFor117();
        } else if (course == ECourse.M_118) {
            eligible = isEligibleFor118();
        } else if (course == ECourse.M_124) {
            eligible = isEligibleFor124();
        } else if (course == ECourse.M_125) {
            eligible = isEligibleFor125();
        } else if (course == ECourse.M_126) {
            eligible = isEligibleFor126();
        } else if (course == ECourse.M_141) {
            eligible = isEligibleFor141();
        } else if (course == ECourse.M_155) {
            eligible = isEligibleFor155();
        } else if (course == ECourse.M_156 || course == ECourse.M_160) {
            eligible = isEligibleFor156or160();
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 117.
     *
     * @return true if the student is eligible for MATH 117
     */
    boolean isEligibleFor117() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 100C".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();

            for (final ECourse id : completed) {
                if ("M 117".equals(id) || "MATH 117".equals(id)
                    || "M 118".equals(id) || "MATH 118".equals(id)
                    || "M 124".equals(id) || "MATH 124".equals(id)
                    || "M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 002".equals(id) || "MATH 002".equals(id)
                    || "M 141".equals(id) || "MATH 141".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)
                    || "M 229".equals(id) || "MATH 229".equals(id)
                    || "M 269".equals(id) || "MATH 269".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests whether a student has placed out of a course.
     *
     * @param courseId the course ID
     * @return true if the student has placed out
     */
    private boolean placedOut(final String courseId) {

        boolean placed = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if (courseId.equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                placed = true;
                break;
            }
        }

        return placed;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 117.
     *
     * @return true if student has some form of credit for MATH 117
     */
    boolean has117() {

        boolean has117 = placedOut("M 117");

        if (!has117) {
            final Collection<ECourse> completed = getCompleted();
            has117 = completed.contains(ECourse.M_117);
        }

        return has117;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 118.
     *
     * @return true if student has some form of credit for MATH 118
     */
    boolean has118() {

        boolean has118 = placedOut("M 118");

        if (!has118) {
            final Collection<ECourse> completed = getCompleted();
            has118 = completed.contains(ECourse.M_118);
        }

        return has118;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 124.
     *
     * @param bOrHigher true to require a B- or higher grade
     * @return true if student has some form of credit for MATH 124
     */
    boolean has124(final boolean bOrHigher) {

        boolean has124 = placedOut("M 124");

        if (!has124) {
            final Collection<ECourse> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();
            has124 = completed.contains(ECourse.M_124);
        }

        return has124;
    }

    /**
     * Tests whether the student has course credit or transfer credit for MATH 120.
     *
     * @param bOrHigher true to require a B- or higher grade
     * @return true if student has some form of credit for MATH 120
     */
    boolean has120(final boolean bOrHigher) {

        final Collection<ECourse> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();
        return completed.contains(ECourse.M_120);
    }

    /**
     * Tests whether the student has course credit or transfer credit for MATH 127.
     *
     * @param bOrHigher true to require a B- or higher grade
     * @return true if student has some form of credit for MATH 127
     */
    boolean has127(final boolean bOrHigher) {

        final Collection<ECourse> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();
        return completed.contains(ECourse.M_127);
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 125.
     *
     * @return true if student has some form of credit for MATH 125
     */
    boolean has125() {

        boolean has125 = placedOut("M 125");

        if (!has125) {
            final Collection<ECourse> completed = getCompleted();
            has125 = completed.contains(ECourse.M_125);
        }

        return has125;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 126.
     *
     * @param bOrHigher true to require a B- or higher grade
     * @return true if student has some form of credit for MATH 126
     */
    boolean has126(final boolean bOrHigher) {

        boolean has126 = placedOut("M 126");

        if (!has126) {
            final Collection<ECourse> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();
            has126 = completed.contains(ECourse.M_126);
        }

        return has126;
    }

    /**
     * Tests eligibility for MATH 118.
     *
     * @return true if the student is eligible for MATH 118
     */
    boolean isEligibleFor118() {

        boolean eligible = placedOut("M 117");

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            eligible = completed.contains(ECourse.M_117) || completed.contains(ECourse.M_118)
                       || completed.contains(ECourse.M_120) || completed.contains(ECourse.M_127)
                       || completed.contains(ECourse.M_124) || completed.contains(ECourse.M_125)
                       || completed.contains(ECourse.M_126) || completed.contains(ECourse.M_141)
                       || completed.contains(ECourse.M_155) || completed.contains(ECourse.M_156)
                       || completed.contains(ECourse.M_157) || completed.contains(ECourse.M_159)
                       || completed.contains(ECourse.M_160) || completed.contains(ECourse.M_161)
                       || completed.contains(ECourse.M_229) || completed.contains(ECourse.M_269);
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 124.
     *
     * @return true if the student is eligible for MATH 124
     */
    boolean isEligibleFor124() {

        boolean eligible = placedOut("M 118");

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            eligible = completed.contains(ECourse.M_118) || completed.contains(ECourse.M_120)
                       || completed.contains(ECourse.M_127) || completed.contains(ECourse.M_124)
                       || completed.contains(ECourse.M_125) || completed.contains(ECourse.M_126)
                       || completed.contains(ECourse.M_141) || completed.contains(ECourse.M_155)
                       || completed.contains(ECourse.M_156) || completed.contains(ECourse.M_157)
                       || completed.contains(ECourse.M_159) || completed.contains(ECourse.M_160)
                       || completed.contains(ECourse.M_161) || completed.contains(ECourse.M_229)
                       || completed.contains(ECourse.M_269);
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 125.
     *
     * @return true if the student is eligible for MATH 125
     */
    boolean isEligibleFor125() {

        boolean eligible = placedOut("M 118");

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            eligible = completed.contains(ECourse.M_118) || completed.contains(ECourse.M_120)
                       || completed.contains(ECourse.M_127) || completed.contains(ECourse.M_124)
                       || completed.contains(ECourse.M_125) || completed.contains(ECourse.M_126)
                       || completed.contains(ECourse.M_141) || completed.contains(ECourse.M_155)
                       || completed.contains(ECourse.M_156) || completed.contains(ECourse.M_157)
                       || completed.contains(ECourse.M_159) || completed.contains(ECourse.M_160)
                       || completed.contains(ECourse.M_161) || completed.contains(ECourse.M_229)
                       || completed.contains(ECourse.M_269);
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 126.
     *
     * @return true if the student is eligible for MATH 126
     */
    boolean isEligibleFor126() {

        boolean eligible = placedOut("M 125");

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            eligible = completed.contains(ECourse.M_125) || completed.contains(ECourse.M_127)
                       || completed.contains(ECourse.M_126) || completed.contains(ECourse.M_155)
                       || completed.contains(ECourse.M_156) || completed.contains(ECourse.M_157)
                       || completed.contains(ECourse.M_159) || completed.contains(ECourse.M_160)
                       || completed.contains(ECourse.M_161);
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 141.
     *
     * @return true if the student is eligible for MATH 141
     */
    boolean isEligibleFor141() {

        boolean eligible = placedOut("M 118");

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            eligible = completed.contains(ECourse.M_118) || completed.contains(ECourse.M_120)
                       || completed.contains(ECourse.M_127) || completed.contains(ECourse.M_141)
                       || completed.contains(ECourse.M_155) || completed.contains(ECourse.M_156)
                       || completed.contains(ECourse.M_157) || completed.contains(ECourse.M_159)
                       || completed.contains(ECourse.M_160) || completed.contains(ECourse.M_161);
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 155.
     *
     * @return true if the student is eligible for MATH 155
     */
    boolean isEligibleFor155() {

        boolean has124 = placedOut("M 124");
        boolean has125 = placedOut("M 125");

        boolean eligible = has124 && has125;

        if (!eligible) {
            final Collection<ECourse> completed = getCompleted();
            if (!has124) {
                has124 = completed.contains(ECourse.M_124) || completed.contains(ECourse.M_120);
            }
            if (!has125) {
                has125 = completed.contains(ECourse.M_125);
            }
            if (has124 && has125) {
                eligible = true;
            } else {
                eligible = completed.contains(ECourse.M_127) || completed.contains(ECourse.M_141)
                           || completed.contains(ECourse.M_155) || completed.contains(ECourse.M_156)
                           || completed.contains(ECourse.M_157) || completed.contains(ECourse.M_159)
                           || completed.contains(ECourse.M_160) || completed.contains(ECourse.M_161);
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 156 or MATH 160.
     *
     * @return true if the student is eligible for MATH 156 or MATH 160
     */
    boolean isEligibleFor156or160() {

        boolean has124 = placedOut("M 124");
        boolean has126 = placedOut("M 126");

        boolean eligible = has124 && has126;

        if (!eligible) {
            final Collection<ECourse> completed = getCompletedBOrHigher();
            if (!has124) {
                has124 = completed.contains(ECourse.M_124) || completed.contains(ECourse.M_120)
                         || completed.contains(ECourse.M_155);
            }
            if (!has126) {
                has126 = completed.contains(ECourse.M_126);
            }
            if (has124 && has126) {
                eligible = true;
            } else {
                eligible = completed.contains(ECourse.M_127) || completed.contains(ECourse.M_156)
                           || completed.contains(ECourse.M_159) || completed.contains(ECourse.M_160)
                           || completed.contains(ECourse.M_161);
            }
        }

        return eligible;
    }
}
