package dev.mathops.db.logic.placement;

import dev.mathops.commons.res.ResBundle;

import java.util.Locale;

/**
 * Localized resources.
 */
final class Res extends ResBundle {

    // Used by PlacementLogic

    /** A resource key. */
    static final String PLC_CANT_LOOKUP_RECORD_L = key(1);

    /** A resource key. */
    static final String PLC_CANT_LOOKUP_RECORD_S = key(2);

    /** A resource key. */
    static final String PLC_NO_ATTEMPTS_REMAIN_L = key(3);

    /** A resource key. */
    static final String PLC_NO_ATTEMPTS_REMAIN_S = key(4);

    /** A resource key. */
    static final String PLC_WHY_FIRST_YEAR = key(5);

    /** A resource key. */
    static final String PLC_WHY_CSU_ONLINE = key(6);

    /** A resource key. */
    static final String PLC_WHY_ALL_ATTEMPTS_USED = key(7);

    /** A resource key. */
    static final String PLC_ERR_NO_DATE_RANGES = key(8);

    /** A resource key. */
    static final String PLC_WHY_PAST_LAST_DATE = key(9);

    //

    /** The resources - an array of key-values pairs. */
    private static final String[][] EN_US = {

            {PLC_CANT_LOOKUP_RECORD_L, "Unable to look up placement record."},
            {PLC_CANT_LOOKUP_RECORD_S, "Lookup failed"},
            {PLC_NO_ATTEMPTS_REMAIN_L, "You have no attempts remaining on the Math Placement Tool."},
            {PLC_NO_ATTEMPTS_REMAIN_S, "No attempts remain"},
            {PLC_WHY_FIRST_YEAR, "you are an incoming first-year student"},
            {PLC_WHY_CSU_ONLINE, "you are registered through CSU Online"},
            {PLC_WHY_ALL_ATTEMPTS_USED, "You have used your unproctored attempt on the Math Placement Tool."},
            {PLC_ERR_NO_DATE_RANGES, "ERROR: Unproctored allowed but no date ranges: {0}"},
            {PLC_WHY_PAST_LAST_DATE, "You are no longer eligible to complete the Math Placement Tool without "
                                     + "proctoring. Your last date of eligibility was {0}."},
            {PLC_WHY_PAST_LAST_DATE,
                    "You will be eligible to complete the Math Placement Tool without proctoring starting {0}."},
    };

    /** The singleton instance. */
    private static final Res instance = new Res();

    /**
     * Private constructor to prevent direct instantiation.
     */
    private Res() {

        super(Locale.US, EN_US);
    }

    /**
     * Gets the message with a specified key using the current locale.
     *
     * @param key the message key
     * @return the best-matching message, an empty string if none is registered (never {@code null})
     */
    public static String get(final String key) {

        return instance.getMsg(key);
    }

    /**
     * Retrieves the message with a specified key, then uses a {@code MessageFormat} to format that message pattern with
     * a collection of arguments.
     *
     * @param key       the message key
     * @param arguments the arguments, as for {@code MessageFormat}
     * @return the formatted string (never {@code null})
     */
    public static String fmt(final String key, final Object... arguments) {

        return instance.formatMsg(key, arguments);
    }
}
