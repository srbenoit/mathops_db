package dev.mathops.db.logic.mathplan;

/**
 * Possible course requirements.
 */
public enum ERequirement {

    /** 3 credits of any core course. */
    CORE_ONLY,

    /** MATH 117 + MATH 101. */
    M_117_101,

    /** MATH 117 + MATH 118 + MATH 124. */
    M_117_118_124,

    /** MATH 117 + MATH 118 + MATH 125. */
    M_117_118_125,

    /** MATH 117 + MATH 118 + MATH 124 + MATH 125. */
    M_117_118_124_125,

    /** MATH 117 + MATH 118 + MATH 124 + MATH 141. */
    M_117_118_124_141,

    /** MATH 118 + MATH 124 + MATH 125. */
    M_118_124_125,

    /** MATH 118 + MATH 124 + MATH 125 + MATH 141. */
    M_118_124_125_141,

    /** MATH 124 + MATH 125 + MATH 126 + MATH 155. */
    M_124_125_126_155,

    /** MATH 126. */
    M_126,

    /** MATH 117 + MATH 118 + MATH 124 + MATH 125 + (MATH 141 or MATH 155). */
    M_117_118_124_125_141_OR_155,

    /** MATH 141. */
    M_141,

    /** MATH 141 or MATH 155 or MATH 160. */
    M_141_OR_155_OR_160,

    /** MATH 155. */
    M_155,

    /** MATH 155 or MATH 160. */
    M_155_OR_160,

    /** MATH 156. */
    M_156,

    /** MATH 156 or MATH 160. */
    M_156_OR_160,

    /** MATH 160. */
    M_160,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, or MATH 120. */
    PICK_3CR_117_118_124_120,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, MATH 120, or MATH 155 */
    PICK_3CR_117_118_124_120_155,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 125, or MATH 141 */
    PICK_3CR_117_118_125_141,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, MATH 120, MATH 125, or MATH 126. */
    PICK_3CR_117_118_124_120_125_126,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, MATH 120, MATH 125, MATH 126, or MATH 141 */
    PICK_3CR_117_118_124_120_125_126_141,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, MATH 120, MATH 125, MATH 126, MATH 141, or MATH 155 */
    PICK_3CR_117_118_124_120_125_126_141_155,

    /** Pick 3 credits from MATH 117, MATH 118, MATH 124, MATH 120, MATH 125, MATH 126, any calculus. */
    PICK_3CR_117_118_124_120_125_126_141_155_156_160,

    /** Pick 3 credits from MATH 118, MATH 124, MATH 125, MATH 126, MATH 155, or MATH 160 */
    PICK_3CR_118_124_125_126_155_160,
}
