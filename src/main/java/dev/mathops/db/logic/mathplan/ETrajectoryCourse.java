package dev.mathops.db.logic.mathplan;

/**
 * The status if a single course in a course trajectory.
 */
public enum ETrajectoryCourse {

    /** The course is not included in the trajectory. */
    NOT_NEEDED,

    /** The course is completed for credit and has a sufficient grade. */
    COMPLETED_FOR_CREDIT_SUFFICIENT_GRADE,

    /** The course is completed but at a C or less and the student needs a B- or higher grade. */
    COMPLETED_BUT_NEED_B,

    /** The student is eligible for the course. */
    ELIGIBLE,

    /** The student has not yet satisfied the prerequisites for the course. */
    INELIGIBLE,
}
