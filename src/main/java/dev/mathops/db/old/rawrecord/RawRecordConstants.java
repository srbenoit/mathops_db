package dev.mathops.db.old.rawrecord;

/**
 * A utility class with constants commonly used in raw records.
 */
public enum RawRecordConstants {
    ;

    /** A commonly used string. */
    public static final String M002 = "M 002";

    /** A commonly used string. */
    public static final String M101 = "M 101";

    /** A commonly used string. */
    public static final String M116 = "M 116";

    /** A commonly used string. */
    public static final String M117 = "M 117";

    /** A commonly used string. */
    public static final String M118 = "M 118";

    /** A commonly used string. */
    public static final String M120 = "M 120";

    /** A commonly used string. */
    public static final String M124 = "M 124";

    /** A commonly used string. */
    public static final String M125 = "M 125";

    /** A commonly used string. */
    public static final String M126 = "M 126";

    /** A commonly used string. */
    public static final String MATH101 = "MATH 101";

    /** A commonly used string. */
    public static final String MATH117 = "MATH 117";

    /** A commonly used string. */
    public static final String MATH118 = "MATH 118";

    /** A commonly used string. */
    public static final String MATH124 = "MATH 124";

    /** A commonly used string. */
    public static final String MATH125 = "MATH 125";

    /** A commonly used string. */
    public static final String MATH126 = "MATH 126";

    /** A commonly used string. */
    public static final String MATH120 = "MATH 120";

    /** A commonly used string. */
    public static final String MATH127 = "MATH 127";

    /** A commonly used string. */
    public static final String MATH141 = "MATH 141";

    /** A commonly used string. */
    public static final String MATH155 = "MATH 155";

    /** A commonly used string. */
    public static final String MATH156 = "MATH 156";

    /** A commonly used string. */
    public static final String MATH160 = "MATH 160";

    /** A commonly used string. */
    public static final String M127 = "M 127";

    /** A commonly used string. */
    public static final String M141 = "M 141";

    /** A commonly used string. */
    public static final String M155 = "M 155";

    /** A commonly used string. */
    public static final String M156 = "M 156";

    /** A commonly used string. */
    public static final String M160 = "M 160";

    /** A commonly used string. */
    public static final String M100C = "M 100C";

    /** A commonly used string. */
    public static final String M100U = "M 100U";

    /** A commonly used string. */
    public static final String M100T = "M 100T";

    /** A commonly used string. */
    public static final String M100P = "M 100P";

    /** A commonly used string. */
    public static final String M1170 = "M 1170";

    /** A commonly used string. */
    public static final String M1180 = "M 1180";

    /** A commonly used string. */
    public static final String M1240 = "M 1240";

    /** A commonly used string. */
    public static final String M1250 = "M 1250";

    /** A commonly used string. */
    public static final String M1260 = "M 1260";


    /**
     * Tests whether a course ID represents one of the 1-credit course that count toward pace.
     *
     * @param courseId the course ID
     * @return true if the ID represents a 1-credit course
     */
    public static boolean isOneCreditCourse(final String courseId) {

        return RawRecordConstants.M117.equals(courseId)
               || RawRecordConstants.M118.equals(courseId)
               || RawRecordConstants.M124.equals(courseId)
               || RawRecordConstants.M125.equals(courseId)
               || RawRecordConstants.M126.equals(courseId)
               || RawRecordConstants.MATH117.equals(courseId)
               || RawRecordConstants.MATH118.equals(courseId)
               || RawRecordConstants.MATH124.equals(courseId)
               || RawRecordConstants.MATH125.equals(courseId)
               || RawRecordConstants.MATH126.equals(courseId);
    }
}
