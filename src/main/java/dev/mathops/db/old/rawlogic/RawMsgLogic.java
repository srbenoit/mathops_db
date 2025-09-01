package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawMsg;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with 'msg' records.
 *
 * <pre>
 * Table:  'msg'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * term                 char(2)           no      PK
 * term_yr              smallint          no      PK
 * touch_point          char(3)           no      PK
 * msg_code             char(8)           no      PK
 * subject              char(50)          yes
 * template             text              yes
 * </pre>
 */
public enum RawMsgLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "msg" : (schemaPrefix + ".msg");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawMsg record) throws SQLException {

        if (record.termKey == null || record.touchPoint == null || record.msgCode == null) {
            throw new SQLException("Null value in primary key field.");
        }

        // FIXME: This needs to be a prepared statement! Subject and template text can include
        //  apostrophes or SQL escapes.

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (term,term_yr,touch_point,msg_code,subject,template) VALUES (?,?,?,?,?,?)");

        // Normal SQL cannot insert into "Text" field - need a Prepared Statement

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.termKey.termCode);
            ps.setInt(2, record.termKey.shortYear.intValue());
            ps.setString(3, record.touchPoint);
            ps.setString(4, record.msgCode);
            ps.setString(5, record.subject);
            ps.setString(6, record.template);

            final boolean result = ps.executeUpdate() == 1;

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
    public static boolean delete(final Cache cache, final RawMsg record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear),
                "  AND touch_point=", conn.sqlStringValue(record.touchPoint),
                "  AND msg_code=", conn.sqlStringValue(record.msgCode));

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
    public static List<RawMsg> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawMsg> result = new ArrayList<>(100);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawMsg.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
