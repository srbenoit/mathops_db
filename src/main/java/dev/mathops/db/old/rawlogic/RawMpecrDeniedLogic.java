package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawMpecrDenied;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with mpecr_denied records.
 *
 * <pre>
 * Table:  'mpecr_denied'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * stu_id               char(9)           no      PK
 * course               char(6)           no      PK
 * exam_placed          char(1)           no
 * exam_dt              date              no
 * why_denied           char(2)           no
 * serial_nbr           integer           yes     PK
 * version              char(5)           yes
 * exam_source          char(2)           yes
 * </pre>
 */
public enum RawMpecrDeniedLogic {
    ;

    /** The field code to indicate denial by prerequisite failure. */
    public static final String DENIED_BY_PREREQ = "PQ";

    /** The field code to indicate denial by validation failure. */
    public static final String DENIED_BY_VAL = "V";

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "mpecr_denied" : (schemaPrefix + ".mpecr_denied");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawMpecrDenied record) throws SQLException {

        final boolean result;

        if (record.stuId.startsWith("99")) {
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,course,exam_placed,exam_dt,",
                    "why_denied,serial_nbr,version,exam_source) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlStringValue(record.examPlaced), ",",
                    conn.sqlDateValue(record.examDt), ",",
                    conn.sqlStringValue(record.whyDenied), ",",
                    conn.sqlLongValue(record.serialNbr), ",",
                    conn.sqlStringValue(record.version), ",",
                    conn.sqlStringValue(record.examSource), ")");

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
    public static boolean delete(final Cache cache, final RawMpecrDenied record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt),
                "  AND serial_nbr=", conn.sqlLongValue(record.serialNbr));

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
    public static List<RawMpecrDenied> queryAll(final Cache cache) throws SQLException {

        final List<RawMpecrDenied> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String tableName = getTableName(cache);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                result.add(RawMpecrDenied.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for all records for a student.
     *
     * @param cache the data cache
     * @param stuId the ID of the student to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawMpecrDenied> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                conn.sqlStringValue(stuId));

        final List<RawMpecrDenied> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawMpecrDenied.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for all records for a single exam.
     *
     * @param cache     the data cache
     * @param serialNbr the serial number of the exam to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawMpecrDenied> queryByExam(final Cache cache, final Long serialNbr) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE serial_nbr=",
                conn.sqlLongValue(serialNbr));

        final List<RawMpecrDenied> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawMpecrDenied.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
