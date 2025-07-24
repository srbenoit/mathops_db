package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.log.Log;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.logic.mathplan.types.ETrajectoryCourse;
import dev.mathops.db.logic.mathplan.types.PickList;

import java.util.ArrayList;
import java.util.List;

/**
 * A student's "recommended trajectory" through Mathematics courses to try to satisfy the requirements for any of their
 * selected majors.
 */
public final class RecommendedTrajectory {

    /** The requirements. */
    public final Requirements requirements;

    /** Trajectory status for MATH 117. */
    public ETrajectoryCourse math117 = ETrajectoryCourse.NOT_NEEDED;

    /** Trajectory status for MATH 118. */
    public ETrajectoryCourse math118 = ETrajectoryCourse.NOT_NEEDED;

    /** Trajectory status for MATH 124. */
    public ETrajectoryCourse math124 = ETrajectoryCourse.NOT_NEEDED;

    /** Trajectory status for MATH 125. */
    public ETrajectoryCourse math125 = ETrajectoryCourse.NOT_NEEDED;

    /** Trajectory status for MATH 126. */
    public ETrajectoryCourse math126 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include the option to take MATH 120 rather than 117+118+124. */
    public boolean include120Option = false;

    /**
     * Constructs a new {@code RecommendedTrajectory}.
     *
     * @param stuStatus       the student's current status (completed courses, transfer credit, placement credit)
     * @param theRequirements the requirements for the set of majors that were selected
     */
    RecommendedTrajectory(final StudentStatus stuStatus, final Requirements theRequirements) {

        this.requirements = theRequirements;

        // All named or implicit precalculus requirements are part of the trajectory, but if 117/118/124 are only
        // "implicit", add the 120 option.
        final boolean canAllow120 = !scanNamedCourses(stuStatus);
        scanImplicitPrerequisites(stuStatus);

        // Make sure the trajectory will satisfy all pick lists, and if not, add courses from pick lists (lowest
        // to highest) until they are satisfied.
        for (final PickList pick : theRequirements.pickLists) {
            processPickList(pick, stuStatus);
        }

        this.include120Option = canAllow120 && this.math117 != ETrajectoryCourse.NOT_NEEDED
                                && this.math118 != ETrajectoryCourse.NOT_NEEDED
                                && this.math124 != ETrajectoryCourse.NOT_NEEDED;
    }

    /**
     * Scans the set of named precalculus requirements and adds those courses to the trajectory.  This method tracks
     * whether any of MATH 117, 118, or 124 are named, and returns true if so.
     *
     * @param stuStatus the current status
     * @return true if any of MATh 117, 118, or 124 are named; false if not
     */
    private boolean scanNamedCourses(final StudentStatus stuStatus) {

        boolean named171824 = false;

        for (final ECourse named : this.requirements.namedPrecalculus) {
            if (named == ECourse.M_117) {
                this.math117 = compute117Status(stuStatus);
                named171824 = true;
            } else if (named == ECourse.M_118) {
                named171824 = true;
                this.math118 = compute118Status(stuStatus);
            } else if (named == ECourse.M_124) {
                named171824 = true;
                this.math124 = compute124Status(stuStatus, this.requirements.needsBMinusIn2426);
            } else if (named == ECourse.M_125) {
                this.math125 = compute125Status(stuStatus);
            } else if (named == ECourse.M_126) {
                this.math126 = compute126Status(stuStatus, this.requirements.needsBMinusIn2426);
            }
        }

        return named171824;
    }

