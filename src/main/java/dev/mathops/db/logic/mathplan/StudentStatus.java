package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawMpeCredit;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStmpe;
import dev.mathops.db.old.rawrecord.RawStudent;

import java.sql.SQLException;
import java.util.Collection;
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
    public Collection<String> getCompleted() {

        final Collection<String> completed = new HashSet<>(6);
        for (final RawFfrTrns transfer : this.transferCredit) {
            completed.add(transfer.course);
        }
        for (final RawStcourse finished : this.completedCourses) {
            completed.add(finished.course);
        }

        return completed;
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
    public Collection<String> getCompletedBOrHigher() {

        final Collection<String> completed = new HashSet<>(6);
        for (final RawFfrTrns transfer : this.transferCredit) {
            final String grade = transfer.grade;
            if (grade.startsWith("A") || grade.startsWith("B") || grade.startsWith("TA") || grade.startsWith("TB")) {
                completed.add(transfer.course);
            }
        }
        for (final RawStcourse finished : this.completedCourses) {
            final String grade = finished.courseGrade;
            if (grade.startsWith("A") || grade.startsWith("B") || grade.startsWith("TA") || grade.startsWith("TB")) {
                completed.add(finished.course);
            }
        }

        return completed;
    }

    /**
     * Tests whether a specific precalculus course has been completed.
     *
     * @param course the course
     * @return true if the course was completed
     */
    public boolean hasCompleted(final EPrecalcCourse course) {

        boolean result = false;

        final Collection<String> completed = getCompleted();

        for (final String id : completed) {
            if (course == EPrecalcCourse.M_101 && ("M 101".equals(id) || "MATH 101".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_117 && ("M 117".equals(id) || "MATH 117".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_118 && ("M 118".equals(id) || "MATH 118".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_124 && ("M 124".equals(id) || "MATH 124".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_125 && ("M 125".equals(id) || "MATH 125".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_126 && ("M 126".equals(id) || "MATH 126".equals(id))) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Tests whether a specific precalculus course has been completed with a B- or higher grade.
     *
     * @param course the course
     * @return true if the course was completed with a B- or higher grade
     */
    public boolean hasCompletedWithB(final EPrecalcCourse course) {

        boolean result = false;

        final Collection<String> completed = getCompletedBOrHigher();

        for (final String id : completed) {
            if (course == EPrecalcCourse.M_101 && ("M 101".equals(id) || "MATH 101".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_117 && ("M 117".equals(id) || "MATH 117".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_118 && ("M 118".equals(id) || "MATH 118".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_124 && ("M 124".equals(id) || "MATH 124".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_125 && ("M 125".equals(id) || "MATH 125".equals(id))) {
                result = true;
            } else if (course == EPrecalcCourse.M_126 && ("M 126".equals(id) || "MATH 126".equals(id))) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Tests whether the student is eligible for a specific course.
     *
     * @param course the course
     * @return true if the student has met the prerequisites to take the course
     */
    public boolean isEligible(final EPrecalcCourse course) {

        boolean eligible = false;

        if (course == EPrecalcCourse.M_101) {
            eligible = true;
        } else if (course == EPrecalcCourse.M_117 || course == EPrecalcCourse.M_120) {
            eligible = isEligibleFor117();
        } else if (course == EPrecalcCourse.M_118) {
            eligible = isEligibleFor118();
        } else if (course == EPrecalcCourse.M_124) {
            eligible = isEligibleFor124();
        } else if (course == EPrecalcCourse.M_125) {
            eligible = isEligibleFor125();
        } else if (course == EPrecalcCourse.M_126) {
            eligible = isEligibleFor126();
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
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 117".equals(id) || "MATH 117".equals(id)
                    || "M 118".equals(id) || "MATH 118".equals(id)
                    || "M 124".equals(id) || "MATH 124".equals(id)
                    || "M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 055".equals(id) || "MATH 055".equals(id)
                    || "M 093".equals(id) || "MATH 093".equals(id)
                    || "M 099".equals(id) || "MATH 099".equals(id)
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
     * Tests eligibility for MATH 118.
     *
     * @return true if the student is eligible for MATH 118
     */
    boolean isEligibleFor118() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 117".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 117".equals(id) || "MATH 117".equals(id)
                    || "M 118".equals(id) || "MATH 118".equals(id)
                    || "M 124".equals(id) || "MATH 124".equals(id)
                    || "M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 141".equals(id) || "MATH 141".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)
                    || "M 229".equals(id) || "MATH 229".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 124.
     *
     * @return true if the student is eligible for MATH 124
     */
    boolean isEligibleFor124() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 118".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 118".equals(id) || "MATH 118".equals(id)
                    || "M 124".equals(id) || "MATH 124".equals(id)
                    || "M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 141".equals(id) || "MATH 141".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)
                    || "M 229".equals(id) || "MATH 229".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 125.
     *
     * @return true if the student is eligible for MATH 125
     */
    boolean isEligibleFor125() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 118".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 118".equals(id) || "MATH 118".equals(id)
                    || "M 124".equals(id) || "MATH 124".equals(id)
                    || "M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 141".equals(id) || "MATH 141".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)
                    || "M 229".equals(id) || "MATH 229".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 126.
     *
     * @return true if the student is eligible for MATH 126
     */
    boolean isEligibleFor126() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 125".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 125".equals(id) || "MATH 125".equals(id)
                    || "M 126".equals(id) || "MATH 126".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 141.
     *
     * @return true if the student is eligible for MATH 141
     */
    boolean isEligibleFor141() {

        boolean eligible = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 118".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                eligible = true;
                break;
            }
        }

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 118".equals(id) || "MATH 118".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 127".equals(id) || "MATH 127".equals(id)
                    || "M 141".equals(id) || "MATH 141".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)
                    || "M 156".equals(id) || "MATH 156".equals(id)
                    || "M 157".equals(id) || "MATH 157".equals(id)
                    || "M 159".equals(id) || "MATH 159".equals(id)
                    || "M 160".equals(id) || "MATH 160".equals(id)
                    || "M 161".equals(id) || "MATH 161".equals(id)) {
                    eligible = true;
                    break;
                }
            }
        }

        return eligible;
    }

    /**
     * Tests eligibility for MATH 155.
     *
     * @return true if the student is eligible for MATH 155
     */
    boolean isEligibleFor155() {

        boolean has124 = false;
        boolean has125 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 124".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has124 = true;
            } else if ("M 125".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has125 = true;
            }
        }

        if (!(has124 && has125)) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                if ("M 124".equals(id) || "MATH 124".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)) {
                    has124 = true;
                } else if ("M 125".equals(id) || "MATH 125".equals(id)) {
                    has125 = true;
                } else if ("M 127".equals(id) || "MATH 127".equals(id)
                           || "M 155".equals(id) || "MATH 155".equals(id)
                           || "M 156".equals(id) || "MATH 156".equals(id)
                           || "M 159".equals(id) || "MATH 159".equals(id)
                           || "M 160".equals(id) || "MATH 160".equals(id)
                           || "M 161".equals(id) || "MATH 161".equals(id)) {
                    has124 = true;
                    has125 = true;
                }
            }
        }

        return has124 && has125;
    }

    /**
     * Tests eligibility for MATH 156 or MATH 160.
     *
     * @return true if the student is eligible for MATH 156 or MATH 160
     */
    boolean isEligibleFor156or160() {

        boolean has124 = false;
        boolean has126 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 124".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has124 = true;
            } else if ("M 126".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has126 = true;
            }
        }

        if (!(has124 && has126)) {
            final Collection<String> completed = getCompletedBOrHigher();

            for (final String id : completed) {
                if ("M 124".equals(id) || "MATH 124".equals(id)
                    || "M 120".equals(id) || "MATH 120".equals(id)
                    || "M 155".equals(id) || "MATH 155".equals(id)) {
                    has124 = true;
                } else if ("M 126".equals(id) || "MATH 126".equals(id)) {
                    has126 = true;
                } else if ("M 127".equals(id) || "MATH 127".equals(id)
                           || "M 156".equals(id) || "MATH 156".equals(id)
                           || "M 159".equals(id) || "MATH 159".equals(id)
                           || "M 160".equals(id) || "MATH 160".equals(id)
                           || "M 161".equals(id) || "MATH 161".equals(id)) {
                    has124 = true;
                    has126 = true;
                }
            }
        }

        return has124 && has126;
    }
}
