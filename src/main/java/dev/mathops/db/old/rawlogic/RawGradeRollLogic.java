package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawGradeRoll;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "grade_roll" records.
 *
 * <pre>
 * Table:  'grade_roll'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * fullname             char(26)                  no
 * grade_opt            char(2)                   yes
 * term                 char(2)                   yes     PK
 * term_yr              smallint                  yes     PK
 * </pre>
 */
public enum RawGradeRollLogic {
    ;

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawGradeRoll record)
            throws SQLException {

        if (record.stuId == null || record.course == null || record.sect == null || record.fullname == null
                || record.gradeOpt == null || record.termKey == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String sql = SimpleBuilder.concat("INSERT INTO grade_roll (",
                "stu_id,course,sect,fullname,grade_opt,term,term_yr) VALUES (",
                LogicUtils.sqlStringValue(record.stuId), ",",
                LogicUtils.sqlStringValue(record.course), ",",
                LogicUtils.sqlStringValue(record.sect), ",",
                LogicUtils.sqlStringValue(record.fullname), ",",
                LogicUtils.sqlStringValue(record.gradeOpt), ",",
                LogicUtils.sqlStringValue(record.termKey.termCode), ",",
                LogicUtils.sqlIntegerValue(record.termKey.shortYear), ")");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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
    public static boolean delete(final Cache cache, final RawGradeRoll record) throws SQLException {

        if (record.stuId == null || record.course == null || record.sect == null || record.termKey == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String sql = SimpleBuilder.concat("DELETE FROM grade_roll ",
                "WHERE stu_id=", LogicUtils.sqlStringValue(record.stuId),
                "  AND course=", LogicUtils.sqlStringValue(record.course),
                "  AND sect=", LogicUtils.sqlStringValue(record.sect),
                "  AND term=", LogicUtils.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", LogicUtils.sqlIntegerValue(record.termKey.shortYear));

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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
    public static List<RawGradeRoll> queryAll(final Cache cache) throws SQLException {

        return executeQuery(cache, "SELECT * FROM grade_roll");
    }

    /**
     * Queries for all students registered for a particular term. This method does not automatically scan for
     * "provisional" satisfaction of prerequisites, and does not return synthetic test data records.
     *
     * @param conn the database connection, checked out to this thread
     * @param term the active term
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawGradeRoll> queryByTerm(final Cache conn, final TermKey term)
            throws SQLException {

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM grade_roll WHERE term='", term.termCode,
                "' AND term_yr=", term.shortYear);

        return executeQuery(conn, sql);
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param cache the data cache
     * @param sql   the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawGradeRoll> executeQuery(final Cache cache, final String sql)
            throws SQLException {

        final List<RawGradeRoll> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawGradeRoll.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
