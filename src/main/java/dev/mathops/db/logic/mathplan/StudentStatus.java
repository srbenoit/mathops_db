package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.logic.mathplan.types.ECourse;
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
    public boolean hasCompleted(final ECourse course) {

        final Collection<String> completed = getCompleted();
        return isCourseInList(course, completed);
    }

    /**
     * Tests whether a specific precalculus course has been completed with a B- or higher grade.
     *
     * @param course the course
     * @return true if the course was completed with a B- or higher grade
     */
    public boolean hasCompletedWithB(final ECourse course) {

        final Collection<String> completed = getCompletedBOrHigher();
        return isCourseInList(course, completed);
    }

    /**
     * Tests whether a course appears in a list of course IDs.
     *
     * @param course the course for which to test
     * @param list   the list of course IDs
     * @return true if the course appears in the list
     */
    private static boolean isCourseInList(final ECourse course, final Collection<String> list) {

        boolean result = false;

        for (final String id : list) {
            final String trimmed = id.replace("MATH ", "M ");

            if (course == ECourse.M_101 && "M 101".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_117 && "M 117".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_118 && "M 118".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_124 && "M 124".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_125 && "M 125".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_126 && "M 126".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_141 && "M 141".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_155 && "M 155".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_156 && "M 156".equals(trimmed)) {
                result = true;
            } else if (course == ECourse.M_160 && "M 160".equals(trimmed)) {
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
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 117.
     *
     * @return true if student has some form of credit for MATH 117
     */
    boolean has117() {

        boolean has117 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 117".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has117 = true;
                break;
            }
        }

        if (!has117) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 117".equals(trimmed)) {
                    has117 = true;
                    break;
                }
            }
        }

        return has117;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 118.
     *
     * @return true if student has some form of credit for MATH 118
     */
    boolean has118() {

        boolean has118 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 118".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has118 = true;
                break;
            }
        }

        if (!has118) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 118".equals(trimmed)) {
                    has118 = true;
                    break;
                }
            }
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

        boolean has124 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 124".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has124 = true;
                break;
            }
        }

        if (!has124) {
            final Collection<String> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 124".equals(trimmed)) {
                    has124 = true;
                    break;
                }
            }
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

        boolean has120 = false;

        final Collection<String> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();

        for (final String id : completed) {
            final String trimmed = id.replace("MATH ", "M ");

            if ("M 120".equals(trimmed)) {
                has120 = true;
                break;
            }
        }

        return has120;
    }

    /**
     * Tests whether the student has course credit or transfer credit for MATH 127.
     *
     * @param bOrHigher true to require a B- or higher grade
     * @return true if student has some form of credit for MATH 127
     */
    boolean has127(final boolean bOrHigher) {

        boolean has127 = false;

        final Collection<String> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();

        for (final String id : completed) {
            final String trimmed = id.replace("MATH ", "M ");

            if ("M 127".equals(trimmed)) {
                has127 = true;
                break;
            }
        }

        return has127;
    }

    /**
     * Tests whether the student has course credit, transfer credit, or math placement credit for MATH 125.
     *
     * @return true if student has some form of credit for MATH 125
     */
    boolean has125() {

        boolean has125 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 125".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has125 = true;
                break;
            }
        }

        if (!has125) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 125".equals(trimmed)) {
                    has125 = true;
                    break;
                }
            }
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

        boolean has126 = false;

        for (final RawMpeCredit mpt : this.placementCredit) {
            if ("M 126".equals(mpt.course) && ("P".equals(mpt.examPlaced) || "C".equals(mpt.examPlaced))) {
                has126 = true;
                break;
            }
        }

        if (!has126) {
            final Collection<String> completed = bOrHigher ? getCompletedBOrHigher() : getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 126".equals(trimmed)) {
                    has126 = true;
                    break;
                }
            }
        }

        return has126;
    }

    /**
     * Tests eligibility for MATH 118.
     *
     * @return true if the student is eligible for MATH 118
     */
    boolean isEligibleFor118() {

        boolean eligible = has117() || has120(false) || has127(false);

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 118".equals(trimmed) || "M 124".equals(trimmed) || "M 125".equals(trimmed)
                    || "M 126".equals(trimmed) || "M 141".equals(trimmed) || "M 155".equals(trimmed)
                    || "M 156".equals(trimmed) || "M 157".equals(trimmed) || "M 159".equals(trimmed)
                    || "M 160".equals(trimmed) || "M 161".equals(trimmed) || "M 229".equals(trimmed)) {
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

        boolean eligible = has118() || has120(false) || has127(false);

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 124".equals(trimmed) || "M 125".equals(trimmed) || "M 126".equals(trimmed)
                    || "M 141".equals(trimmed) || "M 155".equals(trimmed) || "M 156".equals(trimmed)
                    || "M 157".equals(trimmed) || "M 159".equals(trimmed) || "M 160".equals(trimmed)
                    || "M 161".equals(trimmed) || "M 229".equals(trimmed)) {
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

        boolean eligible = has118() || has120(false) || has127(false);

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 124".equals(trimmed) || "M 125".equals(trimmed) || "M 126".equals(trimmed)
                    || "M 141".equals(trimmed) || "M 155".equals(trimmed) || "M 156".equals(trimmed)
                    || "M 157".equals(trimmed) || "M 159".equals(trimmed) || "M 160".equals(trimmed)
                    || "M 161".equals(trimmed) || "M 229".equals(trimmed)) {
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

        boolean eligible = has125() || has127(false);

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 126".equals(trimmed) || "M 155".equals(trimmed) || "M 156".equals(trimmed)
                    || "M 157".equals(trimmed) || "M 159".equals(trimmed) || "M 160".equals(trimmed)
                    || "M 161".equals(trimmed)) {
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

        boolean eligible = has118() || has120(false) || has127(false);

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 141".equals(trimmed) || "M 155".equals(trimmed) || "M 156".equals(trimmed)
                    || "M 157".equals(trimmed) || "M 159".equals(trimmed) || "M 160".equals(trimmed)
                    || "M 161".equals(trimmed)) {
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

        boolean eligible = (has124(false) || has120(false) || has127(false)) && has125();

        if (!eligible) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 155".equals(trimmed) || "M 156".equals(trimmed) || "M 159".equals(trimmed)
                    || "M 160".equals(trimmed) || "M 161".equals(trimmed)) {
                    eligible = true;
                }
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

        boolean has124 = has124(true) || has120(true) || has127(true);
        boolean has126 = has126(true);

        if (!(has124 && has126)) {
            final Collection<String> completed = getCompleted();

            for (final String id : completed) {
                final String trimmed = id.replace("MATH ", "M ");

                if ("M 155".equals(trimmed)) {
                    has124 = true;
                } else if ("M 156".equals(trimmed) || "M 159".equals(trimmed) || "M 160".equals(trimmed)
                           || "M 161".equals(trimmed)) {
                    has124 = true;
                    has126 = true;
                }
            }
        }

        return has124 && has126;
    }
}
