package dev.mathops.db.logic.mathplan;

import dev.mathops.db.old.rawrecord.RawRecordConstants;

/**
 * Possible next steps for a student.
 */
public enum ENextStep {

    //
    //
    //

    // Student just needs 1B - Placement is not needed
    MSG_PLACEMENT_NOT_NEEDED,

    // Student needs more than just 1B, but is already eligible
    MSG_ALREADY_ELIGIBLE,

    // Student should try to place into MATH 117/120
    MSG_PLACE_INTO_117,

    // Student should try to place out of MATH 117
    MSG_PLACE_OUT_117,

    // Student should try to place into MATH 118
    MSG_PLACE_INTO_118,

    // Student should try to place out of MATH 118
    MSG_PLACE_OUT_118,

    // Student should try to place out of MATH 117 and 118
    MSG_PLACE_OUT_117_118,

    // Student should try to place into MATH 125
    MSG_PLACE_INTO_125,

    // Student should try to place out of MATH 125
    MSG_PLACE_OUT_125,

    // Student should try to place out of MATH 118 and 125
    MSG_PLACE_OUT_118_125,

    // Student should try to place out of MATH 117, 118, and 125
    MSG_PLACE_OUT_117_118_125,

    // Student should try to place into MATH 155
    MSG_PLACE_INTO_155,




    //
    //
    //

    /** A. Action: [Tell me more about the Math Placement Exam]. */
    ACT_MATH_PLACEMENT_EXAM,

    /** B. Action: [Tell me more about the ELM Tutorial]. */
    ACT_ELM_TUTORIAL,

    /** C. Action: [Tell me more about the Precalculus Tutorial]. */
    ACT_PRECALCULUS_TUTORIAL,

    /** D. Action: [Contact my adviser]. */
    ACT_CONTACT_MY_ADVISER,
}
