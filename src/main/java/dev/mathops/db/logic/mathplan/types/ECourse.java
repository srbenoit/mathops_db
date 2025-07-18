package dev.mathops.db.logic.mathplan.types;

/**
 * All courses that participate in the Math Plan.
 */
public enum ECourse {

    /** A sentinel value used to indicate no course. */
    NONE(0),

    /** MATH 101. */
    M_101(3),

    /** MATH 117. */
    M_117(1),

    /** MATH 118. */
    M_118(1),

    /** MATH 120 (used only in pick lists). */
    M_120(3),

    /** MATH 124. */
    M_124(1),

    /** MATH 125. */
    M_125(1),

    /** MATH 126. */
    M_126(1),

    /** MATH 141. */
    M_141(3),

    /** MATH 141 or MATH 155. */
    M_141_OR_155(3),

    /** MATH 141 or MATH 155 or MATH 160. */
    M_141_OR_155_OR_160(3),

    /** MATH 155. */
    M_155(4),

    /** MATH 155 or MATH 160. */
    M_155_OR_160(4),

    /** MATH 156. */
    M_156(4),

    /** MATH 156 or MATH 160. */
    M_156_OR_160(4),

    /** MATH 160. */
    M_160(4);

    /** The number of credits (or least number of credits for a choice). */
    public final int credits;

    /**
     * Constructs a new {@code ECourse}.
     *
     * @param theCredits the number of credits (or least number of credits for a choice)
     */
    ECourse(final int theCredits) {
        this.credits = theCredits;
    }
}
