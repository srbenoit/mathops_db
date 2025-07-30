package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawParameters;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with parameters records.
 *
 * <pre>
 * Table:  'parameters'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * pgm_name             char(20)                  no      PK
 * parm1                char(20)                  yes
 * parm2                char(20)                  yes
 * parm3                char(20)                  yes
 * parm4                char(20)                  yes
 * parm5                char(20)                  yes
 * parm6                char(20)                  yes
 * parm7                char(20)                  yes
 * parm8                char(20)                  yes
 * parm9                char(20)                  yes
 * parm10               date                      yes
 * </pre>
 */
public enum RawParametersLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "parameters" : (schemaPrefix + ".parameters");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawParameters record) throws SQLException {

        if (record.pgmName == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (pgm_name,parm1,parm2,parm3,parm4,parm5,parm6,parm7,parm8,parm9,parm10) VALUES (",
                conn.sqlStringValue(record.pgmName), ",",
                conn.sqlStringValue(record.parm1), ",",
                conn.sqlStringValue(record.parm2), ",",
                conn.sqlStringValue(record.parm3), ",",
                conn.sqlStringValue(record.parm4), ",",
                conn.sqlStringValue(record.parm5), ",",
                conn.sqlStringValue(record.parm6), ",",
                conn.sqlStringValue(record.parm7), ",",
                conn.sqlStringValue(record.parm8), ",",
                conn.sqlStringValue(record.parm9), ",",
                conn.sqlDateValue(record.parm10), ")");

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
    public static boolean delete(final Cache cache, final RawParameters record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE pgm_name=", conn.sqlStringValue(record.pgmName));

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
    public static List<RawParameters> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawParameters> result = new ArrayList<>(20);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawParameters.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for the parameters having a given program.
     *
     * @param cache   the data cache
     * @param pgmName the program name
     * @return the parameters model; {@code null} if not found or an error occurs
     * @throws SQLException if there is an error performing the query
     */
    public static RawParameters query(final Cache cache, final String pgmName) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return doSingleQuery(conn, SimpleBuilder.concat(
                    "SELECT * FROM ", tableName, " WHERE pgm_name=", conn.sqlStringValue(pgmName)));
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Updates the value of the 'parm1' parameter for a specified program.
     *
     * @param cache    the data cache
     * @param pgmName  the program name
     * @param newParm1 the new value for 'parm1'
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error performing the query
     */
    public static boolean updateParm1(final Cache cache, final String pgmName,
                                      final String newParm1) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName, " SET parm1=", conn.sqlStringValue(newParm1),
                " WHERE pgm_name=", conn.sqlStringValue(pgmName));

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
     * Updates the value of the 'parm2' parameter for a specified program.
     *
     * @param cache    the data cache
     * @param pgmName  the program name
     * @param newParm2 the new value for 'parm2'
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error performing the query
     */
    public static boolean updateParm2(final Cache cache, final String pgmName,
                                      final String newParm2) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName, " SET parm2=", conn.sqlStringValue(newParm2),
                " WHERE pgm_name=", conn.sqlStringValue(pgmName));

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
     * Updates the value of the 'parm10' parameter for a specified program.
     *
     * @param cache     the data cache
     * @param pgmName   the program name
     * @param newParm10 the new value for 'parm10'
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error performing the query
     */
    public static boolean updateParm10(final Cache cache, final String pgmName,
                                       final LocalDate newParm10) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName, " SET parm10=", conn.sqlDateValue(newParm10),
                " WHERE pgm_name=", conn.sqlStringValue(pgmName));

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
     * Performs a query that returns single record.
     *
     * @param conn the database connection
     * @param sql  the query SQL
     * @return the record; null if none returned
     * @throws SQLException if there is an error performing the query
     */
    private static RawParameters doSingleQuery(final DbConnection conn, final String sql) throws SQLException {

        RawParameters result = null;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawParameters.fromResultSet(rs);
            }
        }

        return result;
    }
}
