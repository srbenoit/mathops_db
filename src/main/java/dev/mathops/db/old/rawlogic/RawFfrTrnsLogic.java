package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A utility class to work with "ffr_trns" records.
 *
 * <pre>
 * Table:  'ffr_trns'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * exam_placed          char(1)                   no
 * exam_dt              date                      no
 * dt_cr_refused        date                      yes
 * grade                char(2)                   yes
 * </pre>
 */
public enum RawFfrTrnsLogic {
    ;

    /** Test student IDs that have transfer credit for 118-126 (but not 117). */
    private static final List<String> transfer118through126;

    /** Test student IDs that have transfer credit for 125-126. */
    private static final List<String> transfer125through126;

    /** Test student IDs that have transfer credit for 117-124. */
    private static final List<String> transfer117through124;

    static {
        transfer118through126 = Arrays.asList("999011124", "999012124", "999012424");

        transfer125through126 = Arrays.asList("999011132", "999012132", "999012432");

        transfer117through124 = List.of("999033211");
    }

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "ffr_trns" : (schemaPrefix + ".ffr_trns");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawFfrTrns record) throws SQLException {

        if (record.stuId == null || record.course == null || record.examPlaced == null || record.examDt == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawFfrTrns for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                    " (stu_id,course,exam_placed,exam_dt,dt_cr_refused,grade) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlStringValue(record.examPlaced), ",",
                    conn.sqlDateValue(record.examDt), ",",
                    conn.sqlDateValue(record.dtCrRefused), ",",
                    conn.sqlStringValue(record.grade), ")");

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
     * Updates the grade on a record.
     *
     * @param cache    the data cache
     * @param record   the record to update
     * @param newGrade the new grade
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateGrade(final Cache cache, final RawFfrTrns record, final String newGrade)
            throws SQLException {

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping update of RawFfrTrns for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName, " set grade=",
                    conn.sqlStringValue(newGrade),
                    " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                    "  AND course=", conn.sqlStringValue(record.course));

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
    public static boolean delete(final Cache cache, final RawFfrTrns record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
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
    public static List<RawFfrTrns> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all transfer credit configurations.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of matching transfer credit records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawFfrTrns> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final List<RawFfrTrns> result;

        if (!stuId.isEmpty() && stuId.charAt(0) == '9') {
            result = queryByTestStudent(stuId);
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                    " WHERE stu_id=", conn.sqlStringValue(stuId));

            try {
                result = executeListQuery(conn, sql);
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
    private static List<RawFfrTrns> executeListQuery(final DbConnection conn, final String sql)
            throws SQLException {

        final List<RawFfrTrns> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawFfrTrns.fromResultSet(rs));
            }
        }

        return result;
    }

    /**
     * Queries for the set of placement credits on record for a student.
     *
     * @param studentId the ID of the student to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     */
    private static List<RawFfrTrns> queryByTestStudent(final String studentId) {

        final List<RawFfrTrns> result = new ArrayList<>(3);

        final char ch3 = studentId.charAt(2);
        final char ch4 = studentId.charAt(3);

        if (ch3 == 'P') {
            getMathPlanTestStudent(studentId, result);
        } else if (ch3 == 'C' && ch4 == 'I') {
            getCheckinTestStudent(studentId, result);
        } else {
            Log.warning("Invalid test student ID: " + studentId);
        }

        return result;
    }

    /**
     * Gets a test student configured to test some aspect of the Math Plan.
     *
     * @param studentId the student ID
     * @param result    a list to which to add generated test records
     */
    private static void getMathPlanTestStudent(final String studentId, final Collection<? super RawFfrTrns> result) {

        if (transfer118through126.contains(studentId)) {
            result.add(makeRecord(studentId, RawRecordConstants.M118, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M124, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M125, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M126, "A"));
        } else if (transfer125through126.contains(studentId)) {
            result.add(makeRecord(studentId, RawRecordConstants.M125, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M126, "A"));
        } else if (transfer117through124.contains(studentId)) {
            result.add(makeRecord(studentId, RawRecordConstants.M117, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M118, "A"));
            result.add(makeRecord(studentId, RawRecordConstants.M124, "A"));
        }
    }

    /**
     * Gets a test student configured to test some aspect of Checkin.
     *
     * @param studentId the student ID
     * @param result    a list to which to add generated test records
     */
    private static void getCheckinTestStudent(final String studentId, final Collection<? super RawFfrTrns> result) {

        final char ch5 = studentId.charAt(4);
        final char ch6 = studentId.charAt(5);

        if (ch5 == 'C' && ch6 == 'H') {
            // Challenge Exam test cases
            final String id = studentId.substring(6);

            switch (id) {
                case "012" -> result.add(makeRecord(studentId, "M 055", "S"));
                case "013" -> result.add(makeRecord(studentId, "M 093", "S"));
                case "014" -> result.add(makeRecord(studentId, "M 099", "S"));
                case "015" -> result.add(makeRecord(studentId, "M 002", null));
                case "016", "021" -> result.add(makeRecord(studentId, RawRecordConstants.M117, "A"));
                case "031" -> result.add(makeRecord(studentId, RawRecordConstants.M118, "A"));
                case "041" -> result.add(makeRecord(studentId, RawRecordConstants.M124, "A"));
                case "051" -> result.add(makeRecord(studentId, RawRecordConstants.M125, "A"));
            }
        }
    }

    /**
     * Creates a transfer credit record.
     *
     * @param studentId the student ID
     * @param course    the course
     * @return the generated record
     */
    private static RawFfrTrns makeRecord(final String studentId, final String course, final String grade) {

        return new RawFfrTrns(studentId, course, "T", LocalDate.now(), null, grade);
    }
}
