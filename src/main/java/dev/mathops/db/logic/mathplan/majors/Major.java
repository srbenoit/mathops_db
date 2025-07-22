package dev.mathops.db.logic.mathplan.majors;

import dev.mathops.db.logic.mathplan.types.EMajorTrack;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A math requirement associated with a major (major or major/concentration).
 */
public final class Major implements Comparable<Major> {

    /** The question numbers in the Math Plan that indicate the program is selected. */
    public final int[] questionNumbers;

    /** The program codes from CIM (like 'MAJR-CONC-BS'). */
    public final List<String> programCodes;

    /** The major tracks in which the major participates. */
    public final EnumSet<EMajorTrack> majorTracks;

    /** The program name. */
    public final String programName;

    /** The catalog page URL. */
    public final String catalogPageUrl;

    /** The Math requirements for the major. */
    public final ERequirement requirements;

    /** The "ideal" courses in which a student would enroll in their first term. */
    public final IdealFirstTerm idealFirstTerm;

    /**
     * Constructs a new {@code Major}.
     *
     * @param theQuestionNumbers the array of question numbers in the Math Plan that indicate the program is selected
     * @param theProgramCodes    the array of CIM program codes for the major and concentrations
     * @param theMajorTracks     the major tracks in which the major participates
     * @param theProgramName     the name of the major (displayed in the major selection page)
     * @param theCatalogPageUrl  the URL to a descriptive page for the major
     * @param theRequirements    the  required math courses in the major
     * @param theIdealFirstTerm  the "ideal" courses for which student will be eligible in the first semester
     */
    public Major(final int[] theQuestionNumbers, final String[] theProgramCodes, final EMajorTrack[] theMajorTracks,
                 final String theProgramName, final String theCatalogPageUrl, final ERequirement theRequirements,
                 final IdealFirstTerm theIdealFirstTerm) {

        this.questionNumbers = theQuestionNumbers.clone();
        this.programCodes = Arrays.asList(theProgramCodes);
        this.majorTracks = EnumSet.noneOf(EMajorTrack.class);
        if (theMajorTracks != null && theMajorTracks.length > 0) {
            Collections.addAll(this.majorTracks, theMajorTracks);
        }
        this.programName = theProgramName;
        this.catalogPageUrl = theCatalogPageUrl;
        this.requirements = theRequirements;
        this.idealFirstTerm = theIdealFirstTerm;
    }

    /**
     * Constructs a new {@code Major}.
     *
     * @param theQuestionNumbers the array of question numbers in the Math Plan that indicate the program is selected
     * @param theProgramCodes    the array of CIM program codes for the major and concentrations
     * @param theMajorTracks     the major tracks in which the major participates
     * @param theProgramName     the name of the major (displayed in the major selection page)
     * @param theCatalogPageUrl  the URL to a descriptive page for the major
     * @param theRequirements    the  required math courses in the major
     * @param theIdealFirstTerm  the "ideal" courses for which student will be eligible in the first semester
     */
    private Major(final int[] theQuestionNumbers, final Collection<String> theProgramCodes,
                  final Collection<EMajorTrack> theMajorTracks, final String theProgramName,
                  final String theCatalogPageUrl, final ERequirement theRequirements,
                  final IdealFirstTerm theIdealFirstTerm) {

        this.questionNumbers = theQuestionNumbers.clone();
        this.programCodes = new ArrayList<>(theProgramCodes);
        this.majorTracks = EnumSet.copyOf(theMajorTracks);
        this.programName = theProgramName;
        this.catalogPageUrl = theCatalogPageUrl;
        this.requirements = theRequirements;
        this.idealFirstTerm = theIdealFirstTerm;
    }

    /**
     * Creates a clone of this major with a different program name.
     *
     * @param newName the new program name
     * @return the clone
     */
    public Major cloneWithNewName(final String newName) {

        return new Major(this.questionNumbers, this.programCodes, this.majorTracks, newName,
                this.catalogPageUrl, this.requirements, this.idealFirstTerm);
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

        // Uppercase so "Environmental and Natural..." sorts before "Environmental Horticulture"
        final String myName = this.programName.toUpperCase(Locale.ROOT);
        final String otherName = other.programName.toUpperCase(Locale.ROOT);

        int result = myName.compareTo(otherName);

        if (result == 0) {
            final String first = this.programCodes.getFirst();
            final String otherFirst = other.programCodes.getFirst();
            result = first.compareTo(otherFirst);
        }

        return result;
    }
}
