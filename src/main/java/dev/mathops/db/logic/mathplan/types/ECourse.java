package dev.mathops.db.logic.mathplan.types;

/**
 * All courses that participate in the Math Plan.
 */
public enum ECourse {

    /** A sentinel value used to indicate no course. */
    NONE(0, ""),

    /** MATH 101. */
    M_101(3, "MATH 101"),

    /** MATH 117. */
    M_117(1, "MATH 117"),

    /** MATH 118. */
    M_118(1, "MATH 118"),

    /** MATH 120 (used only in pick lists). */
    M_120(3, "MATH 120"),

    /** MATH 124. */
    M_124(1, "MATH 124"),

    /** MATH 125. */
    M_125(1, "MATH 125"),

    /** MATH 126. */
    M_126(1, "MATH 126"),

    /** MATH 127 (used only in pick lists). */
    M_127(3, "MATH 127"),

    /** MATH 141. */
    M_141(3, "MATH 141"),

    /** MATH 141 or MATH 155. */
    M_141_OR_155(3, "MATH 141 or MATH 155"),

    /** MATH 141 or MATH 155 or MATH 160. */
    M_141_OR_155_OR_160(3, "MATH 141 or MATH 155 or MATH 160"),

    /** MATH 155. */
    M_155(4, "MATH 155"),

    /** MATH 155 or MATH 160. */
    M_155_OR_160(4, "MATH 155 or MATH 160"),

    /** MATH 156. */
    M_156(4, "MATH 156"),

    /** MATH 156 or MATH 160. */
    M_156_OR_160(4, "MATH 156 or MATH 160"),

    /** MATH 160. */
    M_160(4, "MATH 160"),

    /** MATH 002. */
    M_002(3, "MATH 002"),

    /** MATH 157. */
    M_157(3, "MATH 157"),

    /** MATH 159. */
    M_159(3, "MATH 159"),

    /** MATH 161. */
    M_161(4, "MATH 161"),

    /** MATH 229. */
    M_229(2, "MATH 229"),

    /** MATH 269. */
    M_269(2, "MATH 269");

    /** The number of credits (or least number of credits for a choice). */
    public final int credits;

    /** A text label for the course. */
    public final String label;

    /**
     * Constructs a new {@code ECourse}.
     *
     * @param theCredits the number of credits (or least number of credits for a choice)
     * @param theLabel   a text label for the course
     */
    ECourse(final int theCredits, final String theLabel) {

        this.credits = theCredits;
        this.label = theLabel;
    }
}
