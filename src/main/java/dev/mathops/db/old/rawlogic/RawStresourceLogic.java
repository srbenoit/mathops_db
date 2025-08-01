package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawStresource;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "stresource" records.
 *
 * <pre>
 * Table:  'stresource'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * resource_id          char(7)                   no      PK
 * loan_dt              date                      no      PK
 * start_time           integer                   no      PK
 * due_dt               date                      no
 * return_dt            date                      yes
 * finish_time          integer                   yes
 * times_display        smallint                  no
 * create_dt            date                      yes
 * </pre>
 */
public enum RawStresourceLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stresource" : (schemaPrefix + ".stresource");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStresource record) throws SQLException {

        if (record.stuId == null || record.resourceId == null || record.loanDt == null || record.startTime == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,resource_id,loan_dt,start_time,",
                "due_dt,return_dt,finish_time,times_display,create_dt) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.resourceId), ",",
                conn.sqlDateValue(record.loanDt), ",",
                conn.sqlIntegerValue(record.startTime), ",",
                conn.sqlDateValue(record.dueDt), ",",
                conn.sqlDateValue(record.returnDt), ",",
                conn.sqlIntegerValue(record.finishTime), ",",
                conn.sqlIntegerValue(record.timesDisplay), ",",
                conn.sqlDateValue(record.createDt), ")");

        try (final Statement s = conn.createStatement()) {
            result = s.executeUpdate(sql) == 1;

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
    public static boolean delete(final Cache cache, final RawStresource record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND resource_id=", conn.sqlStringValue(record.resourceId),
                "  AND loan_dt=", conn.sqlDateValue(record.loanDt),
                "  AND start_time=", conn.sqlIntegerValue(record.startTime));

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
    public static List<RawStresource> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all resource loans for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of matching records
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStresource> queryByStudent(final Cache cache, final String stuId) throws SQLException {

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
     * Gets the record of an outstanding loan of a resource.
     *
     * @param cache      the data cache
     * @param resourceId the resource ID
     * @return the list of matching records
     * @throws SQLException if there is an error performing the query
     */
    public static RawStresource queryOutstanding(final Cache cache, final String resourceId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE resource_id=", conn.sqlStringValue(resourceId),
                " AND return_dt IS NULL");

        try {
            return executeSingleQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the return date and finish time.
     *
     * @param cache      the data cache
     * @param record     the record to update
     * @param returnDate the new return date
     * @param finishTime the new finish time
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateReturnDateTime(final Cache cache, final RawStresource record,
                                               final LocalDate returnDate, final Integer finishTime)
            throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET return_dt=", conn.sqlDateValue(returnDate), ",",
                "    finish_time=", conn.sqlIntegerValue(finishTime),
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND resource_id=", conn.sqlStringValue(record.resourceId),
                "   AND loan_dt=", conn.sqlDateValue(record.loanDt),
                "   AND start_time=", conn.sqlIntegerValue(record.startTime));

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
    private static List<RawStresource> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStresource> result = new ArrayList<>(20);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStresource.fromResultSet(rs));
            }
        }

        return result;
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static RawStresource executeSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawStresource result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawStresource.fromResultSet(rs);
            }
        }

        return result;
    }
}
