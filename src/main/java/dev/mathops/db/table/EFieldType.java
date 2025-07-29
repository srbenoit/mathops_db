package dev.mathops.db.table;

import dev.mathops.commons.log.Log;
import dev.mathops.text.builder.SimpleBuilder;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Locale;

/**
 * Supported types of database field.
 */
public enum EFieldType {

    /** Java Integer (java.sql.Types TINYINT, SMALLINT, INTEGER). */
    INTEGER(Integer.class),

    /** Java Long (java.sql.Types BIGINT). */
    LONG(Long.class),

    /** Java Float (java.sql.Types FLOAT, REAL). */
    FLOAT(Float.class),

    /** Java Double (java.sql.Types DOUBLE). */
    DOUBLE(Double.class),

    /** Java BigDecimal (java.sql.Types NUMERIC, DECIMAL). */
    DECIMAL(BigDecimal.class),

    /** Java String (java.sql.Types CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR, CLOB). */
    STRING(String.class),

    /** Java byte array (java.sql.Types BINARY, VARBINARY, LONGVARBINARY, BLOB). */
    BINARY(Blob.class),

    /** Java Boolean (java.sql.Types BOOLEAN). */
    BOOLEAN(Boolean.class),

    /** Java LocalDate (java.sql.Types DATE). */
    LOCAL_DATE(Date.class),

    /** Java LocalTime (java.sql.Types TIME). */
    LOCAL_TIME(Time.class),

    /** Java LocalDateTime (java.sql.Types TIMESTAMP). */
    LOCAL_DATE_TIME(Timestamp.class);

    /** The type class. */
    public final Class<?> cls;

    /**
     * Constructs a new {@code EFieldType}.
     *
     * @param theCls the value class
     */
    EFieldType(final Class<?> theCls) {

        this.cls = theCls;
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String clsName = this.cls.getName();

        return SimpleBuilder.concat("EFieldType{cls=", clsName, "}");
    }

    /**
     * Returns the field type with a specified name.
     * @param name the name (case-insensitive)
     * @return the field type
     */
    public static EFieldType forName(final String name) {

        EFieldType result = null;

        try {
            final String upper = name.toUpperCase(Locale.ROOT);
            result = EFieldType.valueOf(upper);
        } catch (final IllegalArgumentException ex) {
            Log.warning("Attempt to parse '", name, "' as field type.");
        }

        return result;
    }
}
