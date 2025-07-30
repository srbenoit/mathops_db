package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawPlcFee;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "plc_fee" records.
 *
 * <pre>
 * Table:  'plc_fee'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                    no      PK
 * course               char(6)                    no      PK
 * exam_dt              date                       no
 * bill_dt              date                       no
 *
 * </pre>
 */
public enum RawPlcFeeLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "plc_fee" : (schemaPrefix + ".plc_fee");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawPlcFee record) throws SQLException {

        if (record.stuId == null || record.course == null || record.examDt == null || record.billDt == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,course,exam_dt,bill_dt) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlDateValue(record.examDt), ",",
                conn.sqlDateValue(record.billDt), ")");

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
    public static boolean delete(final Cache cache, final RawPlcFee record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course));

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
    public static List<RawPlcFee> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawPlcFee> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawPlcFee.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries every record in the database.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the complete set of records in the database
     * @throws SQLException if there is an error accessing the database
     */
    public static RawPlcFee queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeSingleQuery(conn,
                    "SELECT * FROM " + tableName + " WHERE stu_id=" + conn.sqlStringValue(stuId));
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries the most recent date of a billing cycle.
     *
     * @param cache the data cache
     * @return the most recent bill date found
     * @throws SQLException if there is an error accessing the database
     */
    public static LocalDate queryMostRecentBillDate(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT MAX(bill_dt) FROM " + tableName;

        LocalDate result = null;

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                final Date tmp = rs.getDate(1);
                result = tmp == null ? null : tmp.toLocalDate();
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Executes a query that returns a single record.
     *
     * @param conn the database connection
     * @param sql  the query
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    private static RawPlcFee executeSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawPlcFee result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawPlcFee.fromResultSet(rs);
            }
        }

        return result;
    }
}
