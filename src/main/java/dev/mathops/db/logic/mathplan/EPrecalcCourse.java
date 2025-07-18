package dev.mathops.db.logic.mathplan;

/**
 * Precalculus courses (including MATH 101) that can appear in degree requirements.
 */
public enum EPrecalcCourse {

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
    M_126(1);

    /** The number of credits. */
    public final int credits;

    /**
     * Constructs a new {@code EPrecalcCourse}.
     *
     * @param theCredits the number of credits
     */
    EPrecalcCourse(final int theCredits) {
        this.credits = theCredits;
    }
}
