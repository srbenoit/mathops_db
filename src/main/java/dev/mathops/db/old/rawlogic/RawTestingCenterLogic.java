package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawTestingCenter;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to look up testing centers.
 *
 * <pre>
 * Table:  'testing_centers'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * testing_center_id    char(14)                  no      PK
 * tc_name              char(40)                  no
 * addres_1             char(35)                  yes
 * addres_2             char(35)                  yes
 * addres_3             char(35)                  yes
 * city                 char(18)                  no
 * state                char(2)                   no
 * zip_code             char(10)                  no
 * active               char(1)                   no
 * dtime_created        datetime year to second   no
 * dtime_approved       datetime year to second   yes
 * dtime_denied         datetime year to second   yes
 * dtime_revoked        datetime year to second   yes
 * is_remote            char(1)                   no
 * is_proctored         char(1)                   no
 * </pre>
 */
public enum RawTestingCenterLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "testing_centers" : (schemaPrefix + ".testing_centers");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawTestingCenter record) throws SQLException {

        if (record.testingCenterId == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (testing_center_id,tc_name,addres_1,addres_2,addres_3,city,state,zip_code,active,dtime_created,",
                "dtime_approved,dtime_denied,dtime_revoked,is_remote,is_proctored) VALUES (",
                conn.sqlStringValue(record.testingCenterId), ",",
                conn.sqlStringValue(record.tcName), ",",
                conn.sqlStringValue(record.addres1), ",",
                conn.sqlStringValue(record.addres2), ",",
                conn.sqlStringValue(record.addres3), ",",
                conn.sqlStringValue(record.city), ",",
                conn.sqlStringValue(record.state), ",",
                conn.sqlStringValue(record.zipCode), ",",
                conn.sqlStringValue(record.active), ",",
                conn.sqlDateTimeValue(record.dtimeCreated), ",",
                conn.sqlDateTimeValue(record.dtimeApproved), ",",
                conn.sqlDateTimeValue(record.dtimeDenied), ",",
                conn.sqlDateTimeValue(record.dtimeRevoked), ",",
                conn.sqlStringValue(record.isRemote), ",",
                conn.sqlStringValue(record.isProctored), ")");

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
    public static boolean delete(final Cache cache, final RawTestingCenter record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE testing_center_id=",
                conn.sqlStringValue(record.testingCenterId));

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
    public static List<RawTestingCenter> queryAll(final Cache cache) throws SQLException {

        final List<RawTestingCenter> result = new ArrayList<>(10);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String tableName = getTableName(cache);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                result.add(RawTestingCenter.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Gets the record with a specified testing center ID.
     *
     * @param cache           the data cache
     * @param testingCenterId the ID
     * @return the record; {@code null} if not found
     * @throws SQLException if there is an error accessing the database
     */
    public static RawTestingCenter query(final Cache cache, final String testingCenterId)
            throws SQLException {

        RawTestingCenter result = null;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE testing_center_id=",
                conn.sqlStringValue(testingCenterId));

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawTestingCenter.fromResultSet(rs);
                if (rs.next()) {
                    Log.warning("Multiple testing center records with id ", testingCenterId);
                }
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
