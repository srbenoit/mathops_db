package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawStterm;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stterm" records.
 *
 * <pre>
 * Table:  'stterm'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * pace                 smallint                  no
 * pace_track           char(2)                   no
 * first_course         char(6)                   no
 * cohort               char(8)                   yes
 * urgency              smallint                  yes
 * do_not_disturb       char(1)                   yes
 * </pre>
 */
public enum RawSttermLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stterm" : (schemaPrefix + ".stterm");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStterm record) throws SQLException {

        if (record.stuId == null || record.termKey == null) {
            throw new SQLException("Null value in primary key or required field.");
        }
        if (record.pace == null) {
            throw new SQLException("Null value in required pace field for " + record.stuId);
        }
        if (record.paceTrack == null) {
            throw new SQLException("Null value in required pace_track field for " + record.stuId);
        }
        if (record.firstCourse == null) {
            throw new SQLException("Null value in required first_course field for " + record.stuId);
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            Log.info("Skipping insert of RawStterm for test student:");
            Log.info("stu_id: ", record.stuId);
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                    " (stu_id,term,term_yr,pace,pace_track,first_course,cohort,urgency,do_not_disturb) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.termKey.termCode), ",",
                    conn.sqlIntegerValue(record.termKey.shortYear), ",",
                    conn.sqlIntegerValue(record.pace), ",",
                    conn.sqlStringValue(record.paceTrack), ",",
                    conn.sqlStringValue(record.firstCourse), ",",
                    conn.sqlStringValue(record.cohort), ",",
                    conn.sqlIntegerValue(record.urgency), ",",
                    conn.sqlStringValue(record.doNotDisturb), ")");

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
    public static boolean delete(final Cache cache, final RawStterm record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear));

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
    public static List<RawStterm> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all student term configurations for a term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @return the list of matching student term records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStterm> queryAllByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all student term configurations.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStterm> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets the record for a student in a given term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @param stuId   the ID of the student to query
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static RawStterm query(final Cache cache, final TermKey termKey, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        try {
            return executeSingleQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the pace, pace track, and first course in a record. The record in the database whose term name, term
     * year, and student ID matches the provided values will be updated.
     *
     * @param cache       the data cache
     * @param stuId       the student ID (may not be null)
     * @param termKey     the term key (may not be null)
     * @param pace        the new pace
     * @param paceTrack   the new pace track (may not be null)
     * @param firstCourse the new first course (may not be null)
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updatePaceTrackFirstCourse(final Cache cache, final String stuId, final TermKey termKey,
                                                     final int pace, final String paceTrack, final String firstCourse)
            throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET pace=", Integer.toString(pace), ",",
                "     pace_track=", conn.sqlStringValue(paceTrack), ",",
                "     first_course=", conn.sqlStringValue(firstCourse),
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

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
     * Updates the cohort ID in a record.
     *
     * @param cache   the data cache
     * @param stuId   the student ID (may not be null)
     * @param termKey the term (may not be null)
     * @param cohort  the new cohort
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateCohort(final Cache cache, final String stuId, final TermKey termKey,
                                       final String cohort) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET cohort=", conn.sqlStringValue(cohort),
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

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
     * Updates the urgency in a record.
     *
     * @param cache   the data cache
     * @param stuId   the student ID (may not be null)
     * @param termKey the term (may not be null)
     * @param urgency the new urgency
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateUrgency(final Cache cache, final String stuId, final TermKey termKey,
                                        final Integer urgency) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET urgency=", conn.sqlIntegerValue(urgency),
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

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
     * Updates the canvas ID in a record.
     *
     * @param cache    the data cache
     * @param stuId    the student ID (may not be null)
     * @param termKey  the term (may not be null)
     * @param canvasId the new canvas ID
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    @Deprecated
    public static boolean updateCanvasId(final Cache cache, final String stuId, final TermKey termKey,
                                         final String canvasId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET canvas_id=", conn.sqlStringValue(canvasId),
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND term=", conn.sqlStringValue(termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

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
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStterm> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStterm> result = new ArrayList<>(10);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStterm.fromResultSet(rs));
            }
        }

        return result;
    }

    /**
     * Executes a query that returns a single record.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the matching record
     * @throws SQLException if there is an error accessing the database
     */
    private static RawStterm executeSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawStterm result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawStterm.fromResultSet(rs);
            }
        }

        return result;
    }
}
