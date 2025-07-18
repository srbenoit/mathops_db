package dev.mathops.db.logic.mathplan;

/**
 * The status of a student with respect to the Math Plan.
 */
public enum EMathPlanStatus {

    /** The student has not started the Math Plan. */
    NOT_STARTED,

    /** The student has submitted majors of interest but not acknowledged that "this is only a recommendation". */
    SUBMITTED_MAJORS,

    /** The student has acknowledged that "this is only a recommendation", but not reviewed existing work. */
    CHECKED_ONLY_RECOMMENDATION,

    /** The student has reviewed existing work, but has not completed the plan */
    REVIEWED_EXISTING,

    /** The student has completed the plan and placement is not needed. */
    PLAN_COMPLETED_PLACEMENT_NOT_NEEDED,

    /** The student has completed the plan and placement is needed. */
    PLAN_COMPLETED_PLACEMENT_NEEDED,
}
