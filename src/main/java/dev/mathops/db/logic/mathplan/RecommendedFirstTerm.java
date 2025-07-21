package dev.mathops.db.logic.mathplan;

import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.logic.mathplan.types.EIdealFirstTermType;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;
import dev.mathops.db.logic.mathplan.types.PickList;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * A calculated "recommended first term" registration set based on a collection of selected majors.  This object will
 * not take into account courses the student has already completed.
 *
 * <p>
 * We start with named courses.  For each input major that has "named" ideal first-year eligibility, we take that
 * major's ideal first term registration, accumulating this set for all named majors in the input list. The result will
 * be a set of "named" courses.
 *
 * <p>
 * Then we look at pick lists.  For each input major that has a pick list, we remove any from the pick list that are
 * already in the named list
 */
public final class RecommendedFirstTerm {

    /** The computed first term named requirements. */
    public final IdealFirstTerm firstTermNamed;

    /** The computed first term pick list requirements. */
    public final IdealFirstTerm firstTermPick;

    /**
     * Constructs a new {@code RecommendedFirstTerm} from a list of majors.
     *
     * @param majors the majors
     */
    RecommendedFirstTerm(final Collection<Major> majors) {

        if (majors == null || majors.isEmpty()) {
            this.firstTermNamed = IdealFirstTerm.UNDETERMINED;
            this.firstTermPick = IdealFirstTerm.UNDETERMINED;
        } else {
            boolean coreOnly = true;
            for (final Major major : majors) {
                if (major.idealFirstTerm.type != EIdealFirstTermType.CORE_ONLY) {
                    coreOnly = false;
                    break;
                }
            }

            if (coreOnly) {
                this.firstTermNamed = IdealFirstTerm.CORE_ONLY;
                this.firstTermPick = IdealFirstTerm.CORE_ONLY;
            } else {
                final EnumSet<ECourse> namedCourses = gatherNamedCourses(majors);
                final PickList pickCourses = gatherPickCourses(namedCourses, majors);

                this.firstTermNamed = new IdealFirstTerm(EIdealFirstTermType.NAMED_LIST, namedCourses);
                this.firstTermPick = new IdealFirstTerm(EIdealFirstTermType.PICK_LIST, pickCourses.courses);
            }
        }
    }

    /**
     * Scans a list of majors for all that have "named" ideal first-term courses and accumulates those.
     *
     * <p>
     * The possible input sets of named courses (from majors currently defined)
     * <ul>
     *     <li>117</li>
     *     <li>117 + 101</li>
     *     <li>117 + 118</li>
     *     <li>117 + 118 + 124</li>
     *     <li>117 + 118 + 125</li>
     *     <li>117 + 118 + 124 + 125</li>
     *     <li>117 + 118 + 125 + 126</li>
     *     <li>118 + 124 + 125</li>
     *     <li>124 + 125 + 126</li>
     *     <li>155</li>
     *     <li>156</li>
     *     <li>160</li>
     *     <li>M_155_OR_160</li>
     *     <li>M_156_OR_160</li>
     * </ul>
     *
     * @param majors the majors
     * @return the set of named ideal first-term courses(possibly empty)
     */
    private static EnumSet<ECourse> gatherNamedCourses(final Iterable<Major> majors) {

        final EnumSet<ECourse> result = EnumSet.noneOf(ECourse.class);

        for (final Major major : majors) {
            final IdealFirstTerm ideal = major.idealFirstTerm;
            if (ideal.type == EIdealFirstTermType.NAMED_LIST) {
                result.addAll(ideal.courses);
            }
        }

        return result;
    }

