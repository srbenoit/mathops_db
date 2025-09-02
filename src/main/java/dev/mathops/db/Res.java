package dev.mathops.db;

import dev.mathops.commons.res.ResBundle;

import java.util.Locale;

/**
 * Localized resources.
 */
final class Res extends ResBundle {

    // Used by DbConnection

    /** A resource key. */
    static final String DB_CONN_REG_IFX = key(1);

    /** A resource key. */
    static final String DB_CONN_REG_IFX_FAIL = key(2);

    /** A resource key. */
    static final String DB_CONN_REG_ORA = key(3);

    /** A resource key. */
    static final String DB_CONN_REG_ORA_FAIL = key(4);

    /** A resource key. */
    static final String DB_CONN_REG_PG = key(5);

    /** A resource key. */
    static final String DB_CONN_REG_PG_FAIL = key(6);

    //

    /** The resources - an array of key-values pairs. */
    private static final String[][] EN_US = {

            {DB_CONN_REG_IFX, "Registering Informix JDBC driver"},
            {DB_CONN_REG_IFX_FAIL, "Failed to register Informix JDBC driver"},
            {DB_CONN_REG_ORA, "Registering Oracle JDBC driver"},
            {DB_CONN_REG_ORA_FAIL, "Failed to register Oracle JDBC driver"},
            {DB_CONN_REG_PG, "Registering PostgreSQL JDBC driver"},
            {DB_CONN_REG_PG_FAIL, "Failed to register PostgreSQL JDBC driver"},
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
    static String get(final String key) {

        return instance.getMsg(key);
    }
}
