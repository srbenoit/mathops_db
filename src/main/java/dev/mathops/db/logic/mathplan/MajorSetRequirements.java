package dev.mathops.db.logic.mathplan;

import java.util.Collection;

/**
 * A class that calculates and stores the cumulative requirements from a collection of majors.
 */
final class MajorSetRequirements {

    /** A flag indicating the student only needs 3 credits of core. */
    public final boolean coreOnly;

    /** The named calculus requirement. */
    public final ENamedCalculusRequirement namedCalculusRequirement;

    /** The precalculus requirement. */
    public final PrecalculusRequirement precalculusRequirement;

    /**
     * Constructs a new {@code MajorSetRequirements}.
     *
     * @param majors        the collection of majors
     * @param stuStatus the student's current status
     */
    MajorSetRequirements(final Collection<Major> majors, final StudentStatus stuStatus) {

        if (isAllCore(majors)) {
            this.coreOnly = true;
            this.namedCalculusRequirement = ENamedCalculusRequirement.NONE;
            this.precalculusRequirement = new PrecalculusRequirement();
        } else {
            this.coreOnly = false;
            this.namedCalculusRequirement = determineNamedCalculusRequirement(majors);
            this.precalculusRequirement = new PrecalculusRequirement(majors, this.namedCalculusRequirement,
                    stuStatus);
        }
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
     * Determines the cumulative "named calculus requirement" for all majors in a collection.
     *
     * @param majors the collection of majors
     * @return the cumulative named calculus requirement
     */
    private static ENamedCalculusRequirement determineNamedCalculusRequirement(final Iterable<Major> majors) {

        boolean m160 = false;
        boolean m156 = false;
        boolean m155 = false;
        boolean m141 = false;
        boolean m156or160 = false;
        boolean m155or160 = false;
        boolean m141or155 = false;
        boolean m141or155or160 = false;

        // Gather explicitly named requirements
        for (final Major major : majors) {
            if (major.requirements == ERequirement.M_117_118_124_141
                || major.requirements == ERequirement.M_118_124_125_141
                || major.requirements == ERequirement.M_141) {
                m141 = true;
            } else if (major.requirements == ERequirement.M_124_125_126_155
                       || major.requirements == ERequirement.M_155) {
                m155 = true;
            } else if (major.requirements == ERequirement.M_117_118_124_125_141_OR_155) {
                m141or155 = true;
            } else if (major.requirements == ERequirement.M_141_OR_155_OR_160) {
                m141or155or160 = true;
            } else if (major.requirements == ERequirement.M_155_OR_160) {
                m155or160 = true;
            } else if (major.requirements == ERequirement.M_156) {
                m156 = true;
            } else if (major.requirements == ERequirement.M_156_OR_160) {
                m156or160 = true;
            } else if (major.requirements == ERequirement.M_160) {
                m160 = true;
            }
        }

        final ENamedCalculusRequirement result;

        if (m160) {
            result = ENamedCalculusRequirement.M_160;
        } else if (m156) {
            result = m155 || m155or160 ? ENamedCalculusRequirement.M_160 : ENamedCalculusRequirement.M_156;
        } else if (m156or160) {
            result = m155 || m155or160 ? ENamedCalculusRequirement.M_160 : ENamedCalculusRequirement.M_156_OR_160;
        } else if (m155) {
            result = ENamedCalculusRequirement.M_155;
        } else if (m155or160) {
            result = ENamedCalculusRequirement.M_155_OR_160;
        } else if (m141) {
            result = ENamedCalculusRequirement.M_141;
        } else if (m141or155) {
            result = ENamedCalculusRequirement.M_141_OR_155;
        } else if (m141or155or160) {
            result = ENamedCalculusRequirement.M_141_OR_155_OR_160;
        } else {
            result = ENamedCalculusRequirement.NONE;
        }

        return result;
    }

}

