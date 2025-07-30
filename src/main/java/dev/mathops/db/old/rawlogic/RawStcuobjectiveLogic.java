package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawStcuobjective;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stcuobjective" records.
 *
 * <pre>
 * Table:  'stcuobjective'
 *
 * Column name              Type                      Nulls   Key
 * -----------------------  ------------------------  ------  -----
 * stu_id                   char(9)                   no      PK
 * course                   char(6)                   no      PK
 * unit                     smallint                  no      PK
 * objective                smallint                  no      PK
 * lecture_viewed_dt        date                      yes
 * seed                     integer                   yes
 * last_component_finished  smallint                  yes
 * </pre>
 */
public enum RawStcuobjectiveLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stcuobjective" : (schemaPrefix + ".stcuobjective");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStcuobjective record) throws SQLException {

        if (record.stuId == null || record.course == null || record.unit == null || record.objective == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of stcuobjective for test student:");
            Log.info("  Student ID: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,course,unit,objective,",
                    "lecture_viewed_dt,seed,last_component_finished) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlIntegerValue(record.unit), ",",
                    conn.sqlIntegerValue(record.objective), ",",
                    conn.sqlDateValue(record.lectureViewedDt), ",",
                    conn.sqlIntegerValue(record.seed), ",",
                    conn.sqlIntegerValue(record.lastComponentFinished), ")");

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
    public static boolean delete(final Cache cache, final RawStcuobjective record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND unit=", conn.sqlIntegerValue(record.unit),
                "  AND objective=", conn.sqlIntegerValue(record.objective));

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
    public static List<RawStcuobjective> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        return doListQuery(cache, sql);
    }

    /**
     * Gets all records for a student.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStcuobjective> queryByStudent(final Cache cache, final String studentId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                conn.sqlStringValue(studentId));

        return doListQuery(cache, sql);
    }

    /**
     * Gets the student status in a particular lesson within a given course, unit, and objective.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @param courseId  the course for which to query
     * @param unit      the unit for which to query
     * @param objective the objective for which to query
     * @return the student's lesson status; {@code null} if none exists
     * @throws SQLException if there is an error accessing the database
     */
    public static RawStcuobjective query(final Cache cache, final String studentId, final String courseId,
                                         final Integer unit, final Integer objective) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(studentId),
                " AND course=", conn.sqlStringValue(courseId),
                " AND unit=", conn.sqlIntegerValue(unit),
                " AND objective=", conn.sqlIntegerValue(objective));

        try {
            return doSingleQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Tests whether the instructional lecture for a unit and objective has been viewed by a student.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @param unit      the unit number
     * @param objective the objective number
     * @return {@code true} of the lecture has been viewed; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean hasLectureBeenViewed(final Cache cache, final String studentId, final String courseId,
                                               final Integer unit, final Integer objective) throws SQLException {

        final RawStcuobjective row = query(cache, studentId, courseId, unit, objective);

        return row != null && row.lectureViewedDt != null;
    }

    /**
     * Records the fact that the instructional lecture has been viewed by a student.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @param unit      the unit number
     * @param objective the objective number
     * @param now       the date/time the lecture was viewed
     * @return true if successful; false otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean recordLectureView(final Cache cache, final String studentId, final String courseId,
                                            final Integer unit, final Integer objective, final ZonedDateTime now)
            throws SQLException {

        final boolean result;

        if (studentId.startsWith("99")) {
            Log.info("Skipping update of StudentLesson for test student:");
            Log.info("  Student ID: ", studentId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final RawStcuobjective existing = query(cache, studentId, courseId, unit, objective);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql;
            if (existing == null) {
                sql = SimpleBuilder.concat(
                        "INSERT INTO ", tableName, " (stu_id,course,unit,objective,",
                        "lecture_viewed_dt) VALUES (",
                        conn.sqlStringValue(studentId), ",",
                        conn.sqlStringValue(courseId), ",",
                        conn.sqlIntegerValue(unit), ",",
                        conn.sqlIntegerValue(objective), ",",
                        conn.sqlDateValue(now.toLocalDate()), ")");
            } else {
                sql = SimpleBuilder.concat("UPDATE ", tableName,
                        " SET lecture_viewed_dt=", conn.sqlDateValue(now.toLocalDate()),
                        " WHERE stu_id=", conn.sqlStringValue(studentId),
                        " AND course=", conn.sqlStringValue(courseId),
                        " AND unit=", conn.sqlIntegerValue(unit),
                        " AND objective=", conn.sqlIntegerValue(objective));
            }

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
     * Executes a query that returns a single records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static RawStcuobjective doSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawStcuobjective result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawStcuobjective.fromResultSet(rs);
            }
        }

        return result;
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param cache the cache
     * @param sql   the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStcuobjective> doListQuery(final Cache cache, final String sql) throws SQLException {

        final List<RawStcuobjective> result = new ArrayList<>(50);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStcuobjective.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
