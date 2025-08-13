package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawSthomework;
import dev.mathops.db.old.rawrecord.RawSthwqa;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "sthwqa" records.
 *
 * <pre>
 * Table:  'sthwqa'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * serial_nbr           integer                   no      PK
 * question_nbr         smallint                  no      PK
 * answer_nbr           smallint                  no      PK
 * objective            char(6)                   no
 * stu_answer           varchar(100)              no
 * stu_id               char(9)                   no
 * version              char(5)                   no
 * ans_correct          char(1)                   no
 * hw_dt                date                      no
 * finish_time          integer                   yes
 * </pre>
 */
public enum RawSthwqaLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "sthwqa" : (schemaPrefix + ".sthwqa");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawSthwqa record) throws SQLException {

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawSthwqa for test student:");
            Log.info("  Student ID: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (serial_nbr,question_nbr,",
                    "answer_nbr,objective,stu_answer,stu_id,version,ans_correct,hw_dt,finish_time) VALUES (",
                    conn.sqlLongValue(record.serialNbr), ",",
                    conn.sqlIntegerValue(record.questionNbr), ",",
                    conn.sqlIntegerValue(record.answerNbr), ",",
                    conn.sqlStringValue(record.objective), ",",
                    conn.sqlStringValue(record.stuAnswer), ",",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.version), ",",
                    conn.sqlStringValue(record.ansCorrect), ",",
                    conn.sqlDateValue(record.hwDt), ",",
                    conn.sqlIntegerValue(record.finishTime), ")");

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
    public static boolean delete(final Cache cache, final RawSthwqa record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE serial_nbr=", conn.sqlLongValue(record.serialNbr),
                " AND question_nbr=", conn.sqlIntegerValue(record.questionNbr),
                " AND answer_nbr=", conn.sqlIntegerValue(record.answerNbr));

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
    public static boolean deleteNoCommit(final Cache cache, final DbConnection conn, final RawSthwqa record)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE serial_nbr=", conn.sqlLongValue(record.serialNbr),
                " AND question_nbr=", conn.sqlIntegerValue(record.questionNbr),
                " AND answer_nbr=", conn.sqlIntegerValue(record.answerNbr));

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
    public static List<RawSthwqa> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthwqa> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                conn.sqlStringValue(stuId));

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records for a single homework attempt, identified by serial number.
     *
     * @param cache  the data cache
     * @param serial the serial number
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawSthwqa> queryBySerial(final Cache cache, final Long serial) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE serial_nbr=",
                conn.sqlLongValue(serial));

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Deletes all records for a homework attempt.
     *
     * @param cache  the data cache
     * @param record the homework attempt whose corresponding answer records to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */

    public static boolean deleteAllForAttempt(final Cache cache, final RawSthomework record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE serial_nbr=", conn.sqlLongValue(record.serialNbr));

        try (final Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            conn.commit();
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return true;
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawSthwqa> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawSthwqa> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawSthwqa.fromResultSet(rs));
            }
        }

        return result;
    }
}
