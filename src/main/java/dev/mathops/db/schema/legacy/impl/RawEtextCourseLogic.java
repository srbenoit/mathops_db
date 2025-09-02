package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawEtextCourse;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with etext_course records.
 *
 * <pre>
 * Table:  'etext_course'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * etext_id             char(6)                   no      PK
 * course               char(6)                   no      PK
 * </pre>
 */
public enum RawEtextCourseLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "etext_course" : (schemaPrefix + ".etext_course");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawEtextCourse record) throws SQLException {

        if (record.etextId == null || record.course == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (etext_id,course) VALUES (",
                conn.sqlStringValue(record.etextId), ",",
                conn.sqlStringValue(record.course), ")");

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
    public static boolean delete(final Cache cache, final RawEtextCourse record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE etext_id=", conn.sqlStringValue(record.etextId),
                "  AND course=", conn.sqlStringValue(record.course));

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
    public static List<RawEtextCourse> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for e-text courses provided by a particular e-text.
     *
     * @param cache   the data cache
     * @param etextId the ID of the e-text course to query
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawEtextCourse> queryByEtext(final Cache cache, final String etextId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, SimpleBuilder.concat(
                    "SELECT * FROM ", tableName, " WHERE etext_id=", conn.sqlStringValue(etextId)));
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all e-text courses that provide access to a particular course.
     *
     * @param cache    the data cache
     * @param courseId the ID of the e-text course to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawEtextCourse> queryByCourse(final Cache cache, final String courseId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, SimpleBuilder.concat(
                    "SELECT * FROM ", tableName, " WHERE course=", conn.sqlStringValue(courseId)));
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawEtextCourse> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawEtextCourse> result = new ArrayList<>(10);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawEtextCourse.fromResultSet(rs));
            }
        }

        return result;
    }
}
