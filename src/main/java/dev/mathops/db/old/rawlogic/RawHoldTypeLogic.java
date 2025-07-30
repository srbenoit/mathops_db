package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawHoldType;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with hold_type records.
 *
 * <pre>
 * Table:  'hold_type'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * hold_id              char(2)                   yes     PK
 * sev_admin_hold       char(1)                   no
 * hold_type            char(10)                  no
 * add_hold             char(1)                   no
 * delete_hold          char(1)                   no
 * </pre>
 */
public enum RawHoldTypeLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "hold_type" : (schemaPrefix + ".hold_type");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawHoldType record) throws SQLException {

        if (record.holdId == null || record.sevAdminHold == null || record.holdType == null
            || record.addHold == null || record.deleteHold == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (hold_id,sev_admin_hold,hold_type,add_hold,delete_hold) VALUES (",
                conn.sqlStringValue(record.holdId), ",",
                conn.sqlStringValue(record.sevAdminHold), ",",
                conn.sqlStringValue(record.holdType), ",",
                conn.sqlStringValue(record.addHold), ",",
                conn.sqlStringValue(record.deleteHold), ")");

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
    public static boolean delete(final Cache cache, final RawHoldType record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE hold_id=", conn.sqlStringValue(record.holdId));

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
    public static List<RawHoldType> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawHoldType> result = new ArrayList<>(50);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawHoldType.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