    /**
     * Scans for implicit requirements needed to fulfill prerequisites for named courses.
     *
     * @param stuStatus the current status
     */
    private void scanImplicitPrerequisites(final StudentStatus stuStatus) {

        for (final ECourse implicit : this.requirements.implicitCourses) {

            if (implicit == ECourse.M_117 && this.math117 == ETrajectoryCourse.NOT_NEEDED) {
                this.math117 = compute117Status(stuStatus);
            } else if (implicit == ECourse.M_118 && this.math118 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute118Status(stuStatus);
            } else if (implicit == ECourse.M_124 && this.math124 == ETrajectoryCourse.NOT_NEEDED) {
                this.math124 = compute124Status(stuStatus, this.requirements.needsBMinusIn2426);
            } else if (implicit == ECourse.M_125 && this.math125 == ETrajectoryCourse.NOT_NEEDED) {
                this.math125 = compute125Status(stuStatus);
            } else if (implicit == ECourse.M_126 && this.math126 == ETrajectoryCourse.NOT_NEEDED) {
                this.math126 = compute126Status(stuStatus, this.requirements.needsBMinusIn2426);
            }
        }

        if (this.requirements.namedCalculusRequirement == ECourse.M_160
            || this.requirements.namedCalculusRequirement == ECourse.M_156
            || this.requirements.namedCalculusRequirement == ECourse.M_156_OR_160) {

            if (this.math117 == ETrajectoryCourse.NOT_NEEDED) {
                this.math117 = compute117Status(stuStatus);
            }
            if (this.math118 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute118Status(stuStatus);
            }
            if (this.math124 == ETrajectoryCourse.NOT_NEEDED) {
                this.math124 = compute124Status(stuStatus, true);
            }
            if (this.math125 == ETrajectoryCourse.NOT_NEEDED) {
                this.math125 = compute125Status(stuStatus);
            }
            if (this.math126 == ETrajectoryCourse.NOT_NEEDED) {
                this.math126 = compute126Status(stuStatus, true);
            }
        } else if (this.requirements.namedCalculusRequirement == ECourse.M_155
                   || this.requirements.namedCalculusRequirement == ECourse.M_155_OR_160) {
            if (this.math117 == ETrajectoryCourse.NOT_NEEDED) {
                this.math117 = compute117Status(stuStatus);
            }
            if (this.math118 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute118Status(stuStatus);
            }
            if (this.math124 == ETrajectoryCourse.NOT_NEEDED) {
                this.math124 = compute124Status(stuStatus, false);
            }
            if (this.math125 == ETrajectoryCourse.NOT_NEEDED) {
                this.math125 = compute125Status(stuStatus);
            }
        } else if (this.requirements.namedCalculusRequirement == ECourse.M_141
                   || this.requirements.namedCalculusRequirement == ECourse.M_141_OR_155
                   || this.requirements.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
            if (this.math117 == ETrajectoryCourse.NOT_NEEDED) {
                this.math117 = compute117Status(stuStatus);
            }
            if (this.math118 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute118Status(stuStatus);
            }
        }
    }

    /**
     * Computes the status of MATH 117.
     *
     * @param stuStatus the student status
     * @return the trajectory status for MATH 117
     */
    private static ETrajectoryCourse compute117Status(final StudentStatus stuStatus) {

        return stuStatus.hasCompleted(ECourse.M_117)
                ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                : stuStatus.isEligibleFor117() ? ETrajectoryCourse.ELIGIBLE : ETrajectoryCourse.INELIGIBLE;
    }

    /**
     * Computes the status of MATH 118.
     *
     * @param stuStatus the student status
     * @return the trajectory status for MATH 118
     */
    private static ETrajectoryCourse compute118Status(final StudentStatus stuStatus) {

        return stuStatus.hasCompleted(ECourse.M_118)
                ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                : stuStatus.isEligible(
                ECourse.M_118) ? ETrajectoryCourse.ELIGIBLE : ETrajectoryCourse.INELIGIBLE;
    }

    /**
     * Computes the status of MATH 124.
     *
     * @param stuStatus the student status
     * @param needsB    true if the student needs a B- or higher grade
     * @return the trajectory status for MATH 124
     */
    private static ETrajectoryCourse compute124Status(final StudentStatus stuStatus, final boolean needsB) {

        final ETrajectoryCourse result;

        if (needsB) {
            result = stuStatus.hasCompletedWithB(ECourse.M_124)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.hasCompleted(ECourse.M_124) ? ETrajectoryCourse.COMPLETED_BUT_NEED_B
                    : stuStatus.isEligible(ECourse.M_124) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        } else {
            result = stuStatus.hasCompleted(ECourse.M_124)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.isEligible(ECourse.M_124) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        }

        return result;
    }

