package dev.mathops.db.logic.mathplan.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A pick list with a number of credits and a list of allowed courses.
 */
public final class PickList {

    /** The number of credits. */
    public int numCredits;

    /** The courses from which to select. */
    public final List<ECourse> courses;

    /**
     * Constructs a new {@code PickList}
     *
     * @param theNumCredits the number of credits
     * @param theCourses    the courses
     */
    public PickList(final int theNumCredits, final ECourse... theCourses) {

        this.numCredits = theNumCredits;

        final int count = theCourses.length;
        this.courses = new ArrayList<>(count);
        Collections.addAll(this.courses, theCourses);
    }

    /**
     * Removes a course from the pick list and (if that course was actually in the list), decreases the number of
     * credits remaining to be completed by the number of credits of the removed course.
     *
     * @param course the course to remove
     */
    public void remove(final ECourse course) {

        if (this.courses.remove(course)) {
            this.numCredits -= course.credits;
        }
    }

    /**
     * Gets the "lowest" course in the pick list, using ordinal order in the ECourse enumeration as the metric.
     *
     * @return the "lowest" course
     */
    public ECourse getLowest() {

        ECourse lowest;

        if (this.courses.isEmpty()) {
            lowest = ECourse.NONE;
        } else {
            lowest = this.courses.getFirst();
            for (final ECourse test : this.courses) {
                if (test.ordinal() < lowest.ordinal()) {
                    lowest = test;
                }
            }
        }

        return lowest;
    }
}
