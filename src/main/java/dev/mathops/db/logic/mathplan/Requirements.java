package dev.mathops.db.logic.mathplan;

import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.PickList;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.text.builder.HtmlBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * A container for requirements (named and implicit).
 */
public final class Requirements {

    /** A flag indicating the student only needs 3 credits of core. */
    public final boolean coreOnly;

    /** The single named calculus requirement that should be used to recommend student readiness. */
    public final ECourse namedCalculusRequirement;

    /** Named courses required for a degree program of interest. */
    public final EnumSet<ECourse> namedPrecalculus;

    /** Courses that are implicitly required to meet prerequisites of named courses. */
    public final EnumSet<ECourse> implicitCourses;

    /** Pick lists that the student should meet. */
    public final List<PickList> pickLists;

    /** A flag indicating the student will need placement or a B- or higher in MATH 124 and 126. */
    public boolean needsBMinusIn2426;

    /** The recommended first term eligibility. */
    public final RecommendedFirstTerm firstTerm;

    /**
     * Constructs a {@code Requirements} object that indicates nothing is required.
     */
    public Requirements() {

        this.namedPrecalculus = EnumSet.noneOf(ECourse.class);
        this.implicitCourses = EnumSet.noneOf(ECourse.class);

        this.coreOnly = true;
        this.namedCalculusRequirement = ECourse.NONE;
        this.pickLists = new ArrayList<>(0);
        this.needsBMinusIn2426 = false;

        this.firstTerm = new RecommendedFirstTerm(new ArrayList<>(0));
    }

    /**
     * Constructs a {@code Requirements} object that computes requirements based on a list of majors of interest.
     *
     * @param majors    the collection of majors
     * @param stuStatus the student's current status (used to adjust pick lists based on coursework already completed)
     */
    Requirements(final Collection<Major> majors, final StudentStatus stuStatus) {

        this.namedPrecalculus = EnumSet.noneOf(ECourse.class);
        this.implicitCourses = EnumSet.noneOf(ECourse.class);
        this.coreOnly = isAllCore(majors);

        if (this.coreOnly) {
            this.namedCalculusRequirement = ECourse.NONE;
            this.pickLists = new ArrayList<>(0);
            this.needsBMinusIn2426 = false;
        } else {
            this.needsBMinusIn2426 = false;
            this.namedCalculusRequirement = populateNamedRequirements(majors);
            populateImplicitRequirements();

            this.pickLists = new ArrayList<>(3);
            generatePickLists(majors, stuStatus);
        }

        this.firstTerm = new RecommendedFirstTerm(majors);
    }

    /**
     * Tests whether no requirements are indicated.
     *
     * @return the list of requirements is empty
     */
    public boolean isEmpty() {

        return this.namedCalculusRequirement == ECourse.NONE && this.pickLists.isEmpty();
    }

    /**
     * Tests whether all majors in a collection have "CORE_ONLY" as their requirements.
     *
     * @param majors the collection of majors
     * @return true if there are no majors that have something other than a core requirement (or if the collection is
     *         empty)
     */
    private static boolean isAllCore(final Iterable<Major> majors) {

        boolean allCore = true;

        for (final Major major : majors) {
            if (major.requirements != ERequirement.CORE_ONLY) {
                allCore = false;
                break;
            }
        }

        return allCore;
    }

