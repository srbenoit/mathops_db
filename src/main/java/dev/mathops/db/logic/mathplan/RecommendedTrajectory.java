package dev.mathops.db.logic.mathplan;

/**
 * A student's "recommended trajectory" through Mathematics courses to try to satisfy the requirements for any of their
 * selected majors.
 */
public class RecommendedTrajectory {

    /** The calculus requirement. */
    private final ENamedCalculusRequirement calcRequirement;

    /** True if the student needs a B- or higher in MATH 124 and 126. */
    private final boolean needsB;

    /** True to include MATH 101. */
    private ETrajectoryCourse math101 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include MATH 117. */
    private ETrajectoryCourse math117 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include MATH 118. */
    private ETrajectoryCourse math118 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include MATH 124. */
    private ETrajectoryCourse math124 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include MATH 125. */
    private ETrajectoryCourse math125 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include MATH 126. */
    private ETrajectoryCourse math126 = ETrajectoryCourse.NOT_NEEDED;

    /** True to include the option to take MATH 120 rather than 117+118+124. */
    private boolean include120Option = false;

    /**
     * Constructs a new {@code RecommendedTrajectory}.
     *
     * @param stuStatus    the student's current status (completed courses, transfer credit, placement credit)
     * @param requirements the requirements for the set of majors that were selected
     */
    RecommendedTrajectory(final StudentStatus stuStatus, final MajorSetRequirements requirements) {

        // If there is a calc requirement, that's part of the trajectory (this could be a choice between several
        // calculus courses).
        this.calcRequirement = requirements.namedCalculusRequirement;
        this.needsB = requirements.precalculusRequirement.needsBMinusIn2426;

        // All named or implicit precalculus requirements are part of the trajectory, but if 117/118/124 are only
        // "implicit", add the 120 option.
        final boolean canAllow120 = !scanNamedCourses(stuStatus, requirements);
        scanImplicitPrerequisites(stuStatus, requirements);

        // Make sure the trajectory will satisfy all pick lists, and if not, add courses from pick lists (lowest
        // to highest) until they are satisfied.
        for (final PickList pick : requirements.precalculusRequirement.pickLists) {
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
     * @param stuStatus    the current status
     * @param requirements the requirements for the set of majors that were selected
     * @return true if any of MATh 117, 118, or 124 are named; false if not
     */
    private boolean scanNamedCourses(final StudentStatus stuStatus, final MajorSetRequirements requirements) {

        boolean named171824 = false;

        for (final EPrecalcCourse named : requirements.precalculusRequirement.namedCourses) {
            if (named == EPrecalcCourse.M_101) {
                this.math101 = ETrajectoryCourse.ELIGIBLE;
            } else if (named == EPrecalcCourse.M_117) {
                this.math117 = compute117Status(stuStatus);
            } else if (named == EPrecalcCourse.M_118) {
                this.math118 = compute118Status(stuStatus);
            } else if (named == EPrecalcCourse.M_124) {
                this.math118 = compute124Status(stuStatus, needsB);
            } else if (named == EPrecalcCourse.M_125) {
                this.math118 = compute125Status(stuStatus);
            } else if (named == EPrecalcCourse.M_126) {
                this.math118 = compute126Status(stuStatus, needsB);
            }

            named171824 = named171824 || named == EPrecalcCourse.M_117 || named == EPrecalcCourse.M_118
                          || named == EPrecalcCourse.M_124;
        }

        return named171824;
    }

    /**
     * Scans for implicit requirements needed to fulfill prerequisites for named courses.
     *
     * @param stuStatus    the current status
     * @param requirements the requirements for the set of majors that were selected
     */
    private void scanImplicitPrerequisites(final StudentStatus stuStatus, final MajorSetRequirements requirements) {

        for (final EPrecalcCourse implicit : requirements.precalculusRequirement.implicitCourses) {

            if (implicit == EPrecalcCourse.M_117 && this.math117 == ETrajectoryCourse.NOT_NEEDED) {
                this.math117 = compute117Status(stuStatus);
            } else if (implicit == EPrecalcCourse.M_118 && this.math118 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute118Status(stuStatus);
            } else if (implicit == EPrecalcCourse.M_124 && this.math124 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute124Status(stuStatus, needsB);
            } else if (implicit == EPrecalcCourse.M_125 && this.math125 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute125Status(stuStatus);
            } else if (implicit == EPrecalcCourse.M_126 && this.math126 == ETrajectoryCourse.NOT_NEEDED) {
                this.math118 = compute126Status(stuStatus, needsB);
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

        return stuStatus.hasCompleted(EPrecalcCourse.M_117)
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

        return stuStatus.hasCompleted(EPrecalcCourse.M_118)
                ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                : stuStatus.isEligible(
                EPrecalcCourse.M_118) ? ETrajectoryCourse.ELIGIBLE : ETrajectoryCourse.INELIGIBLE;
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
            result = stuStatus.hasCompletedWithB(EPrecalcCourse.M_124)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.hasCompleted(EPrecalcCourse.M_124) ? ETrajectoryCourse.COMPLETED_BUT_NEED_B
                    : stuStatus.isEligible(EPrecalcCourse.M_124) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        } else {
            result = stuStatus.hasCompleted(EPrecalcCourse.M_124)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.isEligible(EPrecalcCourse.M_124) ? ETrajectoryCourse.ELIGIBLE
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

        return stuStatus.hasCompleted(EPrecalcCourse.M_125)
                ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                : stuStatus.isEligible(
                EPrecalcCourse.M_125) ? ETrajectoryCourse.ELIGIBLE : ETrajectoryCourse.INELIGIBLE;
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
            result = stuStatus.hasCompletedWithB(EPrecalcCourse.M_126)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.hasCompleted(EPrecalcCourse.M_126) ? ETrajectoryCourse.COMPLETED_BUT_NEED_B
                    : stuStatus.isEligible(EPrecalcCourse.M_126) ? ETrajectoryCourse.ELIGIBLE
                    : ETrajectoryCourse.INELIGIBLE;
        } else {
            result = stuStatus.hasCompleted(EPrecalcCourse.M_126)
                    ? ETrajectoryCourse.COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE
                    : stuStatus.isEligible(EPrecalcCourse.M_126) ? ETrajectoryCourse.ELIGIBLE
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

        // NOTE: No pick lists currently include MATH 101

        if (this.math117 != ETrajectoryCourse.NOT_NEEDED) {
            pick.remove(EPrecalcCourse.M_117);
        }
        if (this.math118 != ETrajectoryCourse.NOT_NEEDED) {
            pick.remove(EPrecalcCourse.M_118);
        }
        if (this.math124 != ETrajectoryCourse.NOT_NEEDED) {
            pick.remove(EPrecalcCourse.M_124);
        }
        if (this.math125 != ETrajectoryCourse.NOT_NEEDED) {
            pick.remove(EPrecalcCourse.M_125);
        }
        if (this.math126 != ETrajectoryCourse.NOT_NEEDED) {
            pick.remove(EPrecalcCourse.M_126);
        }

        // NOTE: We don't remove 120 because 120 is only allowed when 117+118+124 are allowed, and we have already
        // counted their removal

        // Make sure the trajectory meets the pick list
        if (pick.numCredits > 0 && pick.courses.contains(EPrecalcCourse.M_117)
            && this.math117 == ETrajectoryCourse.NOT_NEEDED) {
            this.math117 = compute117Status(stuStatus);
            pick.remove(EPrecalcCourse.M_117);
        }
        if (pick.numCredits > 0 && pick.courses.contains(EPrecalcCourse.M_118)
            && this.math118 == ETrajectoryCourse.NOT_NEEDED) {
            this.math118 = compute118Status(stuStatus);
            pick.remove(EPrecalcCourse.M_118);
        }
        if (pick.numCredits > 0 && pick.courses.contains(EPrecalcCourse.M_124)
            && this.math124 == ETrajectoryCourse.NOT_NEEDED) {
            this.math124 = compute124Status(stuStatus, this.needsB);
            pick.remove(EPrecalcCourse.M_124);
        }
        if (pick.numCredits > 0 && pick.courses.contains(EPrecalcCourse.M_125)
            && this.math125 == ETrajectoryCourse.NOT_NEEDED) {
            this.math125 = compute125Status(stuStatus);
            pick.remove(EPrecalcCourse.M_125);
        }
        if (pick.numCredits > 0 && pick.courses.contains(EPrecalcCourse.M_126)
            && this.math126 == ETrajectoryCourse.NOT_NEEDED) {
            this.math126 = compute126Status(stuStatus, this.needsB);
            pick.remove(EPrecalcCourse.M_126);
        }
    }

    /**
     * Tests whether Math Placement is needed.  This is based on the existence of precalculus courses (other than MATH
     * 101) where the student is not eligible for any.
     *
     * @return true if placement is needed
     */
    public boolean isPlacementNeeded() {

        // FIXME: What we really need is some sense of what course the student SHOULD be eligible for in their first
        //  semester, and this decision should be based on whether or not the student is currently eligible for that
        //  course.  Right now, we assume MATH 117 will be in the list no matter what (as an implicit requirement
        //  if not named), and we just check eligibility for that.

        return this.math117 == ETrajectoryCourse.INELIGIBLE;
    }

    /**
     * Computes the student's next step (called only when required are greater than just AUCC core).
     *
     * @return the recommended next step
     */
    ENextStep computeNextStep() {

//        /** The calculus requirement. */
//        private final ENamedCalculusRequirement calcRequirement;
//
//        /** True if the student needs a B- or higher in MATH 124 and 126. */
//        private final boolean needsB;
//
//        /** True to include MATH 101. */
//        private ETrajectoryCourse math101 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include MATH 117. */
//        private ETrajectoryCourse math117 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include MATH 118. */
//        private ETrajectoryCourse math118 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include MATH 124. */
//        private ETrajectoryCourse math124 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include MATH 125. */
//        private ETrajectoryCourse math125 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include MATH 126. */
//        private ETrajectoryCourse math126 = ETrajectoryCourse.NOT_NEEDED;
//
//        /** True to include the option to take MATH 120 rather than 117+118+124. */
//        private boolean include120Option = false;

        return null;
    }
}
