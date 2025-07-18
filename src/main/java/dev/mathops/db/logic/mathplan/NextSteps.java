package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.log.Log;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.logic.mathplan.types.ENextStep;
import dev.mathops.db.logic.mathplan.types.ETrajectoryCourse;
import dev.mathops.db.logic.mathplan.types.PickList;

/**
 * A student's "next steps" based on their recommended trajectory and current status.
 */
public final class NextSteps {

    /** The students recommended next step. */
    public final ENextStep nextStep;

    /** A flag indicating placement is currently needed. */
    public final boolean placementNeeded;

    /**
     * Constructs a new {@code NextSteps} that indicates placement is not needed.
     */
    NextSteps() {

        this.nextStep = ENextStep.MSG_PLACEMENT_NOT_NEEDED;
        this.placementNeeded = false;
    }

    /**
     * Constructs a new {@code NextSteps}.
     *
     * @param stuStatus  the student's status
     * @param trajectory the student's recommended trajectory
     */
    NextSteps(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        this.nextStep = computeNextStep(stuStatus, trajectory);

        this.placementNeeded = !(this.nextStep == ENextStep.MSG_PLACEMENT_NOT_NEEDED
                                 || this.nextStep == ENextStep.MSG_ALREADY_ELIGIBLE);
    }

    /**
     * Computes the student's next step (called only when required are greater than just core courses).
     *
     * @param stuStatus  the student status
     * @param trajectory the student's recommended trajectory
     * @return the recommended next step
     */
    private ENextStep computeNextStep(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        final ENextStep result;

        final Requirements req = trajectory.requirements;

        if (req.coreOnly) {
            result = ENextStep.MSG_PLACEMENT_NOT_NEEDED;
        } else {
            // Find the first named course that has a prerequisite (there may not be any)
            final ECourse firstNamed = req.identifyFirstNamedCourse();

            // FIXME: Change the messaging depending on whether the student has placement attempts remaining and
            //  whether they are eligible for the tutorial.  If neither, then implicit courses become on the same
            //  footing as named courses.

            if (firstNamed == ECourse.M_117) {
                result = makeNextStep117(trajectory);
            } else if (firstNamed == ECourse.M_118) {
                result = makeNextStep118(trajectory);
            } else if (firstNamed == ECourse.M_124) {
                result = makeNextStep124(trajectory);
            } else if (firstNamed == ECourse.M_125) {
                result = makeNextStep125(trajectory);
            } else if (firstNamed == ECourse.M_126) {
                result = makeNextStep126(trajectory);
            } else {
                final ECourse namedCalc = req.namedCalculusRequirement;

                if (namedCalc == ECourse.M_141 || namedCalc == ECourse.M_141_OR_155
                    || namedCalc == ECourse.M_141_OR_155_OR_160) {
                    result = makeNextStep141(stuStatus, trajectory);
                } else if (firstNamed == ECourse.M_155 || firstNamed == ECourse.M_155_OR_160) {
                    result = makeNextStep155(stuStatus, trajectory);
                } else if (firstNamed == ECourse.M_156 || firstNamed == ECourse.M_156_OR_160
                           || firstNamed == ECourse.M_160) {
                    result = makeNextStep160(stuStatus, trajectory);
                } else {
                    // There is no first named course, but we're not in the "just core" situation, so there must be
                    // pick lists.
                    result = processPickLists(stuStatus, trajectory);
                }
            }
        }

        return result;
    }

