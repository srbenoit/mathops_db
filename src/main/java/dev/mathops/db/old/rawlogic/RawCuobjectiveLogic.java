package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawCuobjective;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with cuobjective records.
 *
 * <pre>
 * Table:  'cuobjective'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * course               char(6)                   no      PK
 * unit                 smallint                  no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * objective            smallint                  no      PK
 * lesson_id            char(40)                  yes
 * lesson_nbr           char(10)                  yes
 * start_dt             date                      yes
 * </pre>
 */
public enum RawCuobjectiveLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "cuobjective" : (schemaPrefix + ".cuobjective");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawCuobjective record) throws SQLException {

        if (record.course == null || record.unit == null || record.objective == null || record.termKey == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (course,unit,term,term_yr,objective,lesson_id,lesson_nbr,start_dt) VALUES (",
                conn.sqlStringValue(record.course), ",",
                record.unit, ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                record.objective, ",",
                conn.sqlStringValue(record.lessonId), ",",
                conn.sqlStringValue(record.lessonNbr), ",",
                conn.sqlDateValue(record.startDt), ")");

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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
    public static boolean delete(final Cache cache, final RawCuobjective record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE course=", conn.sqlStringValue(record.course),
                "  AND unit=", conn.sqlIntegerValue(record.unit),
                "  AND term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND objective=", conn.sqlIntegerValue(record.objective));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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
    public static List<RawCuobjective> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all course unit objectives in a given term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @return the list of course units; {@code null} on any error
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawCuobjective> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName,
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
    private static List<RawCuobjective> executeListQuery(final DbConnection conn, final String sql)
            throws SQLException {

        final List<RawCuobjective> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawCuobjective.fromResultSet(rs));
            }
        }

        return result;
    }
}
