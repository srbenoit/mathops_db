package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawMpeLog;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with mpe_log records.
 *
 * <pre>
 * Table:  'mpe_log'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * stu_id               char(9)           no      PK
 * academic_yr          char(4)           yes
 * course               char(6)           no      PK
 * version              char(5)           no      PK
 * start_dt             date              no      PK
 * exam_dt              date              yes
 * recover_dt           date              yes
 * serial_nbr           integer           no
 * start_time           integer           no      PK
 * calc_nbr             char(4)           yes
 * </pre>
 */
public enum RawMpeLogLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "mpe_log" : (schemaPrefix + ".mpe_log");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawMpeLog record) throws SQLException {

        final boolean result;

        if (record.stuId.startsWith("99")) {
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "INSERT INTO ", tableName, " (stu_id,academic_yr,course,version,start_dt,",
                    "exam_dt,recover_dt,serial_nbr,start_time,calc_nbr) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.academicYr), ",",
                    conn.sqlStringValue(record.course), ",",
                    conn.sqlStringValue(record.version), ",",
                    conn.sqlDateValue(record.startDt), ",",
                    conn.sqlDateValue(record.examDt), ",",
                    conn.sqlDateValue(record.recoverDt), ",",
                    conn.sqlLongValue(record.serialNbr), ",",
                    conn.sqlIntegerValue(record.startTime), ",",
                    conn.sqlStringValue(record.calcNbr), ")");

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
    public static boolean delete(final Cache cache, final RawMpeLog record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND version=", conn.sqlStringValue(record.version),
                "  AND start_dt=", conn.sqlDateValue(record.startDt),
                "  AND start_time=", conn.sqlIntegerValue(record.startTime),
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
    public static List<RawMpeLog> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawMpeLog> result = new ArrayList<>(500);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawMpeLog.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Updates an {@code mpe_log} record to indicate the exam was finished. This updates the finish date and optionally
     * the recovered date.
     *
     * @param cache     the data cache
     * @param stuId     the student ID of the record to update
     * @param startDt   the start date of the record to update
     * @param startTime the start time of the record to update
     * @param examDt    the new exam (finish) date
     * @param recoverDt the new recovery date (null if the exam was not recovered)
     * @return true if successful; false if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean indicateFinished(final Cache cache, final String stuId, final LocalDate startDt,
                                           final Integer startTime, final LocalDate examDt,
                                           final LocalDate recoverDt) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "UPDATE ", tableName, " SET exam_dt=",
                    conn.sqlDateValue(examDt), ", recover_dt=",
                    conn.sqlDateValue(recoverDt), " WHERE stu_id=",
                    conn.sqlStringValue(stuId), " AND start_dt=",
                    conn.sqlDateValue(startDt), " AND start_time=",
                    conn.sqlIntegerValue(startTime));

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
}