    /**
     * Populates {@code namedCourses} with requirements based on the list of majors.  After this call,
     * {@code namedCourses} will contain an entry for every course named in any major of interest.
     *
     * @param majors the list of majors of interest
     * @return the named Calculus requirement (could be NONE)
     */
    private ECourse populateNamedRequirements(final Iterable<Major> majors) {

        final List<ECourse> calc = new ArrayList<>(3);

        // Gather explicitly named requirements
        for (final Major major : majors) {
            if (major.requirements == ERequirement.M_117_101) {
                this.namedPrecalculus.add(ECourse.M_101);
                this.namedPrecalculus.add(ECourse.M_117);
            } else if (major.requirements == ERequirement.M_117_118_124) {
                this.namedPrecalculus.add(ECourse.M_117);
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
            } else if (major.requirements == ERequirement.M_117_118_125) {
                this.namedPrecalculus.add(ECourse.M_117);
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_125);
            } else if (major.requirements == ERequirement.M_117_118_124_125) {
                this.namedPrecalculus.add(ECourse.M_117);
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
                this.namedPrecalculus.add(ECourse.M_125);
            } else if (major.requirements == ERequirement.M_117_118_124_141) {
                this.namedPrecalculus.add(ECourse.M_117);
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
                calc.add(ECourse.M_141);
            } else if (major.requirements == ERequirement.M_118_124_125) {
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
                this.namedPrecalculus.add(ECourse.M_125);
            } else if (major.requirements == ERequirement.M_118_124_125_141) {
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
                this.namedPrecalculus.add(ECourse.M_125);
                calc.add(ECourse.M_141);
            } else if (major.requirements == ERequirement.M_124_125_126_155) {
                this.namedPrecalculus.add(ECourse.M_124);
                this.namedPrecalculus.add(ECourse.M_125);
                this.namedPrecalculus.add(ECourse.M_126);
                calc.add(ECourse.M_155);
            } else if (major.requirements == ERequirement.M_126) {
                this.namedPrecalculus.add(ECourse.M_126);
            } else if (major.requirements == ERequirement.M_117_118_124_125_141_OR_155) {
                this.namedPrecalculus.add(ECourse.M_117);
                this.namedPrecalculus.add(ECourse.M_118);
                this.namedPrecalculus.add(ECourse.M_124);
                this.namedPrecalculus.add(ECourse.M_125);
                calc.add(ECourse.M_141_OR_155);
            } else if (major.requirements == ERequirement.M_141) {
                calc.add(ECourse.M_141);
            } else if (major.requirements == ERequirement.M_141_OR_155_OR_160) {
                calc.add(ECourse.M_141_OR_155_OR_160);
            } else if (major.requirements == ERequirement.M_155) {
                calc.add(ECourse.M_155);
            } else if (major.requirements == ERequirement.M_155_OR_160) {
                calc.add(ECourse.M_155_OR_160);
            } else if (major.requirements == ERequirement.M_156) {
                calc.add(ECourse.M_156);
            } else if (major.requirements == ERequirement.M_156_OR_160) {
                calc.add(ECourse.M_156_OR_160);
            } else if (major.requirements == ERequirement.M_160) {
                calc.add(ECourse.M_160);
            }
        }

