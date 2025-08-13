package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawSthomework;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A utility class to work with "sthomework" records.
 *
 * <pre>
 * Table:  'sthomework'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * serial_nbr           integer                   no      PK
 * version              char(5)                   no
 * stu_id               char(9)                   no
 * hw_dt                date                      no
 * hw_score             smallint                  no
 * start_time           integer                   no
 * finish_time          integer                   no
 * time_ok              char(1)                   no
 * passed               char(1)                   no
 * hw_type              char(2)                   no
 * course               char(6)                   no
 * sect                 char(4)                   no
 * unit                 smallint                  no
 * objective            char(6)                   no
 * hw_coupon            char(1)                   no
 * used_dt              date                      yes
 * used_serial_nbr      integer                   yes
 * </pre>
 */
public enum RawSthomeworkLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "sthomework" : (schemaPrefix + ".sthomework");
    }

    /** The types of homework. */
    public static final String[] ALL_HW_TYPES = {"HW"};

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawSthomework record) throws SQLException {

        if (record.serialNbr == null || record.version == null || record.stuId == null
            || record.hwDt == null || record.hwScore == null || record.startTime == null
            || record.finishTime == null || record.timeOk == null || record.passed == null
            || record.hwType == null || record.course == null || record.sect == null
            || record.unit == null || record.objective == null || record.hwCoupon == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawSthomework for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            // Adjust serial number if needed to avoid collision with existing record
//            Long ser = record.serialNbr;
//            for (int i = 0; i < 1000; ++i) {
//                final Integer existing = executeSimpleIntQuery(cache.conn,
//                        "SELECT COUNT(*) FROM sthomework WHERE serial_nbr=" + ser);
//
//                if (existing == null || existing.longValue() == 0L) {
//                    break;
//                }
//                ser = Long.valueOf(ser.longValue() + 1L);
//            }

            final String obj = record.objective.toString();

            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "INSERT INTO ", tableName,
                    " (serial_nbr,version,stu_id,hw_dt,hw_score,start_time,finish_time,time_ok,",
                    "passed,hw_type,course,sect,unit,objective,hw_coupon,used_dt,used_serial_nbr) VALUES (",
                    conn.sqlLongValue(record.serialNbr), ",",
                    conn.sqlStringValue(record.version), ",",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlDateValue(record.hwDt), ",",
                    conn.sqlIntegerValue(record.hwScore), ",",
                    conn.sqlIntegerValue(record.startTime), ",",
                    conn.sqlIntegerValue(record.finishTime), ",",
                    conn.sqlStringValue(record.timeOk), ",",
                    conn.sqlStringValue(record.passed), ",",
                    conn.sqlStringValue(record.hwType), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlStringValue(record.sect), ",",
                    conn.sqlIntegerValue(record.unit), ",",
                    conn.sqlStringValue(obj), ",",
                    conn.sqlStringValue(record.hwCoupon), ",",
                    conn.sqlDateValue(record.usedDt), ",",
                    conn.sqlLongValue(record.usedSerialNbr), ")");

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
    public static boolean delete(final Cache cache, final RawSthomework record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE serial_nbr=", conn.sqlLongValue(record.serialNbr),
                " AND version=", conn.sqlStringValue(record.version),
                " AND stu_id=", conn.sqlStringValue(record.stuId));

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
     * Deletes a record but does not do a commit.
     *
     * @param cache  the data cache
     * @param conn   a connection checked out from the cache (for the LEGACY schema)
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean deleteNoCommit(final Cache cache, final DbConnection conn, final RawSthomework record)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE serial_nbr=", conn.sqlLongValue(record.serialNbr),
                " AND version=", conn.sqlStringValue(record.version),
                " AND stu_id=", conn.sqlStringValue(record.stuId));

        try (final Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql) == 1;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        }
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records for a student. Results are sorted by homework date, then finish time.
     *
     * @param cache the data cache
     * @param stuId the student for which to query homeworks
     * @param all   {@code true} to include all homeworks, {@code false} to include only those with passing status of
     *              "Y" or "N"
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> queryByStudent(final Cache cache, final String stuId,
                                                     final boolean all) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                conn.sqlStringValue(stuId), (all ? CoreConstants.EMPTY : " AND (passed='Y' OR passed='N')"),
                " ORDER BY hw_dt,finish_time");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all homework records for a student in a particular course. Results are sorted by homework date, then finish
     * time.
     *
     * @param cache  the data cache
     * @param stuId  the student for which to query homeworks
     * @param course the course for which to query homeworks
     * @param all    {@code true} to include all homeworks, {@code false} to include only those with passing status of
     *               "Y" or "N"
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> queryByStudentCourse(final Cache cache, final String stuId, final String course,
                                                           final boolean all) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "   AND course=", conn.sqlStringValue(course),
                (all ? CoreConstants.EMPTY : " AND (passed='Y' OR passed='N')"),
                " ORDER BY hw_dt,finish_time");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all homework records for a student in a particular course. Results are sorted by homework date, then finish
     * time.
     *
     * @param cache  the data cache
     * @param stuId  the student for which to query homeworks
     * @param course the course for which to query homeworks
     * @param unit   the unit for which to query homeworks
     * @param all    {@code true} to include all homeworks, {@code false} to include only those with passing status of
     *               "Y" or "N"
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> queryByStudentCourseUnit(final Cache cache, final String stuId,
                                                               final String course, final Integer unit,
                                                               final boolean all) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "   AND course=", conn.sqlStringValue(course),
                "   AND unit=", conn.sqlIntegerValue(unit),
                (all ? CoreConstants.EMPTY : " AND (passed='Y' OR passed='N')"),
                " ORDER BY hw_dt,finish_time");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all homework records for a student in a particular course. Results are sorted by homework date, then finish
     * time.
     *
     * @param cache     the data cache
     * @param stuId     the student for which to query homeworks
     * @param course    the course for which to query homeworks
     * @param unit      the unit for which to query homeworks
     * @param objective the objective for which to query homeworks
     * @param all       {@code true} to include all homeworks, {@code false} to include only those with passing status
     *                  of "Y" or "N"
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> queryByStudentCourseUnitObjective(final Cache cache, final String stuId,
                                                                        final String course, final Integer unit,
                                                                        final Integer objective,
                                                                        final boolean all) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "   AND course=", conn.sqlStringValue(course),
                "   AND unit=", conn.sqlIntegerValue(unit),
                "   AND objective=", conn.sqlIntegerValue(objective),
                (all ? CoreConstants.EMPTY : " AND (passed='Y' OR passed='N')"),
                " ORDER BY hw_dt,finish_time");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all homeworks within a certain course of a specified type.
     *
     * @param cache         the data cache
     * @param stuId         the student for which to query homeworks
     * @param course        the course for which to query homeworks
     * @param passedOnly    {@code true} to return only homeworks with passed = 'Y'
     * @param homeworkTypes the types of homeworks for which to query
     * @return the list of matching homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> getHomeworks(final Cache cache, final String stuId, final String course,
                                                   final boolean passedOnly, final String... homeworkTypes)
            throws SQLException {

        final List<RawSthomework> homeworks = queryByStudentCourse(cache, stuId, course, passedOnly);

        final int count = homeworks.size();
        final List<RawSthomework> result = new ArrayList<>(count);

        accumulateHomeworks(result, homeworks, passedOnly, homeworkTypes);

        return result;
    }

    /**
     * Gets all homeworks within a certain course of a specified type.
     *
     * @param cache         the data cache
     * @param stuId         the student for which to query homeworks
     * @param course        the course for which to query homeworks
     * @param unit          the unit for which to query homeworks
     * @param passedOnly    {@code true} to return only homeworks with passed = 'Y'
     * @param homeworkTypes the types of homeworks for which to query (if null or empty, all will be queried)
     * @return the list of matching homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthomework> getHomeworks(final Cache cache, final String stuId, final String course,
                                                   final Integer unit, final boolean passedOnly,
                                                   final String... homeworkTypes) throws SQLException {

        final List<RawSthomework> homeworks = queryByStudentCourseUnit(cache, stuId, course, unit, passedOnly);

        final int count = homeworks.size();
        final List<RawSthomework> result = new ArrayList<>(count);

        accumulateHomeworks(result, homeworks, passedOnly, homeworkTypes);

        return result;
    }

    /**
     * Accumulates matching homeworks from a homeworks map.
     *
     * @param result        the list to which to add matching homeworks
     * @param homeworks     the list of homeworks to scan for matches
     * @param passedOnly    {@code true} to return only homeworks with passed = 'Y'
     * @param homeworkTypes the types of homeworks for which to query (if null or empty, all will be queried)
     */
    private static void accumulateHomeworks(final Collection<? super RawSthomework> result,
                                            final Iterable<RawSthomework> homeworks, final boolean passedOnly,
                                            final String... homeworkTypes) {

        for (final RawSthomework test : homeworks) {
            if (passedOnly && !"Y".equals(test.passed)) {
                continue;
            }

            if (homeworkTypes == null || homeworkTypes.length == 0) {
                result.add(test);
            } else {
                final String type = test.hwType;
                for (final String homeworkType : homeworkTypes) {
                    if (homeworkType.equals(type)) {
                        result.add(test);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Retrieves all {@code RawSthomework} records created in the past N days (counting today) for a specified course,
     * organized into one list per day, sorted by activity date.
     *
     * @param cache   the data cache
     * @param history the list to which to add N lists of records
     * @param numDays the number of days (N)
     * @param today   today's date
     * @param courses the ID of the course for which to gather history
     * @throws SQLException if there is an error accessing the database
     */
    public static void getHistory(final Cache cache, final Collection<? super List<RawSthomework>> history,
                                  final int numDays, final LocalDate today, final String... courses)
            throws SQLException {

        // Get the earliest date for which to return data...
        LocalDate earliest;
        if (numDays <= 1) {
            earliest = today;
        } else {
            earliest = today.minus(Period.ofDays(numDays - 1));
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final HtmlBuilder sql = new HtmlBuilder(200);
        sql.add("SELECT * FROM ", tableName);

        final int numCourses = courses.length;
        if (numCourses == 1) {
            sql.add(" WHERE course=", conn.sqlStringValue(courses[0]));
        } else {
            sql.add(" WHERE course IN (", conn.sqlStringValue(courses[0]));
            for (int i = 1; i < numCourses; ++i) {
                sql.add(CoreConstants.COMMA_CHAR).add(conn.sqlStringValue(courses[i]));
            }
            sql.add(')');
        }

        sql.add(" AND hw_dt>=", conn.sqlDateValue(earliest), " ORDER BY hw_dt,finish_time");

        try {
            final List<RawSthomework> all = executeQuery(conn, sql.toString());
            all.sort(new RawSthomework.FinishDateTimeComparator());

            int start = 0;
            int position = 0;
            final int size = all.size();
            for (int i = 0; i < numDays; ++i) {
                while (position < size && all.get(position).hwDt.equals(earliest)) {
                    ++position;
                }

                final List<RawSthomework> daily = new ArrayList<>(position - start);
                for (int j = start; j < position; ++j) {
                    daily.add(all.get(j));
                }
                history.add(daily);
                start = position;

                earliest = earliest.plusDays(1L);
            }
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the finish time and score in a student homework record. This is called each time a question is answered
     * correctly.
     *
     * @param cache         the data cache
     * @param serial        the serial number of the record to update
     * @param version       the version of the record to update
     * @param stuId         the student ID of the record to update
     * @param newFinishTime the new finish time
     * @param newScore      the new score
     * @param newPassed     the new passed value
     * @return true if update succeeded; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateFinishTimeScore(final Cache cache, final Long serial,
                                                final String version, final String stuId, final int newFinishTime,
                                                final int newScore, final String newPassed) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET finish_time=", Integer.toString(newFinishTime),
                ", hw_score=", Integer.toString(newScore),
                ", passed=", conn.sqlStringValue(newPassed),
                " WHERE serial_nbr=", conn.sqlLongValue(serial),
                "   AND version=", conn.sqlStringValue(version),
                "   AND stu_id=", conn.sqlStringValue(stuId));

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
     * Updates the "passed" field of a record. The fields in the record that constitute its primary key are used to
     * select the record to update.
     *
     * @param cache     the data cache
     * @param rec       the object to update
     * @param newPassed the new "passed" value
     * @return true if successful; false if not
     * @throws SQLException if there is an error updating the record
     */
    public static boolean updatePassed(final Cache cache, final RawSthomework rec,
                                       final String newPassed) throws SQLException {

        final boolean result;

        if (rec.stuId.startsWith("99")) {
            Log.info("Skipping update of StudentExam for test student:");
            Log.info("  Student ID: ", rec.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET passed=", conn.sqlStringValue(newPassed),
                    " WHERE serial_nbr=", conn.sqlLongValue(rec.serialNbr),
                    "   AND version=", conn.sqlStringValue(rec.version),
                    "   AND stu_id=", conn.sqlStringValue(rec.stuId));

            try (final Statement stmt = conn.createStatement()) {
                result = stmt.executeUpdate(sql) > 0;

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
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawSthomework> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawSthomework> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawSthomework.fromResultSet(rs));
            }
        }

        return result;
    }
}
