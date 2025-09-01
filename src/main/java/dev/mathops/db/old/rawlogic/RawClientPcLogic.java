package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawClientPc;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with client PCs (testing stations).
 *
 * <pre>
 * Table:  'client_pc'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * computer_id          char(40)                  no      PK
 * testing_center_id    char(14)                  no
 * station_nbr          char(10)                  yes
 * computer_desc        varchar(80)               yes
 * icon_x               smallint                  yes
 * icon_y               smallint                  yes
 * pc_usage             char(1)                   no
 * current_status       smallint                  no
 * dtime_created        datetime year to second   no
 * dtime_approved       datetime year to second   yes
 * mac_address          char(12)                  yes
 * power_status         char(1)                   yes
 * current_stu_id       char(9)                   yes
 * current_course       char(6)                   yes
 * current_unit         smallint                  yes
 * current_version      char(5)                   yes
 * </pre>
 */
public enum RawClientPcLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "client_pc" : (schemaPrefix + ".client_pc");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawClientPc record)
            throws SQLException {

        if (record.computerId == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (computer_id,testing_center_id,",
                "station_nbr,computer_desc,icon_x,icon_y,pc_usage,current_status,dtime_created,dtime_approved,",
                "mac_address,power_status,power_on_due,last_ping,current_stu_id,current_course,",
                "current_unit,current_version) VALUES (",
                conn.sqlStringValue(record.computerId), ",",
                conn.sqlStringValue(record.testingCenterId), ",",
                conn.sqlStringValue(record.stationNbr), ",",
                conn.sqlStringValue(record.computerDesc), ",",
                conn.sqlIntegerValue(record.iconX), ",",
                conn.sqlIntegerValue(record.iconY), ",",
                conn.sqlStringValue(record.pcUsage), ",",
                conn.sqlIntegerValue(record.currentStatus), ",",
                conn.sqlDateTimeValue(record.dtimeCreated), ",",
                conn.sqlDateTimeValue(record.dtimeApproved), ",",
                conn.sqlStringValue(record.macAddress), ",",
                conn.sqlStringValue(record.powerStatus), ",",
                conn.sqlIntegerValue(record.powerOnDue), ",",
                conn.sqlIntegerValue(record.lastPing), ",",
                conn.sqlStringValue(record.currentStuId), ",",
                conn.sqlStringValue(record.currentCourse), ",",
                conn.sqlIntegerValue(record.currentUnit), ",",
                conn.sqlStringValue(record.currentVersion), ")");

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
    public static boolean delete(final Cache cache, final RawClientPc record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE computer_id=", conn.sqlStringValue(record.computerId));

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
    public static List<RawClientPc> queryAll(final Cache cache) throws SQLException {

        final List<RawClientPc> result = new ArrayList<>(200);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                result.add(RawClientPc.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for all testing stations in a testing center.
     *
     * @param cache           the data cache
     * @param testingCenterId the ID of the testing center
     * @return the list of client PCs
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawClientPc> queryByTestingCenter(final Cache cache,
                                                         final String testingCenterId) throws SQLException {

        final List<RawClientPc> result = new ArrayList<>(100);

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE testing_center_id='",
                testingCenterId, "'");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawClientPc.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Queries for a testing station by its ID.
     *
     * @param cache      the data cache
     * @param computerId the ID of the client PC to query
     * @return the testing station; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public static RawClientPc query(final Cache cache, final String computerId) throws SQLException {

        RawClientPc result = null;

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE computer_id='", computerId, "'");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = RawClientPc.fromResultSet(rs);
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Updates the 'current_status' field of a client_pc record.
     *
     * @param cache            the data cache
     * @param computerId       the computer ID of the record to update
     * @param newCurrentStatus the new "current_status" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateCurrentStatus(final Cache cache, final String computerId,
                                              final Integer newCurrentStatus) throws SQLException {

        final HtmlBuilder sql = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        sql.add("UPDATE ", tableName, " SET current_status=", newCurrentStatus, " WHERE computer_id='", computerId,
                "'");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql.toString()) == 1;

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
     * Updates the 'current_status', 'current_stu_id', 'current_course', 'current_unit', and 'current_version' fields of
     * a client_pc record.
     *
     * @param cache             the data cache
     * @param computerId        the computer ID of the record to update
     * @param newCurrentStatus  the new "current_status" field value
     * @param newCurrentStuId   the new "current_stu_id" field value
     * @param newCurrentCourse  the new "current_course" field value
     * @param newCurrentUnit    the new "current_unit" field value
     * @param newCurrentVersion the new "current_version" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateAllCurrent(final Cache cache, final String computerId, final Integer newCurrentStatus,
                                           final String newCurrentStuId, final String newCurrentCourse,
                                           final Integer newCurrentUnit, final String newCurrentVersion)
            throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET current_status=", conn.sqlIntegerValue(newCurrentStatus), ",",
                " current_stu_id=", conn.sqlStringValue(newCurrentStuId), ",",
                " current_course=", conn.sqlStringValue(newCurrentCourse), ",",
                " current_unit=", conn.sqlIntegerValue(newCurrentUnit), ",",
                " current_version=", conn.sqlStringValue(newCurrentVersion),
                " WHERE computer_id='", computerId, "'");

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
     * Updates the 'pc_usage' field of a client_pc record.
     *
     * @param cache      the data cache
     * @param computerId the computer ID of the record to update
     * @param newPcUsage the new "pc_usage" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updatePcUsage(final Cache cache, final String computerId,
                                        final String newPcUsage) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET pc_usage=", conn.sqlStringValue(newPcUsage),
                " WHERE computer_id='", computerId, "'");

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
     * Updates the 'power_status' field of a client_pc record.
     *
     * @param cache          the data cache
     * @param computerId     the computer ID of the record to update
     * @param newPowerStatus the new "power_status" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updatePowerStatus(final Cache cache, final String computerId,
                                            final String newPowerStatus) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET power_status=", conn.sqlStringValue(newPowerStatus),
                " WHERE computer_id='", computerId, "'");

        try (final Statement stmt = conn.createStatement()) {
            final int numRows = stmt.executeUpdate(sql);
            final boolean result = numRows == 1;

            if (result) {
                conn.commit();
            } else {
                Log.warning("Power status update matched " + numRows + " rows");
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
     * Updates the 'power_on_due' field of a client_pc record.
     *
     * @param cache         the data cache
     * @param computerId    the computer ID of the record to update
     * @param newPowerOnDue the new "power_on_due" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updatePowerOnDue(final Cache cache, final String computerId,
                                           final Integer newPowerOnDue) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET power_on_due=", conn.sqlIntegerValue(newPowerOnDue),
                " WHERE computer_id='", computerId, "'");

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
     * Updates the 'power_on_due' field of a client_pc record, and if the last ping time is not null, updates the
     * 'power_status' field.
     *
     * @param cache       the data cache
     * @param computerId  the computer ID of the record to update
     * @param newLastPing the new "last_ping" field value
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean updateLastPing(final Cache cache, final String computerId,
                                         final Integer newLastPing) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final HtmlBuilder htm = new HtmlBuilder(50);
        htm.add("UPDATE ", tableName, " SET last_ping=", conn.sqlIntegerValue(newLastPing));
        if (newLastPing != null) {
            htm.add(", power_status=2");
        }
        htm.add(" WHERE computer_id='", computerId, "'");

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(htm.toString()) == 1;

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
}
