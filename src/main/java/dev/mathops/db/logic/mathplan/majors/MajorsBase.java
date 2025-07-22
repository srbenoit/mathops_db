package dev.mathops.db.logic.mathplan.majors;

import dev.mathops.commons.log.Log;
import dev.mathops.db.logic.mathplan.types.ECourse;
import dev.mathops.db.logic.mathplan.types.EIdealFirstTermType;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base class for collections of majors
 */
class MajorsBase {

    /** The list of all majors. */
    private final List<Major> majors;

    /**
     * Constructs a new {@code MajorsBase}.
     */
    MajorsBase(final int size) {

        this.majors = new ArrayList<>(size);
    }

    /**
     * Gets the list of majors.
     *
     * @return the list of majors
     */
    final List<Major> innerGetMajors() {

        return this.majors;
    }

    /**
     * Gets an unmodifiable view of the list of majors.
     *
     * @return the list of majors
     */
    public final List<Major> getMajors() {

        return Collections.unmodifiableList(this.majors);
    }

    /**
     * Gets the major with a specified program code.
     *
     * @param programCode the program code
     * @return the major; {@code null} if none matches the program code
     */
    public final Major getMajor(final String programCode) {

        Major result = null;

        for (final Major major : this.majors) {
            boolean found = false;
            for (final String code : major.programCodes) {
                if (code.equals(programCode)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                result = major;
                break;
            }
        }

        return result;
    }

    /**
     * Gets the major with a specified numeric code.
     *
     * @param numericCode the numeric code
     * @return the major; {@code null} if none matches the program code
     */
    public final Major getMajor(final int numericCode) {

        Major result = null;

        for (final Major major : this.majors) {
            boolean found = false;
            for (final int nbr : major.questionNumbers) {
                if (nbr == numericCode) {
                    found = true;
                    break;
                }
            }
            if (found) {
                result = major;
                break;
            }
        }

        return result;
    }
}
