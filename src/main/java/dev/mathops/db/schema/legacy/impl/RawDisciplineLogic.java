package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.LogicUtils;
import dev.mathops.db.schema.legacy.rec.RawDiscipline;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with discipline records.
 *
 * <pre>
 * Table:  'discipline'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * stu_id               char(9)           no      PK
 * dt_incident          date              no      PK
 * incident_type        char(2)           no      PK
 * course               char(6)           no      PK
 * unit                 smallint          no      PK
 * cheat_desc           char(100)         yes
 * action_type          char(2)           yes
 * action_comment       char(100)         yes
 * interviewer          char(20)          yes
 * proctor              char(20)          yes
 * </pre>
 */
public enum RawDisciplineLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "discipline" : (schemaPrefix + ".discipline");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawDiscipline record) throws SQLException {

        // NOTE: This is a place where we send user-entered data into a table, so the insert is done
        // with prepared statements.

        if (record.stuId == null || record.dtIncident == null || record.incidentType == null
            || record.course == null || record.unit == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,dt_incident,incident_type,course,unit,cheat_desc,",
                "action_type,action_comment,interviewer,proctor) VALUES (?,?,?,?,?,?,?,?,?,?)");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final PreparedStatement ps = conn.prepareStatement(sql)) {
            LogicUtils.setPsString(ps, 1, record.stuId);
            LogicUtils.setPsDate(ps, 2, record.dtIncident);
            LogicUtils.setPsString(ps, 3, record.incidentType);
            LogicUtils.setPsString(ps, 4, record.course);
            LogicUtils.setPsInteger(ps, 5, record.unit);
            LogicUtils.setPsString(ps, 6, record.cheatDesc);
            LogicUtils.setPsString(ps, 7, record.actionType);
            LogicUtils.setPsString(ps, 8, record.actionComment);
            LogicUtils.setPsString(ps, 9, record.interviewer);
            LogicUtils.setPsString(ps, 10, record.proctor);

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
    public static boolean delete(final Cache cache, final RawDiscipline record) throws SQLException {

        final boolean result;

        final HtmlBuilder sql = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                " AND dt_incident=", conn.sqlDateValue(record.dtIncident),
                " AND incident_type=", conn.sqlStringValue(record.incidentType),
                " AND course=", conn.sqlStringValue(record.course),
                " AND unit=", conn.sqlIntegerValue(record.unit));

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
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawDiscipline> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all discipline records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of discipline records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawDiscipline> queryByStudent(final Cache cache, final String stuId) throws SQLException {

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
     * Gets all discipline records for an action code.
     *
     * @param cache  the data cache
     * @param action the action code
     * @return the list of discipline records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawDiscipline> queryByActionCode(final Cache cache, final String action) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE action_type=", conn.sqlStringValue(action));

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
    private static List<RawDiscipline> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawDiscipline> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawDiscipline.fromResultSet(rs));
            }
        }

        return result;
    }
}
