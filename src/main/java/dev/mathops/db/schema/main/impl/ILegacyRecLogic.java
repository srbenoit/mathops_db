package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.RecBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface implemented by record logic implementations.
 *
 * @param <T> the record type
 */
public interface ILegacyRecLogic<T extends RecBase> extends IRecLogic<T> {

    /**
     * Executes an update SQL statement that SHOULD alter one row.
     *
     * @param cache the data cache
     * @param sql   the query SQL
     * @return true of the statement succeeded and indicated one row was changed; false otherwise
     * @throws SQLException if there is an error performing the update
     */
    default boolean doUpdateOneRow(final Cache cache, final String sql) throws SQLException {

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

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
     * Performs a query that returns single record.
     *
     * @param cache the data cache
     * @param sql   the query SQL
     * @return the record; null if none returned
     * @throws SQLException if there is an error performing the query
     */
    default T doSingleQuery(final Cache cache, final String sql) throws SQLException {

        T result = null;

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                result = fromResultSet(rs);
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Performs a query that returns list of records.
     *
     * @param cache the data cache
     * @param sql   the query SQL
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    default List<T> doListQuery(final Cache cache, final String sql) throws SQLException {

        final List<T> result = new ArrayList<>(10);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                final T rec = fromResultSet(rs);
                result.add(rec);
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