    /**
     * Computes the status of MATH 125.
     *
     * @param stuStatus the student status
     * @return the trajectory status for MATH 125
     */
    private static ETrajectoryCourse compute125Status(final StudentStatus stuStatus) {

        return stuStatus.hasCompleted(ECourse.M_125)
                ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                : stuStatus.isEligible(
                ECourse.M_125) ? ETrajectoryCourse.ELIGIBLE : ETrajectoryCourse.INELIGIBLE;
    }

    /**
     * Computes the status of MATH 126.
     *
     * @param stuStatus the student status
     * @param needsB    true if the student needs a B- or higher grade
     * @return the trajectory status for MATH 126
     */
    private static ETrajectoryCourse compute126Status(final StudentStatus stuStatus, final boolean needsB) {

        final ETrajectoryCourse result;

        if (needsB) {
            result = stuStatus.hasCompletedWithB(ECourse.M_126)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.hasCompleted(ECourse.M_126) ? ETrajectoryCourse.COMPLETED_BUT_NEED_B
                    : stuStatus.isEligible(ECourse.M_126) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        } else {
            result = stuStatus.hasCompleted(ECourse.M_126)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.isEligible(ECourse.M_126) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        }

        return result;
    }

    /**
     * Processes a single pick list.
     *
     * @param pick      the pick list
     * @param stuStatus the student's status
     */
    private void processPickList(final PickList pick, final StudentStatus stuStatus) {

        final PickList play = new PickList(pick.numCredits, pick.courses);

        // NOTE: No pick lists currently include MATH 101

        if (this.math117 != ETrajectoryCourse.NOT_NEEDED) {
            play.remove(ECourse.M_117);
        }
        if (this.math118 != ETrajectoryCourse.NOT_NEEDED) {
            play.remove(ECourse.M_118);
        }
        if (this.math124 != ETrajectoryCourse.NOT_NEEDED) {
            play.remove(ECourse.M_124);
        }
        if (this.math125 != ETrajectoryCourse.NOT_NEEDED) {
            play.remove(ECourse.M_125);
        }
        if (this.math126 != ETrajectoryCourse.NOT_NEEDED) {
            play.remove(ECourse.M_126);
        }

        // NOTE: We don't remove 120 because 120 is only allowed when 117+118+124 are allowed, and we have already
        // counted their removal

        // Make sure the trajectory meets the pick list
        if (play.numCredits > 0 && play.courses.contains(ECourse.M_117)
            && this.math117 == ETrajectoryCourse.NOT_NEEDED) {
            this.math117 = compute117Status(stuStatus);
            play.remove(ECourse.M_117);
        }
        if (play.numCredits > 0 && play.courses.contains(ECourse.M_118)
            && this.math118 == ETrajectoryCourse.NOT_NEEDED) {
            this.math118 = compute118Status(stuStatus);
            play.remove(ECourse.M_118);
        }
        if (play.numCredits > 0 && play.courses.contains(ECourse.M_124)
            && this.math124 == ETrajectoryCourse.NOT_NEEDED) {
            this.math124 = compute124Status(stuStatus, this.requirements.needsBMinusIn2426);
            play.remove(ECourse.M_124);
        }
        if (play.numCredits > 0 && play.courses.contains(ECourse.M_125)
            && this.math125 == ETrajectoryCourse.NOT_NEEDED) {
            this.math125 = compute125Status(stuStatus);
            play.remove(ECourse.M_125);
        }
        if (play.numCredits > 0 && play.courses.contains(ECourse.M_126)
            && this.math126 == ETrajectoryCourse.NOT_NEEDED) {
            this.math126 = compute126Status(stuStatus, this.requirements.needsBMinusIn2426);
            play.remove(ECourse.M_126);
        }
    }
}
