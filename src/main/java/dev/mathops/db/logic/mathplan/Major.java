package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.TemporalUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * A math requirement associated with a major (major or major/concentration).
 */
public final class Major implements Comparable<Major> {

    /** The question numbers in the Math Plan that indicate the program is selected. */
    public final int[] questionNumbers;

    /** The program codes from CIM (like 'MAJR-CONC-BS'). */
    public final List<String> programCodes;

    /** The program name. */
    public final String programName;

    /** The catalog page URL. */
    public final String catalogPageUrl;

    /** How important it is for students to take math in their first semester. */
    public final EHowImportantIsMathFirstSemester importance;

    /** The list of courses the student would ideally be eligible for in their first semester. */
    public final List<EEligibility> idealEligibility;

    /**
     * Constructs a new {@code MajorMathRequirement}.
     */
    Major(final int[] theQuestionNumbers, final String[] theProgramCodes,
          final String theProgramName, final String theCatalogPageUrl,
          final EHowImportantIsMathFirstSemester theImportance,
          final EEligibility... theIdealEligibility) {

        this.questionNumbers = theQuestionNumbers.clone();
        this.programCodes = Arrays.asList(theProgramCodes);
        this.programName = theProgramName;
        this.catalogPageUrl = theCatalogPageUrl;
        this.importance = theImportance;
        this.idealEligibility = List.of(theIdealEligibility);
    }

    /**
     * Gets a hash code for this major.  Hash codes are based only on the list of program codes.
     *
     * @return the hash code
     */
    public int hashCode() {

        return this.programCodes.hashCode();
    }

    /**
     * Tests whether this object is equal to another.  Equality of this class of object is based on equality of the list
     * of program codes.
     *
     * @param o the other object
     * @return true if the objects are equal
     */
    public boolean equals(final Object o) {

        final boolean equal;

        if (o == this) {
            equal = true;
        } else if (o instanceof final Major oMajor) {
            equal = this.programCodes.equals(oMajor.programCodes);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Generates a string representation of the date range.
     */
    @Override
    public String toString() {

        return this.programName;
    }

    /**
     * Compares this major to another for order. Order is based first on the major name (compared as strings), then by
     * concentration name (as strings).
     *
     * @param other the other object against which to compare
     * @return 0 if {@code other} is equal to this major; a value less than 0 if this major is lexicographically less
     *         than {@code other}; and a value greater than 0 if this major is lexicographically greater than
     *         {@code other}
     */
    @Override
    public int compareTo(final Major other) {

        int result = this.programName.compareTo(other.programName);

        if (result == 0) {
            final String first = this.programCodes.getFirst();
            final String otherFirst = other.programCodes.getFirst();
            result = first.compareTo(otherFirst);
        }

        return result;
    }
}
