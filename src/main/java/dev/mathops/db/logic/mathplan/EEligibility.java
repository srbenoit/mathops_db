package dev.mathops.db.logic.mathplan;

/**
 * Possible eligibility goals.
 */
public enum EEligibility {

    /** Any AUCC 1B course. */
    AUCC(0),

    /** Eligible for MATH 117 or MATH 120. */
    M_117_120(10),

    /** Eligible for MATH 118. */
    M_118(20),

    /** Eligible for MATH 124. */
//    M_124(30),

    /** Eligible for MATH 125. */
    M_125(40),

    /** Eligible for MATH 126. */
//    M_126(50),

    /** Eligible for MATH 141. */
//    M_141(35),

    /** Eligible for MATH 155. */
    M_155(60),

    /** Eligible for MATH 155 or MATH 160. */
    M_155_160(65),

    /** Eligible for MATH 156. */
//    M_156(90),

    /** Eligible for MATH 156 or MATH 160. */
    M_156_160(95),

    /** Eligible for MATH 160. */
    M_160(100);

    /** A level to allow us to identify the "highest" requirement. */
    public final int level;

    /**
     * Constructs a new {@code EEligibility}.
     *
     * @param theLevel the level
     */
    EEligibility(final int theLevel) {

        this.level = theLevel;
    }
}
