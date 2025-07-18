package dev.mathops.db.logic.mathplan;

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
    public final List<EPrecalcCourse> courses;

    /**
     * Constructs a new {@code PickList}
     *
     * @param theNumCredits the number of credits
     * @param theCourses    the courses
     */
    PickList(final int theNumCredits, final EPrecalcCourse... theCourses) {

        this.numCredits = theNumCredits;

        final int count = theCourses.length;
        this.courses = new ArrayList<>(count);
        Collections.addAll(this.courses, theCourses);
    }

    /**
     * Removes a course from the pick list and (if that course was actually in the list), decreases the number of
     * credits remaining to be completed by the number of credits of the removed course.
     */
    void remove(final EPrecalcCourse course) {
        if (this.courses.remove(course)) {
            this.numCredits -= course.credits;
        }
    }
}
