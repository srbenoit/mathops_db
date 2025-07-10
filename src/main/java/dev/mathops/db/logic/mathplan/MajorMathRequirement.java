package dev.mathops.db.logic.mathplan;

/**
 * A math requirement associated with a major (major or major/concentration).
 */
public final class MajorMathRequirement {

    /** The question numbers in the Math Plan that indicate the program is selected. */
    public final int[] questionNumbers;

    /** The program codes from CIM (like 'MAJR-CONC-BS'). */
    public final String[] programCodes;

    /** The program name. */
    public final String programName;

    /** The catalog page URL. */
    public final String catalogPageUrl;

    /** How important it is for students to take math in their first semester. */
    public final EHowImportantIsMathFirstSemester importance;

    /** The list of courses that would be ideal for students to take in their first semester. */
    public final String[] idealFirstSemesterCourses;

    /**
     * Constructs a new {@code MajorMathRequirement}.
     */
    MajorMathRequirement(final int[] theQuestionNumbers, final String[] theProgramCodes,
                         final String theProgramName, final String theCatalogPageUrl,
                         final EHowImportantIsMathFirstSemester theImportance,
                         final String[] theIdealFirstSemesterCourses) {

        this.questionNumbers = theQuestionNumbers.clone();
        this.programCodes = theProgramCodes.clone();
        this.programName = theProgramName;
        this.catalogPageUrl = theCatalogPageUrl;
        this.importance = theImportance;
        this.idealFirstSemesterCourses = theIdealFirstSemesterCourses.clone();
    }
}
