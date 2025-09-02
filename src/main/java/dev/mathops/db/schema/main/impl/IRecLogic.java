package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.cfg.EDbProduct;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Facet;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * An interface implemented by record logic implementations.
 *
 * @param <T> the record type
 */
public interface IRecLogic<T extends RecBase> {

    /** A string used to build queries. */
    String WHERE = " WHERE ";

    /** A string used to build queries. */
    String AND = " AND ";

    /**
     * Gets the database installation type for a cache.
     *
     * @param cache  the cache
     * @param schema the schema
     * @return the database installation type
     */
    static EDbProduct getDbType(final Cache cache, final ESchema schema) {

        final Profile profile = cache.getProfile();
        final Facet facet = profile.getFacet(schema);

        return facet.login.database.server.type;
    }

    /**
     * Returns the string needed to include an Integer in an SQL statement.
     *
     * @param i the integer
     * @return the SQL string, in the form "null" or "123".
     */
    default String sqlIntegerValue(final int i) {

        return Integer.toString(i);
    }

    /**
     * Returns the string needed to include an Integer in an SQL statement.
     *
     * @param i the integer
     * @return the SQL string, in the form "null" or "123".
     */
    default String sqlIntegerValue(final Integer i) {

        final String result;

        if (i == null) {
            result = "null";
        } else {
            result = i.toString();
        }

        return result;
    }

    /**
     * Returns the string needed to include a Long in an SQL statement.
     *
     * @param l the long
     * @return the SQL string, in the form "null" or "123".
     */
    default String sqlLongValue(final Long l) {

        final String result;

        if (l == null) {
            result = "null";
        } else {
            result = l.toString();
        }

        return result;
    }

//    /**
//     * Returns the string needed to include a Float in an SQL statement.
//     *
//     * @param f the float
//     * @return the SQL string, in the form "null" or "123.456".
//     */
//    default String sqlFloatValue(final Float f) {
//
//        final String result;
//
//        if (f == null) {
//            result = "null";
//        } else {
//            result = f.toString();
//        }
//
//        return result;
//    }

    /**
     * Returns the string needed to include a string in an SQL statement.
     *
     * @param str the string
     * @return the SQL string, in the form "null" or "'string'".
     */
    default String sqlStringValue(final String str) {

        final String result;

        if (str == null) {
            result = "null";
        } else {
            result = "'" + str.replace("'", "''") + "'";
        }

        return result;
    }

    /**
     * Returns the string needed to include a date in an SQL statement.
     *
     * @param dt the date
     * @return the SQL string, in the form "DATE('12/31/2021')".
     */
    default String sqlDateValue(final LocalDate dt) {

        final String result;

        if (dt == null) {
            result = "null";
        } else {
            final int yy = dt.getYear();
            final int mm = dt.getMonthValue();
            final int dd = dt.getDayOfMonth();

            result = "DATE('" + mm + "/" + dd + "/" + yy + "')";
        }

        return result;
    }

    /**
     * Returns the string needed to include a date in an SQL statement for PostgreSQL.
     *
     * @param dt the date
     * @return the SQL string, in the form '2021-12-31'.
     */
    default String sqlPgDateValue(final LocalDate dt) {

        final String result;

        if (dt == null) {
            result = "null";
        } else {
            final int yy = dt.getYear();
            final int mm = dt.getMonthValue();
            final int dd = dt.getDayOfMonth();

            result = "'" + yy + "-" + padTo2(mm) + "-" + padTo2(dd) + "'";
        }

        return result;
    }

    /**
     * Returns the string representation of an integer padded with a leading zero if needed to make it 2 characters.
     *
     * @param value the integer value
     * @return the 2-character representation, like "01" or "95".
     */
    private static String padTo2(final int value) {

        return value > 9 ? Integer.toString(value) : ("0" + value);
    }

    /**
     * Returns the string needed to include a time in an SQL statement.
     *
     * @param tm the time
     * @return the SQL string, in the form "'23:59:58'".
     */
    default String sqlTimeValue(final LocalTime tm) {

        final String result;

        if (tm == null) {
            result = "null";
        } else {
            final int hh = tm.getHour();
            final int mm = tm.getMinute();
            final int ss = tm.getSecond();

            result = "'" + padTo2(hh) + ":" + padTo2(mm) + ":" + padTo2(ss) + "'";
        }

        return result;
    }

    /**
     * Returns the string needed to include a date/time in an SQL statement.
     *
     * @param dtm the date/time
     * @return the SQL string, in the form "'2021-12-31 12:34:56'".
     */
    default String sqlDateTimeValue(final LocalDateTime dtm) {

        final String result;

        if (dtm == null) {
            result = "null";
        } else {
            final int y1 = dtm.getYear();
            final int m1 = dtm.getMonthValue();
            final int d1 = dtm.getDayOfMonth();
            final int hh1 = dtm.getHour();
            final int mm1 = dtm.getMinute();
            final int ss1 = dtm.getSecond();

            final HtmlBuilder sql = new HtmlBuilder(100);

            sql.add("'").add(y1).add('-').add(m1).add('-').add(d1).add(' ').add(hh1).add(':').add(mm1).add(':')
                    .add(ss1).add("'");

            result = sql.toString();
        }

        return result;
    }

    /**
     * Retrieves a String field value from a result set, returning null if the result set indicates a null value was
     * present. The string is trimmed to remove leading or trailing whitespace.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default String getStringField(final ResultSet rs, final String name) throws SQLException {

        final String tmp = rs.getString(name);

        return tmp == null ? null : tmp.trim();
    }

    /**
     * Retrieves a Long field value from a result set, returning null if the result set indicates a null value was
     * present.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default Long getLongField(final ResultSet rs, final String name) throws SQLException {

        final long tmp = rs.getLong(name);

        return rs.wasNull() ? null : Long.valueOf(tmp);
    }

    /**
     * Retrieves an Integer field value from a result set, returning null if the result set indicates a null value was
     * present.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default Integer getIntegerField(final ResultSet rs, final String name) throws SQLException {

        final int tmp = rs.getInt(name);

        return rs.wasNull() ? null : Integer.valueOf(tmp);
    }

    /**
     * Retrieves a LocalDate field value from a result set, returning null if the result set indicates a null value was
     * present.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default LocalDate getDateField(final ResultSet rs, final String name) throws SQLException {

        final Date tmp = rs.getDate(name);

        return tmp == null ? null : tmp.toLocalDate();
    }

    /**
     * Retrieves a LocalDate field value from a result set, returning null if the result set indicates a null value was
     * present.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default LocalTime getTimeField(final ResultSet rs, final String name) throws SQLException {

        final Time tmp = rs.getTime(name);

        return tmp == null ? null : tmp.toLocalTime();
    }

    /**
     * Retrieves a LocalDateTime field value from a result set, returning null if the result set indicates a null value
     * was present.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    default LocalDateTime getDateTimeField(final ResultSet rs, final String name) throws SQLException {

        final Timestamp tmp = rs.getTimestamp(name);

        return tmp == null ? null : tmp.toLocalDateTime();
    }

    /**
     * Extracts a record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    T fromResultSet(ResultSet rs) throws SQLException;

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    boolean insert(Cache cache, T record) throws SQLException;

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    boolean delete(Cache cache, T record) throws SQLException;

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    List<T> queryAll(final Cache cache) throws SQLException;
}
