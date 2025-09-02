package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.LogicUtils;
import dev.mathops.db.schema.legacy.rec.RawStmsg;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stmsg" records.
 *
 * <pre>
 * Table:  'stmsg'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * msg_dt               date                      no      PK
 * pace                 smallint                  yes
 * course_index         smallint                  yes
 * touch_point          char(3)                   no      PK
 * msg_code             char(8)                   no      PK
 * sender               char(50)                  yes
 * </pre>
 */
public enum RawStmsgLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stmsg" : (schemaPrefix + ".stmsg");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStmsg record) throws SQLException {

        if (record.stuId == null || record.msgDt == null || record.touchPoint == null || record.sender == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,msg_dt,pace,course_index,touch_point,msg_code,sender) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlDateValue(record.msgDt), ",",
                conn.sqlIntegerValue(record.pace), ",",
                conn.sqlIntegerValue(record.courseIndex), ",",
                conn.sqlStringValue(record.touchPoint), ",",
                conn.sqlStringValue(record.msgCode), ",",
                conn.sqlStringValue(record.sender), ")");

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
    public static boolean delete(final Cache cache, final RawStmsg record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND msg_dt=", conn.sqlDateValue(record.msgDt),
                "  AND touch_point=", conn.sqlStringValue(record.touchPoint),
                "  AND msg_code=", conn.sqlStringValue(record.msgCode));

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

        return result;
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStmsg> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all purchased e-texts for a student, including all inactive (expired or refunded) e-texts. Results are
     * ordered by e-text ID, and includes any refunded or expired records.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStmsg> queryByStudent(final Cache cache, final String studentId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                conn.sqlStringValue(studentId));

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets the total number of STMSG records in the database.
     *
     * @param cache the data cache
     * @return the number of records, or {@code null} if the query returned no value
     * @throws SQLException if there is an error accessing the database
     */
    public static Integer count(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT count(*) FROM " + tableName;

        return LogicUtils.executeSimpleIntQuery(cache, sql);
    }

    /**
     * Gets the date when the latest message was sent.
     *
     * @param cache the data cache
     * @return the latest message record date, or {@code null} if the query returned no value
     * @throws SQLException if there is an error accessing the database
     */
    public static LocalDate getLatest(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT max(msg_dt) FROM " + tableName;

        return LogicUtils.executeSimpleDateQuery(cache, sql);
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStmsg> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStmsg> result = new ArrayList<>(50);

        try (final Statement statement = conn.createStatement();
             final ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStmsg.fromResultSet(rs));
            }
        }

        return result;
    }
}
