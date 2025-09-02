package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawStchallenge;
import dev.mathops.db.schema.legacy.rec.RawStchallengeqa;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stchallengeqa" records.
 *
 * <pre>
 * Table:  'stchallengeqa'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * version              char(5)                   no
 * exam_dt              date                      no      PK
 * finish_time          integer                   no      PK
 * question_nbr         smallint                  no      PK
 * stu_answer           char(5)                   yes
 * ans_correct          char(1)                   yes
 * </pre>
 */
public enum RawStchallengeqaLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stchallengeqa" : (schemaPrefix + ".stchallengeqa");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStchallengeqa record) throws SQLException {

        if (record.stuId == null || record.course == null || record.version == null
            || record.examDt == null || record.finishTime == null || record.questionNbr == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,course,version,exam_dt,finish_time,",
                "question_nbr,stu_answer,ans_correct) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlStringValue(record.version), ",",
                conn.sqlDateValue(record.examDt), ",",
                conn.sqlIntegerValue(record.finishTime), ",",
                conn.sqlIntegerValue(record.questionNbr), ",",
                conn.sqlStringValue(record.stuAnswer), ",",
                conn.sqlStringValue(record.ansCorrect), ")");

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
    public static boolean delete(final Cache cache, final RawStchallengeqa record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt),
                "  AND finish_time=", conn.sqlIntegerValue(record.finishTime),
                "  AND question_nbr=", conn.sqlIntegerValue(record.questionNbr));

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
     * Deletes all records related to a single challenge exam attempt.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean deleteAllForAttempt(final Cache cache, final RawStchallenge record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt));

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
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStchallengeqa> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawStchallengeqa> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStchallengeqa.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
