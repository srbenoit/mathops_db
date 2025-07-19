package dev.mathops.db.logic.mathplan.types;

/**
 * A type of ideal first-term course list.
 */
public enum EIdealFirstTermType {

    /** Student only needs core courses. */
    CORE_ONLY,

    /** Student would ideally register for all courses on the list. */
    NAMED_LIST,

    /** Student would ideally pick at least one of the courses on the list. */
    PICK_LIST,

    /** Student would ideally pick the (single) listed course or any higher course. */
    COURSE_OR_HIGHER,
}
