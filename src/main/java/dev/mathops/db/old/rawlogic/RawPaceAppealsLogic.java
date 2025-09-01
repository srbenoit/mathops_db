package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawPaceAppeals;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to look up pace_appeals by student, create new hold records, and delete hold records.
 *
 * <pre>
 * Table:  'pace_appeals'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * stu_id               char(9)           no      PK
 * term                 char(2)           no
 * term_yr              smallint          no
 * appeal_dt            date              no      PK
 * relief_given         char(1)           yes
 * pace                 smallint          no
 * pace_track           char(2)           yes
 * ms_nbr               smallint          no      PK
 * ms_type              char(8)           no      PK
 * ms_date              date              no
 * new_deadline_dt      date              yes
 * nbr_atmpts_allow     smallint          yes
 * circumstances        char(200)         no
 * comment              char(200)         yes
 * interviewer          char(20)          no
 * </pre>
 */
public enum RawPaceAppealsLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "pace_appeals" : (schemaPrefix + ".pace_appeals");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawPaceAppeals record) throws SQLException {

        // NOTE: This is a place where we send user-entered data into a table, so the insert is done
        // with prepared statements.

        final boolean result;

        if (record.stuId.startsWith("99")) {
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,term,term_yr,appeal_dt,",
                    "relief_given,pace,pace_track,ms_nbr,ms_type,ms_date,new_deadline_dt,nbr_atmpts_allow,",
                    "circumstances,comment,interviewer) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            try (final PreparedStatement ps = conn.prepareStatement(sql)) {
                LogicUtils.setPsString(ps, 1, record.stuId);
                LogicUtils.setPsString(ps, 2, record.termKey.termCode);
                LogicUtils.setPsInteger(ps, 3, record.termKey.shortYear);
                LogicUtils.setPsDate(ps, 4, record.appealDt);
                LogicUtils.setPsString(ps, 5, record.reliefGiven);
                LogicUtils.setPsInteger(ps, 6, record.pace);
                LogicUtils.setPsString(ps, 7, record.paceTrack);
                LogicUtils.setPsInteger(ps, 8, record.msNbr);
                LogicUtils.setPsString(ps, 9, record.msType);
                LogicUtils.setPsDate(ps, 10, record.msDate);
                LogicUtils.setPsDate(ps, 11, record.newDeadlineDt);
                LogicUtils.setPsInteger(ps, 12, record.nbrAtmptsAllow);
                LogicUtils.setPsString(ps, 13, record.circumstances);
                LogicUtils.setPsString(ps, 14, record.comment);
                LogicUtils.setPsString(ps, 15, record.interviewer);

                result = ps.executeUpdate() == 1;

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
    public static boolean delete(final Cache cache, final RawPaceAppeals record) throws SQLException {

        final boolean result;

        final HtmlBuilder sql = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND term=", conn.sqlStringValue(record.termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "   AND appeal_dt=", conn.sqlDateValue(record.appealDt),
                "   AND pace=", conn.sqlIntegerValue(record.pace),
                "   AND pace_track=", conn.sqlStringValue(record.paceTrack),
                "   AND ms_nbr=", conn.sqlIntegerValue(record.msNbr),
                "   AND ms_type=", conn.sqlStringValue(record.msType));

        try (final Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql.toString()) == 1;

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
     * Updates a record.
     *
     * @param cache  the data cache
     * @param record the record to update
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean update(final Cache cache, final RawPaceAppeals record) throws SQLException {

        final boolean result;

        final HtmlBuilder sql = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("UPDATE ", tableName,
                " SET relief_given=", conn.sqlStringValue(record.reliefGiven),
                ", new_deadline_dt=", conn.sqlDateValue(record.newDeadlineDt),
                ", nbr_atmpts_allow=", conn.sqlIntegerValue(record.nbrAtmptsAllow),
                ", interviewer=", conn.sqlStringValue(record.interviewer),
                ", circumstances=", conn.sqlStringValue(record.circumstances),
                ", comment=", conn.sqlStringValue(record.comment),
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND term=", conn.sqlStringValue(record.termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "   AND appeal_dt=", conn.sqlDateValue(record.appealDt),
                "   AND pace=", conn.sqlIntegerValue(record.pace),
                "   AND pace_track=", conn.sqlStringValue(record.paceTrack),
                "   AND ms_nbr=", conn.sqlIntegerValue(record.msNbr),
                "   AND ms_type=", conn.sqlStringValue(record.msType));

        final String sqlString = sql.toString();

        try (final Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sqlString) == 1;

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
    public static List<RawPaceAppeals> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all pace_appeals records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of pace_appeals records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawPaceAppeals> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE stu_id=", conn.sqlStringValue(stuId));

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
    private static List<RawPaceAppeals> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawPaceAppeals> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawPaceAppeals.fromResultSet(rs));
            }
        }

        return result;
    }
}
