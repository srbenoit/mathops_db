package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawStpaceSummary;
import dev.mathops.db.field.TermKey;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stpace_summary" records.
 *
 * <pre>
 * Table:  'stpace_summary'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * i_in_progress        char(1)                   no
 * pace                 smallint                  no
 * pace_track           char(2)                   yes
 * pace_order           smallint                  no
 * ms_nbr               smallint                  no      PK
 * ms_unit              smallint                  no
 * ms_date              date                      no
 * new_ms_date          char(1)                   yes
 * exam_dt              date                      no
 * re_points            smallint                  no
 * </pre>
 */
public enum RawStpaceSummaryLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stpace_summary" : (schemaPrefix + ".stpace_summary");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStpaceSummary record) throws SQLException {

        if (record.stuId == null || record.course == null || record.sect == null
            || record.termKey == null || record.iInProgress == null || record.pace == null
            || record.paceOrder == null || record.msNbr == null || record.msUnit == null
            || record.msDate == null || record.examDt == null || record.rePoints == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,course,sect,term,term_yr,",
                "i_in_progress,pace,pace_track,pace_order,ms_nbr,ms_unit,ms_date,",
                "new_ms_date,exam_dt,re_points) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlStringValue(record.sect), ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                conn.sqlIntegerValue(record.termKey.shortYear), ",",
                conn.sqlStringValue(record.iInProgress), ",",
                conn.sqlIntegerValue(record.pace), ",",
                conn.sqlStringValue(record.paceTrack), ",",
                conn.sqlIntegerValue(record.paceOrder), ",",
                conn.sqlIntegerValue(record.msNbr), ",",
                conn.sqlIntegerValue(record.msUnit), ",",
                conn.sqlDateValue(record.msDate), ",",
                conn.sqlStringValue(record.newMsDate), ",",
                conn.sqlDateValue(record.examDt), ",",
                conn.sqlIntegerValue(record.rePoints), ")");

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
    public static boolean delete(final Cache cache, final RawStpaceSummary record) throws SQLException {

        final boolean result;

        final HtmlBuilder builder = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        builder.add("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND sect=", conn.sqlStringValue(record.sect),
                "  AND term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND ms_nbr=", conn.sqlIntegerValue(record.msNbr));

        final String sql = builder.toString();

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
    public static List<RawStpaceSummary> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = "SELECT * FROM " + tableName;

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all records for a particular student, course, and term.
     *
     * @param cache   the data cache
     * @param stuId   the student ID
     * @param course  the course ID
     * @param sect    the section number
     * @param termKey the term key
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStpaceSummary> queryByStudentCourseSectionTerm(final Cache cache, final String stuId,
                                                                         final String course, final String sect,
                                                                         final TermKey termKey)
            throws SQLException {

        final HtmlBuilder builder = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        builder.add("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND course=", conn.sqlStringValue(course),
                "  AND sect=", conn.sqlStringValue(sect),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        final String sql = builder.toString();

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStpaceSummary> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStpaceSummary> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStpaceSummary.fromResultSet(rs));
            }
        }

        return result;
    }
}
