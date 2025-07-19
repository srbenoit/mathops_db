package dev.mathops.db.logic.mathplan.types;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

/**
 * A container for the "ideal" set of courses for which the student will be eligible to register in their first term.
 */
public final class IdealFirstTerm {

    /** An ideal registrations object for majors that require only core courses. */
    public static final IdealFirstTerm CORE_ONLY = new IdealFirstTerm(EIdealFirstTermType.CORE_ONLY);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_17_OR_HIGHER = new IdealFirstTerm(EIdealFirstTermType.COURSE_OR_HIGHER,
            ECourse.M_117);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_18_OR_HIGHER = new IdealFirstTerm(EIdealFirstTermType.COURSE_OR_HIGHER,
            ECourse.M_118);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_17 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_117);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_1701 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_117,
            ECourse.M_101);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_1718 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_117,
            ECourse.M_118);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_171824 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_117,
            ECourse.M_118, ECourse.M_124);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_17182425 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST,
            ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_125);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_171825 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_117,
            ECourse.M_118, ECourse.M_125);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_17182526 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST,
            ECourse.M_117, ECourse.M_118, ECourse.M_125, ECourse.M_126);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_182425 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_118,
            ECourse.M_124, ECourse.M_125);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_242526 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_124,
            ECourse.M_125, ECourse.M_126);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_55 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_155);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_56 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_156);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_60 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, ECourse.M_160);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_55_OR_60 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST,
            ECourse.M_155_OR_160);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_56_OR_60 = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST,
            ECourse.M_156_OR_160);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_PICK_17_TO_41 = new IdealFirstTerm(EIdealFirstTermType.PICK_LIST,
            ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125, ECourse.M_126, ECourse.M_141);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_PICK_17_TO_55 = new IdealFirstTerm(EIdealFirstTermType.PICK_LIST,
            ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125, ECourse.M_155);

    /** An ideal registrations object. */
    public static final IdealFirstTerm IDEAL_PICK_18_TO_60 = new IdealFirstTerm(EIdealFirstTermType.PICK_LIST,
            ECourse.M_118, ECourse.M_124, ECourse.M_125, ECourse.M_126, ECourse.M_155, ECourse.M_160);

    /** The type of ideal course registration. */
    public final EIdealFirstTermType type;

    /** The "ideal" courses in which a student would enroll in their first term. */
    public final EnumSet<ECourse> courses;

    /**
     * Constructs a new {@code IdealFirstTerm}.
     *
     * @param theType    the type of the ideal first term registrations.
     * @param theCourses the "ideal" course for which to be eligible before the first semester"
     */
    public IdealFirstTerm(final EIdealFirstTermType theType, final ECourse... theCourses) {

        if (theType == null) {
            throw new IllegalArgumentException("First term registration type may not be null");
        }
        this.type = theType;
        this.courses = EnumSet.noneOf(ECourse.class);

        if (theType == EIdealFirstTermType.CORE_ONLY) {
            if (theCourses != null && theCourses.length > 0) {
                throw new IllegalArgumentException("When type is CORE_ONLY, listed first-term courses are not allowed");
            }
        } else if (theType == EIdealFirstTermType.COURSE_OR_HIGHER) {
            if (theCourses == null || theCourses.length != 1) {
                throw new IllegalArgumentException("When type is COURSE_OR_HIGHER, exactly 1 course must be provided");
            }
            this.courses.add(theCourses[0]);
        } else {
            if (theCourses == null || theCourses.length == 0) {
                throw new IllegalArgumentException("At least one first-term course must be provided");
            }
            Collections.addAll(this.courses, theCourses);
        }
    }

    /**
     * Constructs a new {@code IdealFirstTerm}.
     *
     * @param theType    the type of the ideal first term registrations.
     * @param theCourses the "ideal" course for which to be eligible before the first semester"
     */
    public IdealFirstTerm(final EIdealFirstTermType theType, final Collection<ECourse> theCourses) {

        if (theType == null) {
            throw new IllegalArgumentException("First term registration type may not be null");
        }
        this.type = theType;
        this.courses = EnumSet.noneOf(ECourse.class);

        if (theCourses == null) {
            if (theType != EIdealFirstTermType.CORE_ONLY) {
                throw new IllegalArgumentException("Course collection may not be null unless type is CORE_ONLY.");
            }
        } else {
            final int count = theCourses.size();

            if (theType == EIdealFirstTermType.CORE_ONLY) {
                if (count > 0) {
                    throw new IllegalArgumentException(
                            "When type is CORE_ONLY, listed first-term courses are not allowed");
                }
            } else {
                if (theType == EIdealFirstTermType.COURSE_OR_HIGHER) {
                    if (count != 1) {
                        throw new IllegalArgumentException(
                                "When type is COURSE_OR_HIGHER, exactly 1 course must be provided");
                    }
                } else if (count == 0) {
                    throw new IllegalArgumentException("At least one first-term course must be provided");

                }
                this.courses.addAll(theCourses);
            }
        }
    }

    /**
     * Gets a hash code for this major.  Hash codes are based only on the list of program codes.
     *
     * @return the hash code
     */
    public int hashCode() {

        return this.type.hashCode() + this.courses.hashCode();
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
        } else if (o instanceof final IdealFirstTerm other) {
            equal = this.type == other.type && this.courses.equals(other.courses);
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

        return this.type + " (" + this.courses + ")";
    }
}
