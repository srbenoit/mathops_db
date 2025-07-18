package dev.mathops.db.logic.mathplan;

import dev.mathops.db.old.rawrecord.RawRecordConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * A container for precalculus requirements (named and implicit).
 */
final class PrecalculusRequirement {

    /** Named courses required for a degree program of interest. */
    final EnumSet<EPrecalcCourse> namedCourses;

    /** Courses that are implicitly required to meet prerequisites of named courses. */
    final EnumSet<EPrecalcCourse> implicitCourses;

    /** Pick lists that the student should meet. */
    final List<PickList> pickLists;

    /** A flag indicating the student will need placement or a B- or higher in MATH 124 and 126. */
    boolean needsBMinusIn2426;

    /**
     * Constructs a {@code PrecalculusRequirement} with no requirements.
     */
    PrecalculusRequirement() {

        this.namedCourses = EnumSet.noneOf(EPrecalcCourse.class);
        this.implicitCourses = EnumSet.noneOf(EPrecalcCourse.class);
        this.pickLists = new ArrayList<>(3);
        this.needsBMinusIn2426 = false;
    }

    /**
     * Constructs a {@code PrecalculusRequirement} that computes the requirements based on a list of majors of interest
     * and a  calculus requirement.
     *
     * @param majors              the collection of majors
     * @param calculusRequirement the calculus requirement
     * @param stuStatus           the student's current status (used to adjust pick lists based on coursework already
     *                            completed)
     */
    PrecalculusRequirement(final Iterable<Major> majors, final ENamedCalculusRequirement calculusRequirement,
                           final StudentStatus stuStatus) {

        this();

        populateNamedRequirements(majors);
        populateImplicitRequirements(calculusRequirement);
        generatePickLists(majors, calculusRequirement, stuStatus);
    }

    /**
     * Populates {@code namedCourses} with requirements based on the list of majors.  After this call,
     * {@code namedCourses} will contain an entry for every course named in any major of interest.
     *
     * @param majors the list of majors of interest
     */
    private void populateNamedRequirements(final Iterable<Major> majors) {

        // Gather explicitly named requirements
        for (final Major major : majors) {
            if (major.requirements == ERequirement.M_117_101) {
                this.namedCourses.add(EPrecalcCourse.M_101);
                this.namedCourses.add(EPrecalcCourse.M_117);
            } else if (major.requirements == ERequirement.M_117_118_124
                       || major.requirements == ERequirement.M_117_118_124_141) {
                this.namedCourses.add(EPrecalcCourse.M_117);
                this.namedCourses.add(EPrecalcCourse.M_118);
                this.namedCourses.add(EPrecalcCourse.M_124);
            } else if (major.requirements == ERequirement.M_117_118_125) {
                this.namedCourses.add(EPrecalcCourse.M_117);
                this.namedCourses.add(EPrecalcCourse.M_118);
                this.namedCourses.add(EPrecalcCourse.M_125);
            } else if (major.requirements == ERequirement.M_117_118_124_125
                       || major.requirements == ERequirement.M_117_118_124_125_141_OR_155) {
                this.namedCourses.add(EPrecalcCourse.M_117);
                this.namedCourses.add(EPrecalcCourse.M_118);
                this.namedCourses.add(EPrecalcCourse.M_124);
                this.namedCourses.add(EPrecalcCourse.M_125);
            } else if (major.requirements == ERequirement.M_118_124_125
                       || major.requirements == ERequirement.M_118_124_125_141) {
                this.namedCourses.add(EPrecalcCourse.M_118);
                this.namedCourses.add(EPrecalcCourse.M_124);
                this.namedCourses.add(EPrecalcCourse.M_125);
            } else if (major.requirements == ERequirement.M_124_125_126_155) {
                this.namedCourses.add(EPrecalcCourse.M_124);
                this.namedCourses.add(EPrecalcCourse.M_125);
                this.namedCourses.add(EPrecalcCourse.M_126);
            } else if (major.requirements == ERequirement.M_126) {
                this.namedCourses.add(EPrecalcCourse.M_126);
            }
        }
    }

    /**
     * Populates {@code implicitCourses} with requirements based on the calculus requirements and the list of named
     * precalculus required courses.  After this call, {@code implicitCourses} will contain an entry for every course
     * implicitly required to meet a prerequisite for a course in {@code namedCourses} that does not already appear in
     * {@code namedCourses}.
     *
     * <p>
     * >The two sets {@code namedCourses} and {@code implicitCourses} are disjoint and their union is the complete set
     * of courses the student will either need to complete or place out of to be able to complete any of their majors of
     * interest.
     *
     * @param calculusRequirement the calculus requirement
     */
    private void populateImplicitRequirements(final ENamedCalculusRequirement calculusRequirement) {

        if (calculusRequirement == ENamedCalculusRequirement.M_160
            || calculusRequirement == ENamedCalculusRequirement.M_156
            || calculusRequirement == ENamedCalculusRequirement.M_156_OR_160) {

            this.implicitCourses.add(EPrecalcCourse.M_126);
            this.implicitCourses.add(EPrecalcCourse.M_125);
            this.implicitCourses.add(EPrecalcCourse.M_124);
            this.implicitCourses.add(EPrecalcCourse.M_118);
            this.implicitCourses.add(EPrecalcCourse.M_117);
            this.needsBMinusIn2426 = true;
        } else if (calculusRequirement == ENamedCalculusRequirement.M_155
                   || calculusRequirement == ENamedCalculusRequirement.M_155_OR_160) {
            this.implicitCourses.add(EPrecalcCourse.M_125);
            this.implicitCourses.add(EPrecalcCourse.M_124);
            this.implicitCourses.add(EPrecalcCourse.M_118);
            this.implicitCourses.add(EPrecalcCourse.M_117);
        } else if (calculusRequirement == ENamedCalculusRequirement.M_141
                   || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155
                   || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155_OR_160) {
            this.implicitCourses.add(EPrecalcCourse.M_118);
            this.implicitCourses.add(EPrecalcCourse.M_117);
        }

        if (this.namedCourses.contains(EPrecalcCourse.M_126)) {
            this.implicitCourses.add(EPrecalcCourse.M_125);
            this.implicitCourses.add(EPrecalcCourse.M_118);
            this.implicitCourses.add(EPrecalcCourse.M_117);
        }
        if (this.namedCourses.contains(EPrecalcCourse.M_124) || this.namedCourses.contains(EPrecalcCourse.M_125)) {
            this.implicitCourses.add(EPrecalcCourse.M_118);
            this.implicitCourses.add(EPrecalcCourse.M_117);
        }
        if (this.namedCourses.contains(EPrecalcCourse.M_118)) {
            this.implicitCourses.add(EPrecalcCourse.M_117);
        }

        this.implicitCourses.removeAll(this.namedCourses);
    }

