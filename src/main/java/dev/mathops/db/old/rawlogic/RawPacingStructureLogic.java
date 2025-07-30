package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.old.rawrecord.RawPacingStructure;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "pacing_structure" records.
 *
 * <pre>
 * Table:  'pacing_structure'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * pacing_structure     char(1)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * def_pace_track       char(2)                   yes
 * require_licensed     char(1)                   no
 * require_partic       char(1)                   no
 * max_partic_missed    smallint                  no
 * allow_inc            char(1)                   no
 * max_courses          smallint                  no
 * nbr_open_allowed     smallint                  no
 * require_unit_exams   char(1)                   yes
 * use_midterms         char(1)                   yes
 * allow_coupons        char(1)                   yes
 * coupons_after_win+   char(1)                   yes
 * users_progress_cr    smallint                  yes
 * hw_progress_cr       smallint                  yes
 * re_progress_cr       smallint                  yes
 * ue_progress_cr       smallint                  yes
 * fin_progress_cr      smallint                  yes
 * pacing_name          char(30)                  yes
 * schedule_source      char(9)                   yes
 * sr_due_date_enfor+   char(1)                   yes
 * re_due_date_enfor+   char(1)                   yes
 * ue_due_date_enfor+   char(1)                   yes
 * fe_due_date_enfor+   char(1)                   yes
 * first_obj_avail      char(1)                   yes
 * </pre>
 */
public enum RawPacingStructureLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "pacing_structure" : (schemaPrefix + ".pacing_structure");
    }

    /**
     * Inserts a new pacing_structure record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawPacingStructure record)
            throws SQLException {

        if (record.termKey == null || record.pacingStructure == null || record.requirePartic == null
            || record.maxParticMissed == null || record.allowInc == null
            || record.maxCourses == null || record.nbrOpenAllowed == null) {

            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (term,term_yr,pacing_structure,def_pace_track,require_licensed,",
                "require_partic,max_partic_missed,allow_inc,max_courses,nbr_open_allowed,require_unit_exams,",
                "use_midterms,allow_coupons,coupons_after_window,users_progress_cr,hw_progress_cr,re_progress_cr,",
                "ue_progress_cr,fin_progress_cr,pacing_name,schedule_source,sr_due_date_enforced,re_due_date_enforced,",
                "ue_due_date_enforced,fe_due_date_enforced,first_obj_avail) VALUES (",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                conn.sqlStringValue(record.pacingStructure), ",",
                conn.sqlStringValue(record.defPaceTrack), ",",
                conn.sqlStringValue(record.requireLicensed), ",",
                conn.sqlStringValue(record.requirePartic), ",",
                conn.sqlIntegerValue(record.maxParticMissed), ",",
                conn.sqlStringValue(record.allowInc), ",",
                conn.sqlIntegerValue(record.maxCourses), ",",
                conn.sqlIntegerValue(record.nbrOpenAllowed), ",",
                conn.sqlStringValue(record.requireUnitExams), ",",
                conn.sqlStringValue(record.useMidterms), ",",
                conn.sqlStringValue(record.allowCoupons), ",",
                conn.sqlStringValue(record.couponsAfterWindow), ",",
                conn.sqlIntegerValue(record.usersProgressCr), ",",
                conn.sqlIntegerValue(record.hwProgressCr), ",",
                conn.sqlIntegerValue(record.reProgressCr), ",",
                conn.sqlIntegerValue(record.ueProgressCr), ",",
                conn.sqlIntegerValue(record.finProgressCr), ",",
                conn.sqlStringValue(record.pacingName), ",",
                conn.sqlStringValue(record.scheduleSource), ",",
                conn.sqlStringValue(record.srDueDateEnforced), ",",
                conn.sqlStringValue(record.reDueDateEnforced), ",",
                conn.sqlStringValue(record.ueDueDateEnforced), ",",
                conn.sqlStringValue(record.feDueDateEnforced), ",",
                conn.sqlStringValue(record.firstObjAvail), ")");

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
    public static boolean delete(final Cache cache, final RawPacingStructure record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE pacing_structure=", conn.sqlStringValue(record.pacingStructure),
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
    public static List<RawPacingStructure> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return executeListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Retrieves all pacing structures for a particular term.
     *
     * @param cache   the data cache
     * @param termKey the key of the term for which to query
     * @return the corresponding list of records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawPacingStructure> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", termKey.shortYear);

        return executeListQuery(cache, sql);
    }

    /**
     * Gets the pacing structure object that has a given term and pacing structure ID.
     *
     * @param cache           the data cache
     * @param termKey         the key of the term for which to query
     * @param pacingStructure the pacing structure ID for which to query
     * @return the matching records, null if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static RawPacingStructure query(final Cache cache, final TermKey termKey,
                                           final String pacingStructure) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE pacing_structure=", conn.sqlStringValue(pacingStructure),
                "   AND term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", termKey.shortYear);

        try {
            return executeSingleQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets a pacing structure within the active term.
     *
     * @param cache           the data cache
     * @param pacingStructure the ID of the pacing structure to retrieve
     * @return the pacing structure record; {@code null} if not found
     * @throws SQLException if there is an error accessing the database
     */
    public static RawPacingStructure query(final Cache cache, final String pacingStructure) throws SQLException {

        final SystemData systemData = cache.getSystemData();
        final TermRec activeTerm = systemData.getActiveTerm();

        Log.info("The active term is ", activeTerm);

        return query(cache, activeTerm.term, pacingStructure);
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param cache the data cache
     * @param sql   the query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawPacingStructure> executeListQuery(final Cache cache, final String sql) throws SQLException {

        final List<RawPacingStructure> result = new ArrayList<>(20);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawPacingStructure.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Executes a query that returns a single records.
     *
     * @param conn the database connection
     * @param sql  the query
     * @return the record found; null if none returned
     * @throws SQLException if there is an error accessing the database
     */
    private static RawPacingStructure executeSingleQuery(final DbConnection conn, final String sql)
            throws SQLException {

        RawPacingStructure result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawPacingStructure.fromResultSet(rs);
            }
        }

        return result;
    }
}
