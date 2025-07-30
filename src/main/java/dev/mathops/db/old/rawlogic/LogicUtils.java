package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.TemporalUtils;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The base class for logic modules.
 */
public enum LogicUtils {
    ;

    /** Flag to disable live queries. */
    private static LocalDateTime bannerDownUntil = null;

    /**
     * Tests whether Banner is down currently.
     *
     * @return true if down; false if not
     */
    public static boolean isBannerDown() {

        boolean result = false;

        if (bannerDownUntil != null) {
            final LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(bannerDownUntil)) {
                result = true;
            } else {
                bannerDownUntil = null;
            }
        }

        return result;
    }

    /**
     * Records the fact that a Banner operation failed, which marks Banner as being "Down" for 15 minutes.
     */
    public static void indicateBannerDown() {

        bannerDownUntil = LocalDateTime.now().plusMinutes(15L);

        Log.warning("Banner will be considered DOWN until ", TemporalUtils.FMT_MDY_AT_HMS_A.format(bannerDownUntil));
    }

    /**
     * Records the fact that a Banner operation failed, which marks Banner as being "Down" for a year ("indefinitely").
     */
    public static void indicateBannerDownIndefinitely() {

        bannerDownUntil = LocalDateTime.now().plusYears(1L);

        Log.warning("Banner will be considered DOWN until ", TemporalUtils.FMT_MDY.format(bannerDownUntil));
    }

    /**
     * Records the fact that a Banner operation failed, which marks Banner as being "Down" for 15 minutes.
     */
    public static void indicateBannerUp() {

        bannerDownUntil = null;

        Log.warning("Banner will now be considered UP");
    }


    /**
     * Sets a string parameter in a prepared statement based on an {@code Object} which may be {@code null}. The
     * {@code toString} method on the object is used to generate the string value.
     *
     * @param ps    the prepared statement
     * @param index the index of the parameter to set
     * @param value the value
     * @throws SQLException if there is an error setting the value
     */
    static void setPsString(final PreparedStatement ps, final int index, final Object value) throws SQLException {

        if (value == null) {
            ps.setString(index, null);
        } else {
            final String s = value.toString().replace('\u2019', '\'');

            ps.setString(index, s);
        }
    }

    /**
     * Sets an integer parameter in a prepared statement based on an {@code Integer} which may be {@code null}.
     *
     * @param ps    the prepared statement
     * @param index the index of the parameter to set
     * @param value the value
     * @throws SQLException if there is an error setting the value
     */
    static void setPsInteger(final PreparedStatement ps, final int index, final Integer value) throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value.intValue());
        }
    }

    /**
     * Sets a date parameter in a prepared statement based on a {@code LocalDate} which may be {@code null}.
     *
     * @param ps    the prepared statement
     * @param index the index of the parameter to set
     * @param value the value
     * @throws SQLException if there is an error setting the value
     */
    static void setPsDate(final PreparedStatement ps, final int index, final LocalDate value) throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    /**
     * Sets a date/time parameter in a prepared statement based on a {@code LocalDateTime} which may be {@code null}.
     *
     * @param ps    the prepared statement
     * @param index the index of the parameter to set
     * @param value the value
     * @throws SQLException if there is an error setting the value
     */
    static void setPsTimestamp(final PreparedStatement ps, final int index, final LocalDateTime value)
            throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    /**
     * Executes an SQL query that should return a single integer (such as a COUNT or MAX function).
     *
     * @param cache the data cache
     * @param sql   the SQL to execute
     * @return the result of the query, or {@code null} if the query returned no record
     * @throws SQLException if there is an error executing the query
     */
    static Integer executeSimpleIntQuery(final Cache cache, final String sql) throws SQLException {

        Integer result = null;

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rset = stmt.executeQuery(sql)) {

            if (rset.next()) {
                final int value = rset.getInt(1);
                if (!rset.wasNull()) {
                    result = Integer.valueOf(value);
                }
            }
        } catch (final SQLException ex) {
            Log.warning("Query failed: [", sql, "]", ex);
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Executes an SQL query that should return a single LocalDate.
     *
     * @param cache the data cache
     * @param sql   the SQL to execute
     * @return the result of the query, or {@code null} if the query returned no record
     * @throws SQLException if there is an error executing the query
     */
    static LocalDate executeSimpleDateQuery(final Cache cache, final String sql) throws SQLException {

        LocalDate result = null;

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rset = stmt.executeQuery(sql)) {

            if (rset.next()) {
                final Date dt = rset.getDate(1);
                result = dt == null ? null : dt.toLocalDate();
            }
        } catch (final SQLException ex) {
            Log.warning("Query failed: [", sql, "]", ex);
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Validates a test student ID that begins with "99PL". There are only certain combinations of remaining digits that
     * constitute a valid ID.
     *
     * @param studentId the student ID
     * @return {@code true} if the ID is valid; {@code false} if not
     */
    static boolean validate99PLStudentId(final CharSequence studentId) {

        final char c5 = studentId.charAt(4);
        final char c6 = studentId.charAt(5);
        final char c7 = studentId.charAt(6);
        final char c9 = studentId.charAt(8);

        boolean valid = false;

        if (c5 == '0') {
            valid = c6 == '0' && c7 == '0' && c9 == '0';
        } else if (c5 == '3') {
            valid = c6 >= '0' && c6 <= '8' && c7 == '0';
        } else if (c5 >= '1' && c5 <= '5') {
            if (c6 == '0' || c6 == '1') {
                valid = c7 == '0';
            } else if (c6 == '2') {
                valid = c7 == '0' || c7 == '2';
            } else if (c6 == '3') {
                valid = c7 == '0' || c7 == '2' || c7 == '3';
            } else if (c6 == '4') {
                valid = c7 == '0' || c7 == '2' || c7 == '3' || c7 == '4';
            } else if (c6 == '5') {
                valid = c7 == '0' || c7 == '2' || c7 == '3' || c7 == '5';
            } else if (c6 == '6') {
                valid = c7 == '0' || c7 == '2' || c7 == '3' || c7 == '4' || c7 == '5' || c7 == '6';
            } else if (c6 == '7') {
                valid = c7 == '0' || c7 == '2' || c7 == '3' || c7 == '5' || c7 == '7';
            } else if (c6 == '8') {
                valid = c7 == '0' || c7 == '2' || c7 == '3' || c7 == '4' || c7 == '5' || c7 == '6' || c7 == '7'
                        || c7 == '8';
            }
        }

        if (valid) {
            final char c8 = studentId.charAt(7);
            valid = c8 >= '0' && c8 <= '9' || c8 >= 'A' && c8 <= 'R';

            if (c6 == '0') {
                if (c9 != '0' && c9 != '1') {
                    valid = false;
                }
            } else if (c6 == '1') {
                if (c9 != '0' && c9 != '2') {
                    valid = false;
                }
            } else if (c6 == '2') {
                if (c9 != '0' && c9 != '3') {
                    valid = false;
                }
            } else if (c6 == '3' || c6 == '5' || c6 == '7') {
                if (c9 != '0' && c9 != '4') {
                    valid = false;
                }
            } else if (c6 == '4') {
                if (c9 != '0' && c9 != '5') {
                    valid = false;
                }
            } else if (c6 == '6') {
                if (c9 != '0' && c9 != '6') {
                    valid = false;
                }
            } else if (c9 != '0') {
                valid = false;
            }
        }

        return valid;
    }
}