    /**
     * Populates {@code pickLists} with pick lists, where we want the student to satisfy all pick lists.  These lists
     * will assume students will complete all named courses and the calculus requirement, and only represent what
     * remains from pick lists in the majors of interest.
     *
     * @param majors              the list of majors of interest
     * @param calculusRequirement the calculus requirement
     * @param stuStatus           the student's current status
     */
    private void generatePickLists(final Iterable<Major> majors, final ENamedCalculusRequirement calculusRequirement,
                                   final StudentStatus stuStatus) {

        final Collection<String> completed = stuStatus.getCompleted();

        for (final Major major : majors) {
            if (major.requirements == ERequirement.PICK_3CR_117_118_124_120) {

                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_155) {

                if (completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || calculusRequirement == ENamedCalculusRequirement.M_155
                    || calculusRequirement == ENamedCalculusRequirement.M_155_OR_160) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_125_141) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || calculusRequirement == ENamedCalculusRequirement.M_141
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_125);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126) {

                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120, EPrecalcCourse.M_125, EPrecalcCourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || calculusRequirement == ENamedCalculusRequirement.M_141
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120, EPrecalcCourse.M_125, EPrecalcCourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141_155) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || calculusRequirement == ENamedCalculusRequirement.M_141
                    || calculusRequirement == ENamedCalculusRequirement.M_155
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155
                    || calculusRequirement == ENamedCalculusRequirement.M_155_OR_160
                    || calculusRequirement == ENamedCalculusRequirement.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120, EPrecalcCourse.M_125, EPrecalcCourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || completed.contains(RawRecordConstants.M156) || completed.contains(RawRecordConstants.MATH156)
                    || completed.contains(RawRecordConstants.M160) || completed.contains(RawRecordConstants.MATH160)
                    || calculusRequirement != ENamedCalculusRequirement.NONE) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_117, EPrecalcCourse.M_118, EPrecalcCourse.M_124,
                        EPrecalcCourse.M_120, EPrecalcCourse.M_125, EPrecalcCourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_118_124_125_126_155_160) {

                if (completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || completed.contains(RawRecordConstants.M160) || completed.contains(RawRecordConstants.MATH160)
                    || calculusRequirement == ENamedCalculusRequirement.M_155
                    || calculusRequirement == ENamedCalculusRequirement.M_155_OR_160
                    || calculusRequirement == ENamedCalculusRequirement.M_160) {
                    continue;
                }
                addPickList(completed, EPrecalcCourse.M_118, EPrecalcCourse.M_124, EPrecalcCourse.M_125,
                        EPrecalcCourse.M_126);

            }
        }
    }

    /**
     * Examines a pick list and eliminates courses that are already named, reducing the credit count as courses are
     * removed.  If any credits are still needed, a {@code PickList} object is created with the number of credits
     * remaining and the set of allowed courses that are not already named.
     *
     * @param completed the set of courses for which the student already has credit
     * @param courses   the list of acceptable courses
     */
    private void addPickList(final Iterable<String> completed, final EPrecalcCourse... courses) {

        final PickList pick = new PickList(3, courses);

        // Remove courses for which the student already has credit
        for (final String courseId : completed) {
            if (RawRecordConstants.M117.equals(courseId) || RawRecordConstants.MATH117.equals(courseId)) {
                pick.remove(EPrecalcCourse.M_117);
            } else if (RawRecordConstants.M118.equals(courseId) || RawRecordConstants.MATH118.equals(courseId)) {
                pick.remove(EPrecalcCourse.M_118);
            } else if (RawRecordConstants.M124.equals(courseId) || RawRecordConstants.MATH124.equals(courseId)) {
                pick.remove(EPrecalcCourse.M_124);
            } else if (RawRecordConstants.M125.equals(courseId) || RawRecordConstants.MATH125.equals(courseId)) {
                pick.remove(EPrecalcCourse.M_125);
            } else if (RawRecordConstants.M126.equals(courseId) || RawRecordConstants.MATH126.equals(courseId)) {
                pick.remove(EPrecalcCourse.M_126);
            } else if (RawRecordConstants.M120.equals(courseId) || RawRecordConstants.MATH120.equals(courseId)
                       || RawRecordConstants.M127.equals(courseId) || RawRecordConstants.MATH127.equals(courseId)) {
                // Assume 127 will get counted like 120 (none of our pick lists are greater than 3 credits, so the
                // credit difference will not matter)
                pick.remove(EPrecalcCourse.M_120);
            }
        }

        // Remove courses that are "named" in precalculus requirements
        for (final EPrecalcCourse course : courses) {
            pick.remove(course);
        }

        if (pick.numCredits > 0) {
            this.pickLists.add(pick);
        }
    }
}
