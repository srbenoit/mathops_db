package dev.mathops.db.logic.mathplan.majors;

import dev.mathops.commons.log.Log;
import dev.mathops.text.builder.HtmlBuilder;

/**
 * A utility class to look up majors.
 */
public enum Majors {
    ;

    /**
     * Attempts to look up a {@code Major} by its program code, checking first in current majors, then in deactivated
     * majors.
     *
     * @param programCode the program code
     * @return the major; null if none found or the given program code is null
     */
    public static Major getMajorByProgramCode(final String programCode) {

        Major result = null;

        if (programCode != null) {
            result = MajorsCurrent.INSTANCE.getMajor(programCode);
            if (result == null) {
                result = MajorsDeactivated.INSTANCE.getMajor(programCode);

                if (result == null) {
                    // Do some processing to try to pick up "dual degree" annotations
                    final int ddIndex = programCode.indexOf("-DD-");

                    if (ddIndex > 0) {
                        final String newCode = programCode.substring(0, ddIndex) + programCode.substring(ddIndex + 3);
                        result = MajorsCurrent.INSTANCE.getMajor(newCode);
                        if (result == null) {
                            result = MajorsDeactivated.INSTANCE.getMajor(newCode);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Attempts to look up a {@code Major} by its numeric code (question number), checking first in current majors, then
     * in deactivated majors.
     *
     * @param numericCode the numeric code
     * @return the major; null if none found
     */
    public static Major getMajorByNumericCode(final int numericCode) {

        Major result = MajorsCurrent.INSTANCE.getMajor(numericCode);
        if (result == null) {
            result = MajorsDeactivated.INSTANCE.getMajor(numericCode);
        }

        return result;
    }
}
