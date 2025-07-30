package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawMilestone;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with 'milestone' records.
 *
 * <pre>
 * Table:  'milestone'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * term                 char(2)           no      PK
 * term_yr              smallint          no      PK
 * pace                 smallint          no
 * pace_track           char(2)           no      PK
 * ms_nbr               smallint          no      PK
 * ms_type              char(8)           no      PK
 * ms_date              date              no
 * nbr_atmpts_allow     smallint          yes
 * </pre>
 */
public enum RawMilestoneLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "milestone" : (schemaPrefix + ".milestone");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawMilestone record) throws SQLException {

        if (record.termKey == null || record.paceTrack == null || record.msNbr == null || record.msType == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (term,term_yr,pace,pace_track,ms_nbr,ms_type,ms_date,nbr_atmpts_allow)", " VALUES (",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                conn.sqlIntegerValue(record.pace), ",",
                conn.sqlStringValue(record.paceTrack), ",",
                record.msNbr, ",",
                conn.sqlStringValue(record.msType), ",",
                conn.sqlDateValue(record.msDate), ",",
                conn.sqlIntegerValue(record.nbrAtmptsAllow), ")");

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
    public static boolean delete(final Cache cache, final RawMilestone record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND pace=", conn.sqlIntegerValue(record.pace),
                "  AND pace_track=", conn.sqlStringValue(record.paceTrack),
                "  AND ms_nbr=", conn.sqlIntegerValue(record.msNbr),
                "  AND ms_type=", conn.sqlStringValue(record.msType));

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
    public static List<RawMilestone> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all milestone records.
     *
     * @param cache   the data cache
     * @param termKey the term key for which to return records
     * @return the list of milestone records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawMilestone> getAllMilestones(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM " + tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all milestone records for a specified pace, ordered by milestone date.
     *
     * @param cache     the data cache
     * @param termKey   the term key for which to return records
     * @param pace      the pace for which to return records
     * @param paceTrack the pace ID for which to return records
     * @return the list of milestone records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawMilestone> getAllMilestones(final Cache cache, final TermKey termKey, final int pace,
                                                      final String paceTrack) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                "   AND pace=", conn.sqlIntegerValue(Integer.valueOf(pace)),
                "   AND pace_track=", conn.sqlStringValue(paceTrack));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all milestone records for a specified pace, ordered by milestone date.
     *
     * @param cache     the data cache
     * @param milestone the milestone to update
     * @param newMsDate the new milestone date
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateMsDate(final Cache cache, final RawMilestone milestone,
                                       final LocalDate newMsDate) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET ms_date=", conn.sqlDateValue(newMsDate),
                " WHERE term=", conn.sqlStringValue(milestone.termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(milestone.termKey.shortYear),
                "   AND pace=", conn.sqlIntegerValue(milestone.pace),
                "   AND pace_track=", conn.sqlStringValue(milestone.paceTrack),
                "   AND ms_nbr=", conn.sqlIntegerValue(milestone.msNbr),
                "   AND ms_type=", conn.sqlStringValue(milestone.msType));

        boolean result;

        try (final Statement stmt = conn.createStatement()) {
            final int numRows = stmt.executeUpdate(sql);
            result = numRows == 1;

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
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawMilestone> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawMilestone> result = new ArrayList<>(10);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawMilestone.fromResultSet(rs));
            }
        }

        return result;
    }
}
