package dev.mathops.db.logic.mathplan.types;

/**
 * Possible next steps for a student.
 */
public enum ENextStep {

    /** Next steps cannot be determined. */
    UNDETERMINED("(Undetermined)"),

    /** Student just needs 1B - Placement is not needed. */
    MSG_PLACEMENT_NOT_NEEDED("(AUCC 1B only)"),

    /** Student needs more than just 1B, but is already eligible. */
    MSG_ALREADY_ELIGIBLE("(eligible)"),

    /** Student should try to place into MATH 117/120. */
    MSG_PLACE_INTO_117("Into 117"),

    /** Student should try to place out of MATH 117. */
    MSG_PLACE_OUT_117("Out of 117"),

    /** Student should try to place into MATH 118. */
    MSG_PLACE_INTO_118("Into 118"),

    /** Student should try to place out of MATH 118. */
    MSG_PLACE_OUT_118("Out of 118"),

    /** Student should try to place out of MATH 117 and 118. */
    MSG_PLACE_OUT_117_118("Out of 117, 118"),

    /** Student should try to place into MATH 125. */
    MSG_PLACE_INTO_125("Into 125"),

    /** Student should try to place out of MATH 125. */
    MSG_PLACE_OUT_125("Out of 125"),

    /** Student should try to place out of MATH 118 and 125. */
    MSG_PLACE_OUT_118_125("Out of 118, 125"),

    /** Student should try to place out of MATH 117, 118, and 125. */
    MSG_PLACE_OUT_117_118_125("Out of 117, 118, 125"),

    /** Student should try to place into MATH 155. */
    MSG_PLACE_INTO_155("Into 155"),

    /** Student should try to place out of 126. */
    MSG_PLACE_OUT_126("Out of 126"),

    /** Student should try to place out of 125 and 126. */
    MSG_PLACE_OUT_125_126("Out of 125 and 126"),

    /** Student should try to place out of 118, 124 and 125. */
    MSG_PLACE_OUT_118_124_125("Out of 118, 124, 125"),

    /** Student should try to place out of 118, 125 and 126. */
    MSG_PLACE_OUT_118_125_126("Out of 118, 125, 126"),

    /** Student should try to place out of 117, 118, 125 and 126. */
    MSG_PLACE_OUT_117_118_125_126("Out of 117, 118, 125, 126"),

    /** Student should try to place out of 124. */
    MSG_PLACE_OUT_124("Out of 124"),

    /** Student should try to place out of 118 and 124. */
    MSG_PLACE_OUT_118_124("Out of 118, 124"),

    /** Student should try to place out of 117, 118 and 124. */
    MSG_PLACE_OUT_117_118_124("Out of 117, 118, 124"),

    /** Student should try to place out of 117, 118, 124, and 125. */
    MSG_PLACE_OUT_117_118_124_125("Out of 117, 118, 124, 125"),

    /** Student should try to place out of 124 and 125. */
    MSG_PLACE_OUT_124_125("Out of 124, 125"),

    /** Student should try to place out of 124 and 126. */
    MSG_PLACE_OUT_124_126("Out of 124, 126"),

    /** Student should try to place out of 124, 125, and 126. */
    MSG_PLACE_OUT_124_125_126("Out of 124, 125, 126"),

    /** Student should try to place out of 118, 124, 125, and 126. */
    MSG_PLACE_OUT_118_124_125_126("Out of 118, 124, 125, 126"),

    /** Student should try to place out of 117, 118, 124, 125, and 126. */
    MSG_PLACE_OUT_117_118_124_125_126("Out of 117, 118, 124, 125, 126");

    /** The plan text - a summary for advisers. */
    public final String planText;

    /**
     * Constructs a new {@code ENextStep}.
     *
     * @param thePlanText the plan text
     */
    ENextStep(final String thePlanText) {

        this.planText = thePlanText;
    }
}
