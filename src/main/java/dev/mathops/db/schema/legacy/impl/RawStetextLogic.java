package dev.mathops.db.schema.legacy.impl;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawEtextCourse;
import dev.mathops.db.schema.legacy.rec.RawStetext;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stetext" records.
 *
 * <pre>
 * Table:  'stetext'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * etext_id             char(6)                   no      PK
 * active_dt            date                      no      PK
 * etext_key            char(20)                  yes
 * expiration_dt        date                      yes
 * refund_deadline_dt   date                      yes
 * refund_dt            date                      yes
 * refund_reason        char(60)                  yes
 * </pre>
 */
public enum RawStetextLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stetext" : (schemaPrefix + ".stetext");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStetext record) throws SQLException {

        if (record.stuId == null || record.etextId == null || record.activeDt == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawStetext for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,etext_id,active_dt,",
                    "etext_key,expiration_dt,refund_deadline_dt,refund_dt,refund_reason) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.etextId), ",",
                    conn.sqlDateValue(record.activeDt), ",",
                    conn.sqlStringValue(record.etextKey), ",",
                    conn.sqlDateValue(record.expirationDt), ",",
                    conn.sqlDateValue(record.refundDeadlineDt), ",",
                    conn.sqlDateValue(record.refundDt), ",",
                    conn.sqlStringValue(record.refundReason), ")");

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
    public static boolean delete(final Cache cache, final RawStetext record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "DELETE FROM ", tableName, " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND etext_id=", conn.sqlStringValue(record.etextId),
                "   AND active_dt=", conn.sqlDateValue(record.activeDt));

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
    public static List<RawStetext> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for an active (not refunded or expired) e-text for a student with a particular e-text ID.
     *
     * @param cache     the data cache
     * @param now       the date/time to consider as "now"
     * @param studentId the ID of the student to query
     * @param etextId   the e-text ID for which to query
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStetext> queryByStudentEtext(final Cache cache, final LocalDateTime now,
                                                       final String studentId, final String etextId)
            throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE stu_id=", conn.sqlStringValue(studentId),
                " AND etext_id=", conn.sqlStringValue(etextId),
                " AND refund_dt IS NULL ",
                " AND (expiration_dt IS NULL OR expiration_dt>=",
                conn.sqlDateValue(now.toLocalDate()), ")");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all purchased e-texts for a student, including all inactive (expired or refunded) e-texts. Results
     * are ordered by e-text ID, and includes any refunded or expired records.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStetext> queryByStudent(final Cache cache, final String studentId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE stu_id=", conn.sqlStringValue(studentId),
                " ORDER BY etext_id");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all non-refunded e-texts under a particular key, including whose which may have expired.
     *
     * @param cache the data cache
     * @param key   the e-text activation key
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStetext> queryUnrefundedByKey(final Cache cache, final String key) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE etext_key=", conn.sqlStringValue(key),
                "   AND refund_dt IS NULL");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for active purchased e-texts for a student in a particular course.
     *
     * @param cache     the data cache
     * @param now       the date/time to consider as "now"
     * @param studentId the ID of the student to query
     * @param courseId  the course for which to query
     * @return the list of models that matched the criteria, an empty list if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStetext> getStudentETexts(final Cache cache, final ZonedDateTime now, final String studentId,
                                                    final String courseId) throws SQLException {

        final List<RawStetext> result = new ArrayList<>(10);

        final List<RawEtextCourse> etextCourses = RawEtextCourseLogic.queryByCourse(cache, courseId);

        if (!etextCourses.isEmpty()) {
            // See which of those e-texts are owned by the student and currently active
            for (final RawEtextCourse etextCours : etextCourses) {
                final List<RawStetext> stetext = queryByStudentEtext(cache, now.toLocalDateTime(), studentId,
                        etextCours.etextId);
                result.addAll(stetext);
            }
        }

        return result;
    }

    /**
     * Queries for an active student-e-text record with a particular key.
     *
     * @param cache the data cache
     * @param key   the key
     * @return the model that matched the criteria, {@code null} if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static RawStetext getOwnerOfKey(final Cache cache, final String key) throws SQLException {

        RawStetext result = null;

        final List<RawStetext> list = queryUnrefundedByKey(cache, key);

        if (!list.isEmpty()) {
            result = list.getFirst();

            if (list.size() > 1) {
                Log.warning("Query returned multiple rows");
            }
        }

        return result;
    }

    /**
     * Updates the refund date and refund reason on the object.
     *
     * @param cache        the data cache
     * @param now          the date/time to consider as "now"
     * @param rec          the e-text purchase record to update
     * @param refundReason the reason for the refund
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean deactivate(final Cache cache, final LocalDateTime now, final RawStetext rec,
                                     final String refundReason) throws SQLException {

        final boolean result;

        if (rec.stuId.startsWith("99")) {
            Log.info("Skipping deactive of RawStetext for test student:");
            Log.info("stu_id: ", rec.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "UPDATE ", tableName, " SET refund_dt=", conn.sqlDateValue(now.toLocalDate()),
                    ", refund_reason=", conn.sqlStringValue(refundReason),
                    " WHERE stu_id=", conn.sqlStringValue(rec.stuId),
                    "   AND etext_id=", conn.sqlStringValue(rec.etextId),
                    "   AND active_dt=", conn.sqlDateValue(rec.activeDt));

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
     * Updates the refund deadline date in a student e-text record.
     *
     * @param cache          the data cache
     * @param studentId      the student ID
     * @param eTextId        the e-text ID
     * @param whenActive     the activation date/time
     * @param refundDeadline the new refund deadline
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateRefundDeadline(final Cache cache, final String studentId, final String eTextId,
                                               final LocalDate whenActive, final LocalDate refundDeadline)
            throws SQLException {

        final boolean result;

        if (studentId.startsWith("99")) {
            Log.info("Skipping update of RawStetext for test student:");
            Log.info("stu_id: ", studentId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "UPDATE ", tableName, " SET refund_deadline_dt=", conn.sqlDateValue(refundDeadline),
                    " WHERE stu_id=", conn.sqlStringValue(studentId),
                    "   AND etext_id=", conn.sqlStringValue(eTextId),
                    "   AND active_dt=", conn.sqlDateValue(whenActive));

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
     * Updates the refund date and refund reason in a student e-text record.
     *
     * @param cache        the data cache
     * @param stuId        the student ID
     * @param etextId      the e-text ID
     * @param activeDt     the activation date/time
     * @param refundDate   the new refund date
     * @param refundReason the new refund reason
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateRefund(final Cache cache, final String stuId, final String etextId,
                                       final LocalDate activeDt, final LocalDate refundDate, final String refundReason)
            throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of RawStetext for test student:");
            Log.info("stu_id: ", stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "UPDATE ", tableName, " SET refund_dt=", conn.sqlDateValue(refundDate),
                    ", refund_reason=", conn.sqlStringValue(refundReason),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND etext_id=", conn.sqlStringValue(etextId),
                    "   AND active_dt=", conn.sqlDateValue(activeDt));

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
    private static List<RawStetext> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStetext> result = new ArrayList<>(10);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStetext.fromResultSet(rs));
            }
        }

        return result;
    }
}
