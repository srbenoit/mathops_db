package dev.mathops.db.logic.mathplan.types;

import dev.mathops.commons.log.Log;
import dev.mathops.db.old.rawrecord.RawRecordConstants;

import java.util.EnumSet;

/**
 * All courses that participate in the Math Plan.
 */
public enum ECourse {

    /** A sentinel value used to indicate no course. */
    NONE(0, ""),

    /** MATH 101. */
    M_101(3, "MATH 101"),

    /** MATH 117. */
    M_117(1, "MATH 117"),

    /** MATH 118. */
    M_118(1, "MATH 118"),

    /** MATH 120 (used only in pick lists). */
    M_120(3, "MATH 120"),

    /** MATH 124. */
    M_124(1, "MATH 124"),

    /** MATH 125. */
    M_125(1, "MATH 125"),

    /** MATH 126. */
    M_126(1, "MATH 126"),

    /** MATH 127 (used only in pick lists). */
    M_127(3, "MATH 127"),

    /** MATH 141. */
    M_141(3, "MATH 141"),

    /** MATH 141 or MATH 155. */
    M_141_OR_155(3, "MATH 141 or MATH 155"),

    /** MATH 141 or MATH 155 or MATH 160. */
    M_141_OR_155_OR_160(3, "MATH 141 or MATH 155 or MATH 160"),

    /** MATH 155. */
    M_155(4, "MATH 155"),

    /** MATH 155 or MATH 160. */
    M_155_OR_160(4, "MATH 155 or MATH 160"),

    /** MATH 156. */
    M_156(4, "MATH 156"),

    /** MATH 156 or MATH 160. */
    M_156_OR_160(4, "MATH 156 or MATH 160"),

    /** MATH 160. */
    M_160(4, "MATH 160"),

    /** MATH 002. */
    M_002(3, "MATH 002"),

    /** MATH 157. */
    M_157(3, "MATH 157"),

    /** MATH 159. */
    M_159(3, "MATH 159"),

    /** MATH 161. */
    M_161(4, "MATH 161"),

    /** MATH 229. */
    M_229(2, "MATH 229"),

    /** MATH 269. */
    M_269(2, "MATH 269");

    /** The number of credits (or least number of credits for a choice). */
    public final int credits;

    /** A text label for the course. */
    public final String label;

    /**
     * Constructs a new {@code ECourse}.
     *
     * @param theCredits the number of credits (or least number of credits for a choice)
     * @param theLabel   a text label for the course
     */
    ECourse(final int theCredits, final String theLabel) {

        this.credits = theCredits;
        this.label = theLabel;
    }

    /**
     * Given a list of String course IDs, returns the set of all ECourse objects that correspond to any ID in that
     * list.
     *
     * @param courseIdList the course ID list
     * @return the set of ECourse objects
     */
    public static EnumSet<ECourse> courseIdListToECourseSet(final Iterable<String> courseIdList) {

        final EnumSet<ECourse> result = EnumSet.noneOf(ECourse.class);

        for (final String courseId : courseIdList) {
            final String fixed = courseId.replace("MATH ", "M ");

            if (RawRecordConstants.M101.equals(fixed) || "M 130".equals(fixed)) {
                result.add(ECourse.M_101);
            } else if (RawRecordConstants.M117.equals(fixed)) {
                result.add(ECourse.M_117);
            } else if (RawRecordConstants.M118.equals(fixed)) {
                result.add(ECourse.M_118);
            } else if (RawRecordConstants.M124.equals(fixed)) {
                result.add(ECourse.M_124);
            } else if (RawRecordConstants.M120.equals(fixed)) {
                result.add(ECourse.M_120);
            } else if (RawRecordConstants.M125.equals(fixed)) {
                result.add(ECourse.M_125);
            } else if (RawRecordConstants.M126.equals(fixed)) {
                result.add(ECourse.M_126);
            } else if (RawRecordConstants.M127.equals(fixed)) {
                result.add(ECourse.M_127);
            } else if (RawRecordConstants.M141.equals(fixed)) {
                result.add(ECourse.M_141);
            } else if (RawRecordConstants.M155.equals(fixed)) {
                result.add(ECourse.M_155);
            } else if (RawRecordConstants.M156.equals(fixed)) {
                result.add(ECourse.M_156);
            } else if (RawRecordConstants.M160.equals(fixed)) {
                result.add(ECourse.M_160);
            } else if (RawRecordConstants.M002.equals(fixed)) {
                result.add(ECourse.M_002);
            } else if ("M 157".equals(fixed)) {
                result.add(ECourse.M_157);
            } else if ("M 159".equals(fixed)) {
                result.add(ECourse.M_159);
            } else if ("M 161".equals(fixed)) {
                result.add(ECourse.M_161);
            } else if ("M 229".equals(fixed)) {
                result.add(ECourse.M_229);
            } else if ("M 269".equals(fixed)) {
                result.add(ECourse.M_269);
            } else {
                Log.warning("Completed course not recognized: ", courseId);
            }
        }

        return result;
    }
}
