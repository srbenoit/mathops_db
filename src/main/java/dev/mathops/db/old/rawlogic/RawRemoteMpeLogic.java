package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawRemoteMpe;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "remote_mpe" records.
 *
 * <pre>
 * Table:  'testing_centers'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * apln_term            char(4)                   no      PK
 * course               char(6)                   yes     PK
 * start_dt             date                      no      PK
 * end_dt               date                      no
 * </pre>
 */
public enum RawRemoteMpeLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "remote_mpe" : (schemaPrefix + ".remote_mpe");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawRemoteMpe record) throws SQLException {

        if (record.termKey == null || record.aplnTerm == null || record.course == null
            || record.startDt == null || record.endDt == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (term,term_yr,apln_term,course,start_dt,end_dt) VALUES (",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                conn.sqlTermValue(record.aplnTerm), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlDateValue(record.startDt), ",",
                conn.sqlDateValue(record.endDt), ")");

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
    public static boolean delete(final Cache cache, final RawRemoteMpe record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND apln_term=", conn.sqlStringValue(record.aplnTerm.shortString),
                "  AND course=", conn.sqlStringValue(record.course),
                "  AND start_dt=", conn.sqlDateValue(record.startDt));

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
    public static List<RawRemoteMpe> queryAll(final Cache cache) throws SQLException {

        final List<RawRemoteMpe> result = new ArrayList<>(20);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                result.add(RawRemoteMpe.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries all records with a specified application term.
     *
     * @param cache           the data cache
     * @param applicationTerm the application term
     * @return the complete set of records in the database
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawRemoteMpe> queryByApplicationTerm(final Cache cache,
                                                            final String applicationTerm) throws SQLException {

        final List<RawRemoteMpe> result = new ArrayList<>(10);

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE apln_term='", applicationTerm, "'");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawRemoteMpe.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries all records with a specified course.
     *
     * @param cache  the data cache
     * @param course the course
     * @return the complete set of records in the database
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawRemoteMpe> queryByCourse(final Cache cache, final String course) throws SQLException {

        final List<RawRemoteMpe> result = new ArrayList<>(10);

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE course='", course, "'");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawRemoteMpe.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
