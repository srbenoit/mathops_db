package dev.mathops.db;

/**
 * Enumeration of supported database uses.
 */
public enum EDbUse {

    /** Production data. */
    PRODUCTION("prod"),

    /** Development data. */
    DEVELOPMENT("dev"),

    /** TEst data. */
    TESTING("test"),

    /** Live registration database. */
    LIVE_PRODUCTION("live"),

    /** Live registration database, development instance. */
    LIVE_DEVELOPMENT("live_dev"),

    /** Operational data store. */
    ODS_PRODUCTION("ods"),

    /** Operational data store, development instance. */
    ODS_DEVELOPMENT("ods_dev");

    /** The use name. */
    public final String name;

    /**
     * Constructs a new {@code EDbUse}.
     *
     * @param theName the use name
     */
    EDbUse(final String theName) {

        this.name = theName;
    }

    /**
     * Finds the {@code EDbUse} with a particular name.
     *
     * @param theName the name
     * @return the corresponding {@code EDbUse}; {@code null} if none corresponds to the name
     */
    public static EDbUse forName(final String theName) {

        final EDbUse[] values = values();
        EDbUse result = null;

        for (final EDbUse value : values) {
            if (value.name.equals(theName)) {
                result = value;
                break;
            }
        }

        return result;
    }
}