    /**
     * Processes pick lists in the situation where none of the selected majors name any Math courses.
     *
     * @param stuStatus  the student status
     * @param trajectory the student's recommended trajectory
     * @return the recommended next step
     */
    private static ENextStep processPickLists(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        final ENextStep result;

        final Requirements requirements = trajectory.requirements;

        // Find the "lowest" non-101 course in any pick list
        if (requirements.pickLists.isEmpty()) {
            // This should never occur
            Log.warning("Determining Math Plan next steps for ", stuStatus.student.stuId,
                    "; there were no named courses and no pick lists");
            result = ENextStep.MSG_PLACEMENT_NOT_NEEDED;
        } else {
            final PickList firstPickList = requirements.pickLists.getFirst();
            ECourse lowest = firstPickList.getLowest();
            for (final PickList list : requirements.pickLists) {
                if (list == firstPickList) {
                    continue;
                }
                final ECourse lowestInPick = list.getLowest();
                if (lowestInPick.ordinal() < lowest.ordinal()) {
                    lowest = lowestInPick;
                }
            }

            if (lowest == ECourse.M_126) {
                result = makeNextStep126(trajectory);
            } else if (lowest == ECourse.M_125) {
                result = makeNextStep125(trajectory);
            } else if (lowest == ECourse.M_124) {
                result = makeNextStep124(trajectory);
            } else if (lowest == ECourse.M_118) {
                result = makeNextStep118(trajectory);
            } else if (lowest == ECourse.M_117) {
                result = makeNextStep117(trajectory);
            } else {
                Log.warning("Determining Math Plan next steps for ", stuStatus.student.stuId,
                        "; there were pick lists but lowest course was ", lowest);
                result = ENextStep.MSG_PLACEMENT_NOT_NEEDED;
            }
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 117 (or MATH 120).
     *
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep117(final RecommendedTrajectory trajectory) {

        return trajectory.math117 == ETrajectoryCourse.INELIGIBLE ? ENextStep.MSG_PLACE_INTO_117
                : ENextStep.MSG_ALREADY_ELIGIBLE;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 118.
     *
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep118(final RecommendedTrajectory trajectory) {

        return trajectory.math118 == ETrajectoryCourse.INELIGIBLE ? ENextStep.MSG_PLACE_OUT_117
                : ENextStep.MSG_ALREADY_ELIGIBLE;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 124.
     *
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep124(final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (trajectory.math124 == ETrajectoryCourse.INELIGIBLE) {
            // 118 can only be "INELIGIBLE" or "ELIGIBLE"
            if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                result = ENextStep.MSG_PLACE_OUT_117_118;
            } else {
                result = ENextStep.MSG_PLACE_OUT_118;
            }
        } else {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 125.
     *
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep125(final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
            // 118 can only be "INELIGIBLE" or "ELIGIBLE"
            if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                result = ENextStep.MSG_PLACE_OUT_117_118;
            } else {
                result = ENextStep.MSG_PLACE_OUT_118;
            }
        } else {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 126.
     *
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep126(final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (trajectory.math126 == ETrajectoryCourse.INELIGIBLE) {
            if (trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
                if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                    result = ENextStep.MSG_PLACE_OUT_117_118_125;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_118_125;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_125;
            }
        } else {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 141.
     *
     * @param stuStatus  the student status
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep141(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (stuStatus.isEligibleFor141()) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
            result = ENextStep.MSG_PLACE_OUT_117_118;
        } else {
            result = ENextStep.MSG_PLACE_OUT_118;
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 155.
     *
     * @param stuStatus  the student status
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep155(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (stuStatus.isEligibleFor155()) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else if (stuStatus.has125()) {
            // Student does not have 124
            if (trajectory.math124 == ETrajectoryCourse.INELIGIBLE) {
                // Student does not have 118 or 124
                if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                    // Student does not have 117, 118, or 124
                    result = ENextStep.MSG_PLACE_OUT_117_118_124;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_118_124;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_124;
            }
        } else if (stuStatus.has124(false)) {
            // Student does not have 125
            if (trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
                // Student does not have 118 or 125
                if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                    // Student does not have 117, 118, or 125
                    result = ENextStep.MSG_PLACE_OUT_117_118_125;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_118_125;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_125;
            }
        } else {
            // Student does not have 124 or 125
            if (trajectory.math124 == ETrajectoryCourse.INELIGIBLE
                || trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
                // Student does not have 118, 124, or 125
                if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                    // Student does not have 117, 118, 124, or 125
                    result = ENextStep.MSG_PLACE_OUT_117_118_124_125;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_118_124_125;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_124_125;
            }
        }

        return result;
    }

    /**
     * Computes the next steps to get the student ready to take MATH 160.
     *
     * @param stuStatus  the student status
     * @param trajectory the recommended trajectory
     * @return the next steps
     */
    private static ENextStep makeNextStep160(final StudentStatus stuStatus, final RecommendedTrajectory trajectory) {

        final ENextStep result;

        if (stuStatus.isEligibleFor156or160()) {
            result = ENextStep.MSG_ALREADY_ELIGIBLE;
        } else if (stuStatus.has124(true)) {
            // Must not have 126 at a B- or higher grade
            if (stuStatus.has126(false)) {
                result = ENextStep.MSG_PLACE_OUT_126;
            } else if (trajectory.math126 == ETrajectoryCourse.INELIGIBLE) {
                if (trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
                    if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                        result = ENextStep.MSG_PLACE_OUT_117_118_125_126;
                    } else {
                        result = ENextStep.MSG_PLACE_OUT_118_125_126;
                    }
                } else {
                    result = ENextStep.MSG_PLACE_OUT_125_126;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_126;
            }
        } else if (stuStatus.has126(true)) {
            // Must not have 124 at a B- or higher grade
            if (stuStatus.has124(false)) {
                result = ENextStep.MSG_PLACE_OUT_124;
            } else if (trajectory.math124 == ETrajectoryCourse.INELIGIBLE) {
                if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                    result = ENextStep.MSG_PLACE_OUT_117_118_124;
                } else {
                    result = ENextStep.MSG_PLACE_OUT_118_124;
                }
            } else {
                result = ENextStep.MSG_PLACE_OUT_124;
            }
        } else {
            // Does not have 124 or 126 with the B- grade
            if (trajectory.math126 == ETrajectoryCourse.INELIGIBLE) {
                if (trajectory.math125 == ETrajectoryCourse.INELIGIBLE) {
                    if (trajectory.math124 == ETrajectoryCourse.INELIGIBLE) {
                        if (trajectory.math118 == ETrajectoryCourse.INELIGIBLE) {
                            result = ENextStep.MSG_PLACE_OUT_117_118_124_125_126;
                        } else {
                            result = ENextStep.MSG_PLACE_OUT_118_124_125_126;
                        }
                    } else {
                        // Eligible for 124, which means should be eligible for 125
                        result = ENextStep.MSG_PLACE_OUT_124_125_126;
                    }
                } else {
                    // Eligible for 125, which means should be eligible for 124
                    result = ENextStep.MSG_PLACE_OUT_124_125_126;
                }
            } else {
                // Eligible for 126, which means should be eligible for 124
                result = ENextStep.MSG_PLACE_OUT_124_126;
            }
        }

        return result;
    }
}
