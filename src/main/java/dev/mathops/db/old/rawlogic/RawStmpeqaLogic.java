package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawStmpe;
import dev.mathops.db.old.rawrecord.RawStmpeqa;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stmpeqa" records.
 *
 * <pre>
 * Table:  'stmpeqa'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * version              char(5)                   no      PK
 * exam_dt              date                      yes     PK
 * finish_time          integer                   yes     PK
 * question_nbr         smallint                  yes     PK
 * stu_answer           char(5)                   yes
 * ans_correct          char(1)                   yes
 * subtest              char(3)                   yes
 * tree_ref             char(40)                  yes
 * </pre>
 */
public enum RawStmpeqaLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stmpeqa" : (schemaPrefix + ".stmpeqa");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStmpeqa record) throws SQLException {

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawStmpeqa for test student:");
            Log.info("  Student ID: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,version,exam_dt,finish_time,",
                    "question_nbr,stu_answer,ans_correct,subtest,tree_ref) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.version), ",",
                    conn.sqlDateValue(record.examDt), ",",
                    conn.sqlIntegerValue(record.finishTime), ",",
                    conn.sqlIntegerValue(record.questionNbr), ",",
                    conn.sqlStringValue(record.stuAnswer), ",",
                    conn.sqlStringValue(record.ansCorrect), ",",
                    conn.sqlStringValue(record.subtest), ",",
                    conn.sqlStringValue(record.treeRef), ")");

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
    public static boolean delete(final Cache cache, final RawStmpeqa record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql1 = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE version=", conn.sqlStringValue(record.version),
                "  AND stu_id=", conn.sqlStringValue(record.stuId),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt),
                "  AND finish_time=", conn.sqlIntegerValue(record.finishTime),
                "  AND question_nbr=", conn.sqlIntegerValue(record.questionNbr));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql1) == 1;

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
    public static List<RawStmpeqa> queryAll(final Cache cache) throws SQLException {

        final List<RawStmpeqa> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String tableName = getTableName(cache);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                result.add(RawStmpeqa.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Deletes all records for a placement attempt.
     *
     * @param cache  the data cache
     * @param record the placement attempt whose corresponding answer records to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */

    public static boolean deleteAllForExam(final Cache cache, final RawStmpe record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql1 = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE version=", conn.sqlStringValue(record.version),
                "  AND stu_id=", conn.sqlStringValue(record.stuId),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt),
                "  AND finish_time=", conn.sqlIntegerValue(record.finishTime));

        try (final Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql1);
            conn.commit();
        } finally {
            Cache.checkInConnection(conn);
        }

        return true;
    }
}
