package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawLogins;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with logins records.
 *
 * <pre>
 * Table:  'logins'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * user_id              char(9)                   no
 * user_type            char(3)                   no
 * user_name            char(20)                  no      PK
 * salt                 char(32)                  no
 * stored_key           char(64)                  no
 * server_key           char(64)                  no
 * dtime_created        datetime year to second   no
 * dtime_expires        datetime year to second   yes
 * dtime_last_login     datetime year to second   yes
 * force_pw_change      char(1)                   no
 * email                char(40)                  yes
 * nbr_invalid_atmpts   smallint                  yes
 * </pre>
 */
public enum RawLoginsLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "logins" : (schemaPrefix + ".logins");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawLogins record) throws SQLException {

        if (record.userId == null || record.userType == null || record.userName == null
            || record.dtimeCreated == null || record.forcePwChange == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (user_id,user_type,user_name,stored_key,",
                "server_key,dtime_created,dtime_expires,dtime_last_login,force_pw_change,email,salt,",
                "nbr_invalid_atmpts) VALUES (",
                conn.sqlStringValue(record.userId), ",",
                conn.sqlStringValue(record.userType), ",",
                conn.sqlStringValue(record.userName), ",",
                conn.sqlStringValue(record.storedKey), ",",
                conn.sqlStringValue(record.serverKey), ",",
                conn.sqlDateTimeValue(record.dtimeCreated), ",",
                conn.sqlDateTimeValue(record.dtimeExpires), ",",
                conn.sqlDateTimeValue(record.dtimeLastLogin), ",",
                conn.sqlStringValue(record.forcePwChange), ",",
                conn.sqlStringValue(record.email), ",",
                conn.sqlStringValue(record.salt), ",",
                conn.sqlIntegerValue(record.nbrInvalidAtmpts), ")");

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean delete(final Cache cache, final RawLogins record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE user_name=", conn.sqlStringValue(record.userName));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawLogins> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawLogins> result = new ArrayList<>(50);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawLogins.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for the login having a given username.
     *
     * @param cache    the data cache
     * @param username the username for which to query
     * @return the login model; {@code null} if not found or an error occurs
     * @throws SQLException if there is an error performing the query
     */
    public static RawLogins query(final Cache cache, final String username) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return doSingleQuery(conn, SimpleBuilder.concat(
                    "SELECT * FROM ", tableName, " WHERE user_name=", conn.sqlStringValue(username)));
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the last successful login time on the login record to the current date/time.
     *
     * @param cache  the data cache
     * @param record the login object whose last login time is to be updated
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error performing the query
     */
    public static boolean updateLastLoginTime(final Cache cache, final RawLogins record) throws SQLException {

        final String tableName = getTableName(cache);

        final LocalDateTime now = LocalDateTime.now();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName, " SET dtime_last_login=", conn.sqlDateTimeValue(now),
                " WHERE user_name=", conn.sqlStringValue(record.userName));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) > 0;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the number of password fails and "disabled" state/reason for a local login record.
     *
     * @param cache    the data cache
     * @param username the username of the login object to update
     * @param fails    the new number of password fails
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error performing the query
     */
    public static boolean updatePasswordFails(final Cache cache, final String username,
                                              final Integer fails) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName, " SET nbr_invalid_atmpts=", conn.sqlIntegerValue(fails),
                " WHERE user_name=", conn.sqlStringValue(username));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) > 0;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Performs a query that returns single record.
     *
     * @param conn the database connection
     * @param sql  the query SQL
     * @return the record; null if none returned
     * @throws SQLException if there is an error performing the query
     */
    private static RawLogins doSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawLogins result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawLogins.fromResultSet(rs);
            }
        }

        return result;
    }
}
