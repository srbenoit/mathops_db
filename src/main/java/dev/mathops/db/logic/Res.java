package dev.mathops.db.logic;

import dev.mathops.commons.res.ResBundle;

import java.util.Locale;

/**
 * Localized resources.
 */
final class Res extends ResBundle {

    // Used by DateRange

    /** A resource key. */
    static final String DAT_END_PRECEDES_START = key(1);

    //

    /** The resources - an array of key-values pairs. */
    private static final String[][] EN_US = { //

            {DAT_END_PRECEDES_START, "End date may not precede start date."},
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
