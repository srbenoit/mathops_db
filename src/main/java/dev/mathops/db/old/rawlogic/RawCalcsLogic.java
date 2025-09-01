package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawCalcs;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with testing calcs records.
 *
 * <pre>
 * Table:  'calcs'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * issued_nbr           char(4)                   no
 * return_nbr           char(7)                   no
 * serial_nbr           integer                   no      PK
 * exam_dt              date                      no      PK
 * </pre>
 */
public enum RawCalcsLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "calcs" : (schemaPrefix + ".calcs");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawCalcs record) throws SQLException {

        if (record.stuId == null || record.issuedNbr == null || record.returnNbr == null
            || record.serialNbr == null || record.examDt == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,issued_nbr,return_nbr,serial_nbr,exam_dt) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.issuedNbr), ",",
                conn.sqlStringValue(record.returnNbr), ",",
                conn.sqlLongValue(record.serialNbr), ",",
                conn.sqlDateValue(record.examDt), ")");

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
    public static boolean delete(final Cache cache, final RawCalcs record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND issued_nbr=", conn.sqlStringValue(record.issuedNbr),
                "   AND return_nbr=", conn.sqlStringValue(record.returnNbr),
                "   AND serial_nbr=", conn.sqlLongValue(record.serialNbr),
                "   AND exam_dt=", conn.sqlDateValue(record.examDt));

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) > 0;

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
    public static List<RawCalcs> queryAll(final Cache cache) throws SQLException {

        final List<RawCalcs> result = new ArrayList<>(50);

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawCalcs.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries all records with a specified student ID
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the complete set of records in the database
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawCalcs> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final List<RawCalcs> result = new ArrayList<>(10);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId));

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawCalcs.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for all testing calculator loan records with a specified calculator ID.
     *
     * @param cache        the data cache
     * @param calculatorId the calculator ID
     * @return the matching testing calculator loan record, {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public static RawCalcs queryByCalculatorId(final Cache cache, final String calculatorId) throws SQLException {

        RawCalcs result = null;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE issued_nbr=", conn.sqlStringValue(calculatorId));

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawCalcs.fromResultSet(rs);
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