    /**
     * Scans a list of majors for all that have "pick list" or "course or higher" ideal first-term courses and
     * accumulates a combined pick list from those, eliminating those courses already in the "named courses" list.
     *
     * <p>
     * The set of possible pick lists or "course or higher" (from majors currently defined):
     * <ul>
     *     <li>117 or higher</li>
     *     <li>118 or higher</li>
     *     <li>Pick from 117 / 118 / 124 / 120 / 125 / 126 / 141</li>
     *     <li>Pick from 117 / 118 / 124 / 120 / 125 / 155</li>
     *     <li>Pick from 118 / 124 / 125 / 126 / 155 / 160</li>
     * </ul>
     *
     * @param namedCourses the set of named ideal first-year courses
     * @param majors       the majors
     * @return the set of pick-list ideal first-term courses (possibly empty)
     */
    private static PickList gatherPickCourses(final Collection<ECourse> namedCourses, final Iterable<Major> majors) {

        // Collect all pick lists from the set of majors, eliminating named courses from each
        final List<PickList> allPickLists = new ArrayList<>(10);

        for (final Major major : majors) {
            final IdealFirstTerm ideal = major.idealFirstTerm;
            PickList pick = null;
            if (ideal.type == EIdealFirstTermType.PICK_LIST) {
                pick = new PickList(3, ideal.courses);
            } else if (ideal.type == EIdealFirstTermType.COURSE_OR_HIGHER) {
                if (ideal.courses.contains(ECourse.M_117)) {
                    pick = new PickList(3, ECourse.M_117, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125,
                            ECourse.M_126, ECourse.M_141, ECourse.M_155, ECourse.M_156, ECourse.M_160);
                } else if (ideal.courses.contains(ECourse.M_118)) {
                    pick = new PickList(3, ECourse.M_118, ECourse.M_124, ECourse.M_120, ECourse.M_125, ECourse.M_126,
                            ECourse.M_141, ECourse.M_155, ECourse.M_156, ECourse.M_160);
                }
            }

            if (pick != null) {
                for (final ECourse named : namedCourses) {
                    pick.remove(named);
                }
                if (pick.numCredits > 0) {
                    allPickLists.add(pick);
                }
            }
        }

        final PickList result;

        final int numPickLists = allPickLists.size();
        if (numPickLists == 0) {
            // No courses need to be picked; return an empty list
            result = new PickList(0);
        } else if (numPickLists == 1) {
            // There was only one pick list that has not been satisfied by named courses - that's our result
            result = allPickLists.getFirst();
        } else {
            // Combine multiple pick lists into one pick list

            // Start by finding all courses (if any) that appear in all pick lists.
            final EnumSet<ECourse> inAllPickLists = EnumSet.allOf(ECourse.class);
            for (final PickList pick : allPickLists) {
                for (final ECourse test : ECourse.values()) {
                    if (!pick.courses.contains(test)) {
                        inAllPickLists.remove(test);
                    }
                }
            }

            // Now go through each pick list and see if this "set intersection" list would meet its requirements.
            final Iterator<PickList> iter = allPickLists.iterator();
            int maxCredits = 0;
            while (iter.hasNext()) {
                final PickList pick = iter.next();
                maxCredits = Math.max(maxCredits, pick.numCredits);
                for (final ECourse inAll : inAllPickLists) {
                    if (pick.remove(inAll) <= 0) {
                        iter.remove();
                        break;
                    }
                }
            }

            if (allPickLists.isEmpty()) {
                // The "set intersection" list is sufficient - that is our new pick list, with "maxCredits" credits
                result = new PickList(maxCredits, inAllPickLists);
            } else {
                // The "set intersection" list is not sufficient, so we add all those "set intersection" courses to the
                // set of named courses (they have already been removed from all the pick lists), and work out a new
                // "pick from" result to cover what's left

                final EnumSet<ECourse> additional = EnumSet.noneOf(ECourse.class);
                namedCourses.addAll(inAllPickLists);

                // Now we need to choose courses from pick lists until all of them are satisfied.
                for (final PickList pick : allPickLists) {
                    for (final ECourse alreadyAdded : additional) {
                        pick.remove(alreadyAdded);
                    }
                    while (pick.numCredits > 0) {
                        final ECourse lowest = pick.getLowest();
                        pick.remove(lowest);
                        additional.add(lowest);
                    }
                }

                int newMaxCredits = 0;
                for (final PickList pick : allPickLists) {
                    newMaxCredits = Math.max(newMaxCredits, pick.numCredits);
                }

                // The "additional" set should be sufficient.
                result = new PickList(newMaxCredits, additional);
            }
        }

        return result;
    }

    /**
     * Returns a text description of the ideal first-term eligibility in such a form that the text could be used to fill
     * the blank in "Ideally, you would be eligible to register for _____ in the [TERM] term."
     *
     * <p>
     * This method should not be used if this is a "CORE ONLY" requirement, but should still return reasonable text in
     * that case.
     *
     * @return the text
     */
    public String getText() {

        String result;

        if (this.firstTermNamed.type == EIdealFirstTermType.CORE_ONLY) {
            result = "three credits of AUCC 1B courses";
        } else {
            final boolean noNamed = this.firstTermNamed.courses.isEmpty();
            final boolean noPick = this.firstTermPick.courses.isEmpty();

            if (noNamed) {
                result = pickListText(); // "any of MATH 117, MATH 118 or MATH 120"
            } else if (noPick) {
                result = namedText(); // "MATH 117, MATH 118, and MATH 124"
            } else {
                final String named = namedText();
                final String pick = pickListText();
                result = SimpleBuilder.concat(named, " as well as ", pick);
            }
        }

        return result;
    }

    /**
     * Generates text to represent named requirements.
     *
     * @return the text
     */
    private String namedText() {

        final String result;

        final List<ECourse> courses = new ArrayList<>(this.firstTermNamed.courses);
        courses.sort(null);
        final int numCourses = courses.size();

        final ECourse first = courses.getFirst();
        final ECourse last = courses.getLast();

        if (numCourses == 1) {
            result = first.label;
        } else if (numCourses == 2) {
            result = SimpleBuilder.concat(first.label, " and ", last.label);
        } else {
            final HtmlBuilder htm = new HtmlBuilder(40);

            htm.add(first.label);

            for (int i = 1; i < numCourses - 1; ++i) {
                final ECourse course = courses.get(i);
                htm.add(", ", course.label);
            }

            htm.add(", and ", last.label);

            result = htm.toString();
        }

        return result;
    }

    /**
     * Generates text to represent a choice from a pick list.
     *
     * @return the text
     */
    private String pickListText() {

        final String result;

        final List<ECourse> courses = new ArrayList<>(this.firstTermPick.courses);
        courses.sort(null);
        final int numCourses = courses.size();

        final ECourse first = courses.getFirst();
        final ECourse last = courses.getLast();

        if (numCourses == 1) {
            result = first.label;
        } else if (numCourses == 2) {
            result = SimpleBuilder.concat(first.label, " or ", last.label);
        } else {
            final HtmlBuilder htm = new HtmlBuilder(40);

            htm.add("any of ");
            htm.add(first.label);

            for (int i = 1; i < numCourses - 1; ++i) {
                final ECourse course = courses.get(i);
                htm.add(", ", course.label);
            }

            htm.add(", or ", last.label);

            result = htm.toString();
        }

        return result;
    }
}
