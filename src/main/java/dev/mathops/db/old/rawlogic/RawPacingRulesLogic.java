package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawPacingRules;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "pacing_rules" records.
 *
 * <pre>
 * Table:  'pacing_rules'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * pacing_rules         char(1)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * activity_type        char(2)                   no      PK
 * requirement          char(4)                   no      PK
 * </pre>
 */
public enum RawPacingRulesLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "pacing_rules" : (schemaPrefix + ".pacing_rules");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawPacingRules record) throws SQLException {

        if (record.termKey == null || record.pacingStructure == null || record.activityType == null
            || record.requirement == null) {

            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (term,term_yr,pacing_structure,activity_type,requirement) VALUES (",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                conn.sqlStringValue(record.pacingStructure), ",",
                conn.sqlStringValue(record.activityType), ",",
                conn.sqlStringValue(record.requirement), ")");

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
    public static boolean delete(final Cache cache, final RawPacingRules record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND pacing_structure=", conn.sqlStringValue(record.pacingStructure),
                "  AND activity_type=", conn.sqlStringValue(record.activityType),
                "  AND requirement=", conn.sqlStringValue(record.requirement));

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
    public static List<RawPacingRules> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all pacing structures for a particular term.
     *
     * @param cache   the data cache
     * @param termKey the key of the term for which to query
     * @return the corresponding list of records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawPacingRules> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE term=",
                conn.sqlStringValue(termKey.termCode), " AND term_yr=", termKey.shortYear);

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all pacing structures for a particular term.
     *
     * @param cache           the data cache
     * @param termKey         the key of the term for which to query
     * @param pacingStructure the pacing structure for which to query
     * @return the corresponding list of records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawPacingRules> queryByTermAndPacingStructure(final Cache cache, final TermKey termKey,
                                                                     final String pacingStructure) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE term=",
                conn.sqlStringValue(termKey.termCode), " AND term_yr=", termKey.shortYear,
                " AND pacing_structure=", conn.sqlStringValue(pacingStructure));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Tests whether there is a requirement attached to an activity under a specified pacing structure in a term.
     *
     * @param cache           the data cache
     * @param termKey         the term
     * @param pacingStructure the pacing structure
     * @param activityType    the activity for which to test the requirement
     * @param requirement     the requirement to test
     * @return true if the requirement is necessary according to pacing rules; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean isRequired(final Cache cache, final TermKey termKey, final String pacingStructure,
                                     final String activityType, final String requirement) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", termKey.shortYear,
                "   AND pacing_structure=", conn.sqlStringValue(pacingStructure),
                "   AND activity_type=", conn.sqlStringValue(activityType),
                "   AND requirement=", conn.sqlStringValue(requirement));

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next();
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
    private static List<RawPacingRules> executeListQuery(final DbConnection conn, final String sql)
            throws SQLException {

        final List<RawPacingRules> result = new ArrayList<>(20);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawPacingRules.fromResultSet(rs));
            }
        }

        return result;
    }
}