        return computeNamedCalculus(calc);
    }

    /**
     * Given a list with the named calculus requirements from all selected majors, determine the single named calculus
     * option to use to recommend student readiness.
     *
     * @param calc the list of calculus requirements from all selected majors (could be empty)
     * @return the single calculus requirement (could be NONE)
     */
    private static ECourse computeNamedCalculus(final List<ECourse> calc) {

        final ECourse namedCalculus;

        final int numNamedCalc = calc.size();
        if (numNamedCalc == 1) {
            namedCalculus = calc.getFirst();
        } else if (numNamedCalc > 1) {
            if (calc.contains(ECourse.M_160)) {
                namedCalculus = ECourse.M_160;
            } else if (calc.contains(ECourse.M_156)) {
                namedCalculus = ECourse.M_156;
            } else if (calc.contains(ECourse.M_156_OR_160)) {
                namedCalculus = ECourse.M_156_OR_160;
            } else if (calc.contains(ECourse.M_155)) {
                namedCalculus = ECourse.M_155;
            } else if (calc.contains(ECourse.M_155_OR_160)) {
                namedCalculus = ECourse.M_155_OR_160;
            } else if (calc.contains(ECourse.M_141)) {
                namedCalculus = ECourse.M_141;
            } else if (calc.contains(ECourse.M_141_OR_155)) {
                namedCalculus = ECourse.M_141_OR_155;
            } else if (calc.contains(ECourse.M_141_OR_155_OR_160)) {
                namedCalculus = ECourse.M_141_OR_155_OR_160;
            } else {
                namedCalculus = ECourse.NONE;
            }
        } else {
            namedCalculus = ECourse.NONE;
        }

        return namedCalculus;
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
     */
    private void populateImplicitRequirements() {

        if (this.namedCalculusRequirement == ECourse.M_160
            || this.namedCalculusRequirement == ECourse.M_156
            || this.namedCalculusRequirement == ECourse.M_156_OR_160) {

            this.implicitCourses.add(ECourse.M_126);
            this.implicitCourses.add(ECourse.M_125);
            this.implicitCourses.add(ECourse.M_124);
            this.implicitCourses.add(ECourse.M_118);
            this.implicitCourses.add(ECourse.M_117);
            this.needsBMinusIn2426 = true;
        } else if (this.namedCalculusRequirement == ECourse.M_155
                   || this.namedCalculusRequirement == ECourse.M_155_OR_160) {
            this.implicitCourses.add(ECourse.M_125);
            this.implicitCourses.add(ECourse.M_124);
            this.implicitCourses.add(ECourse.M_118);
            this.implicitCourses.add(ECourse.M_117);
        } else if (this.namedCalculusRequirement == ECourse.M_141
                   || this.namedCalculusRequirement == ECourse.M_141_OR_155
                   || this.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
            this.implicitCourses.add(ECourse.M_118);
            this.implicitCourses.add(ECourse.M_117);
        }

        if (this.namedPrecalculus.contains(ECourse.M_126)) {
            this.implicitCourses.add(ECourse.M_125);
            this.implicitCourses.add(ECourse.M_118);
            this.implicitCourses.add(ECourse.M_117);
        }
        if (this.namedPrecalculus.contains(ECourse.M_124) || this.namedPrecalculus.contains(ECourse.M_125)) {
            this.implicitCourses.add(ECourse.M_118);
            this.implicitCourses.add(ECourse.M_117);
        }
        if (this.namedPrecalculus.contains(ECourse.M_118)) {
            this.implicitCourses.add(ECourse.M_117);
        }

        this.implicitCourses.removeAll(this.namedPrecalculus);
    }

    /**
     * Populates {@code pickLists} with pick lists, where we want the student to satisfy all pick lists.  These lists
     * will assume students will complete all named courses and the calculus requirement, and only represent what
     * remains from pick lists in the majors of interest.
     *
     * @param majors    the list of majors of interest
     * @param stuStatus the student's current status
     */
    private void generatePickLists(final Iterable<Major> majors, final StudentStatus stuStatus) {

        final Collection<ECourse> completed = stuStatus.getCompleted();

        for (final Major major : majors) {

            if (major.requirements == ERequirement.PICK_3CR_117_118_124_120) {

                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_155) {

                if (completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || this.namedCalculusRequirement == ECourse.M_155
                    || this.namedCalculusRequirement == ECourse.M_155_OR_160) {
                    continue;
                }
                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_125_141) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || this.namedCalculusRequirement == ECourse.M_141
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_125);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126) {

                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125,
                        ECourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || this.namedCalculusRequirement == ECourse.M_141
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125,
                        ECourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141_155) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || this.namedCalculusRequirement == ECourse.M_141
                    || this.namedCalculusRequirement == ECourse.M_155
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155
                    || this.namedCalculusRequirement == ECourse.M_155_OR_160
                    || this.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
                    continue;
                }
                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125,
                        ECourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160) {

                if (completed.contains(RawRecordConstants.M141) || completed.contains(RawRecordConstants.MATH141)
                    || completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || completed.contains(RawRecordConstants.M156) || completed.contains(RawRecordConstants.MATH156)
                    || completed.contains(RawRecordConstants.M160) || completed.contains(RawRecordConstants.MATH160)
                    || this.namedCalculusRequirement != ECourse.NONE) {
                    continue;
                }
                addPickList(completed, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125,
                        ECourse.M_126);

            } else if (major.requirements == ERequirement.PICK_3CR_118_124_125_126_155_160) {

                if (completed.contains(RawRecordConstants.M155) || completed.contains(RawRecordConstants.MATH155)
                    || completed.contains(RawRecordConstants.M160) || completed.contains(RawRecordConstants.MATH160)
                    || this.namedCalculusRequirement == ECourse.M_155
                    || this.namedCalculusRequirement == ECourse.M_155_OR_160
                    || this.namedCalculusRequirement == ECourse.M_160) {
                    continue;
                }
                addPickList(completed, ECourse.M_118, ECourse.M_124, ECourse.M_125, ECourse.M_126);
            }
        }
    }

    /**
     * Examines a pick list and eliminates courses that are already named, reducing the credit count as courses are
     * removed.  If any credits are still needed, a {@code PickList} object is created with the number of credits
     * remaining and the set of allowed courses that are not already named.
     *
     * @param completed         the set of courses for which the student already has credit
     * @param acceptableCourses the list of acceptable courses
     */
    private void addPickList(final Iterable<ECourse> completed, final ECourse... acceptableCourses) {

        final PickList pick = new PickList(3, acceptableCourses);

        // Remove courses for which the student already has credit
        for (final ECourse courseId : completed) {
            if (courseId == ECourse.M_127) {
                // No pick lists have 127 - treat this as MATH 120 for purposes of pick lists
                pick.remove(ECourse.M_120);
            } else {
                pick.remove(courseId);
            }
        }

        // Remove courses that are "named" in precalculus requirements
        for (final ECourse course : this.namedPrecalculus) {
            pick.remove(course);
        }

        if (pick.numCredits > 0) {
            this.pickLists.add(pick);
        }
    }

    /**
     * Determines the first "named course" - this is the course for which a student should try to become eligible. If
     * there is a named choice like "MATH 155 or MATH 160", we choose the course with the lowest prerequisites.
     *
     * @return the first named course; null if there are no named courses
     */
    public ECourse identifyFirstNamedCourse() {

        // Find the first named course that has a prerequisite (there may not be any)
        ECourse firstNamed = null;

        if (this.namedPrecalculus.contains(ECourse.M_117)) {
            firstNamed = ECourse.M_117;
        } else if (this.namedPrecalculus.contains(ECourse.M_118)) {
            firstNamed = ECourse.M_118;
        } else if (this.namedPrecalculus.contains(ECourse.M_124)) {
            firstNamed = ECourse.M_124;
        } else if (this.namedPrecalculus.contains(ECourse.M_125)) {
            firstNamed = ECourse.M_125;
        } else if (this.namedPrecalculus.contains(ECourse.M_126)) {
            firstNamed = ECourse.M_126;
        } else if (this.namedCalculusRequirement == ECourse.M_141
                   || this.namedCalculusRequirement == ECourse.M_141_OR_155
                   || this.namedCalculusRequirement == ECourse.M_141_OR_155_OR_160) {
            firstNamed = ECourse.M_141;
        } else if (this.namedCalculusRequirement == ECourse.M_155
                   || this.namedCalculusRequirement == ECourse.M_155_OR_160) {
            firstNamed = ECourse.M_155;
        } else if (this.namedCalculusRequirement == ECourse.M_156
                   || this.namedCalculusRequirement == ECourse.M_156_OR_160) {
            firstNamed = ECourse.M_156;
        } else if (this.namedCalculusRequirement == ECourse.M_160) {
            firstNamed = ECourse.M_160;
        }

        return firstNamed;
    }

    /**
     * Generates a diagnostic String representation of this object.
     *
     * @return the string representation
     */
    public String toString() {

        final HtmlBuilder htm = new HtmlBuilder(100);

        if (this.coreOnly) {
            htm.add("3 Credits of AUCC 1B core");
        } else {
            boolean comma = false;
            if (!this.namedPrecalculus.isEmpty()) {
                for (final ECourse course : this.namedPrecalculus) {
                    if (comma) {
                        htm.add(", ");
                    }
                    htm.add(course.label);
                    comma = true;
                }
            }
            if (this.namedCalculusRequirement != ECourse.NONE) {
                if (comma) {
                    htm.add(", ");
                }
                htm.add(this.namedCalculusRequirement.label);
                comma = true;
            }
            if (!this.implicitCourses.isEmpty()) {
                if (comma) {
                    htm.add(" ");
                }
                htm.add("[implicit: ");
                comma = false;
                for (final ECourse course : this.implicitCourses) {
                    if (comma) {
                        htm.add(", ");
                    }
                    htm.add(course.label);
                    comma = true;
                }
                htm.add("]");
                comma = true;
            }
            if (!this.pickLists.isEmpty()) {
                for (final PickList pick : this.pickLists) {
                    if (comma) {
                        htm.add(", ");
                    }
                    htm.add(pick);
                    comma = true;
                }
            }
            if (this.needsBMinusIn2426) {
                if (comma) {
                    htm.add(", ");
                }
                htm.add("(Needs B- or higher in 124/126)");
            }
        }

        return htm.toString();
    }
}
