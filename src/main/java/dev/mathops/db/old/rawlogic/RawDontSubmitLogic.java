package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawDontSubmit;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with dont_submit records.
 *
 * <pre>
 * Table:  'dont_submit'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * </pre>
 */
public enum RawDontSubmitLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "dont_submit" : (schemaPrefix + ".dont_submit");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawDontSubmit record) throws SQLException {

        if (record.course == null || record.sect == null || record.termKey == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (course,sect,term,term_yr) VALUES (",
                "'", record.course, "',",
                "'", record.sect, "',",
                "'", record.termKey.termCode, "',",
                record.termKey.shortYear, ")");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

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
    public static boolean delete(final Cache cache, final RawDontSubmit record) throws SQLException {

        final boolean result;

        final HtmlBuilder sql = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("DELETE FROM ", tableName,
                " WHERE course=", conn.sqlStringValue(record.course),
                " AND sect=", conn.sqlStringValue(record.sect),
                " AND term=", conn.sqlStringValue(record.termKey.termCode),
                " AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear));

        try (final Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql.toString()) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawDontSubmit> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records for a specific term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawDontSubmit> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                " AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawDontSubmit> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawDontSubmit> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawDontSubmit.fromResultSet(rs));
            }
        }

        return result;
    }
}
