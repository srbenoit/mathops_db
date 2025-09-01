package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawStlessonAssign;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stlesson_assign" records.
 *
 * <pre>
 * Table:  'stlesson_assign'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * lesson_id            char(12)                  no      PK
 * when_shown           datetime year to second   yes
 * when_opened          datetime year to second   yes
 * when_closed          datetime year to second   yes
 * when_hidden          datetime year to second   yes
 * when_started         datetime year to second   yes
 * when_finished        datetime year to second   yes
 * score_tenths         integer                   yes
 * </pre>
 */
public enum RawStlessonAssignLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    private static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stlesson_assign" : (schemaPrefix + ".stlesson_assign");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStlessonAssign record) throws SQLException {

        if (record.stuId == null || record.lessonId == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawStlessonAssign for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,course,lesson_id,when_shown,",
                    "when_open,when_closed,when_hidden,when_started,when_finished,score_tenths) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlStringValue(record.lessonId), ",",
                    conn.sqlDateTimeValue(record.whenShown), ",",
                    conn.sqlDateTimeValue(record.whenOpen), ",",
                    conn.sqlDateTimeValue(record.whenClosed), ",",
                    conn.sqlDateTimeValue(record.whenHidden), ",",
                    conn.sqlDateTimeValue(record.whenStarted), ",",
                    conn.sqlDateTimeValue(record.whenFinished), ",",
                    conn.sqlIntegerValue(record.scoreTenths), ")");

            try (final Statement stmt = conn.createStatement()) {
                result = stmt.executeUpdate(sql) == 1;

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
        }

        return result;
    }

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean delete(final Cache cache, final RawStlessonAssign record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND lesson_id=", conn.sqlStringValue(record.lessonId));

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
    public static List<RawStlessonAssign> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all student term configurations.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of matching student term records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStlessonAssign> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets a single record.
     *
     * @param cache    the data cache
     * @param stuId    the student ID
     * @param course   the course
     * @param lessonId the lesson ID
     * @return the matching record; null if none found
     * @throws SQLException if there is an error accessing the database
     */
    public static RawStlessonAssign query(final Cache cache, final String stuId, final String course,
                                          final String lessonId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                " AND course=", conn.sqlStringValue(course),
                " AND lessod_id=", conn.sqlStringValue(lessonId));

        try {
            return executeSingleQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a single record.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static RawStlessonAssign executeSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawStlessonAssign result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawStlessonAssign.fromResultSet(rs);
            }
        }

        return result;
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStlessonAssign> executeListQuery(final DbConnection conn, final String sql)
            throws SQLException {

        final List<RawStlessonAssign> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStlessonAssign.fromResultSet(rs));
            }
        }

        return result;
    }
}
