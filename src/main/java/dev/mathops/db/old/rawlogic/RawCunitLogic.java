package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.schema.legacy.RawCunit;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with cunit records.
 *
 * <pre>
 * Table:  'cunit'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * course               char(6)                   no      PK
 * unit                 smallint                  no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * unit_exam_wgt        decimal(3,2)              yes
 * unit_desc            char(50)                  yes
 * unit_timelimit       smallint                  yes
 * possible_score       smallint                  yes
 * nbr_questions        smallint                  yes
 * unit_type            char(4)                   yes
 * </pre>
 */
public enum RawCunitLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "cunit" : (schemaPrefix + ".cunit");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawCunit record) throws SQLException {

        if (record.course == null || record.unit == null || record.termKey == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (course,unit,term,term_yr,unit_exam_wgt,unit_desc,unit_timelimit,possible_score,",
                "nbr_questions,unit_type) VALUES (",
                conn.sqlStringValue(record.course), ",",
                record.unit, ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                conn.sqlFloatValue(record.unitExamWgt), ",",
                conn.sqlStringValue(record.unitDesc), ",",
                conn.sqlIntegerValue(record.unitTimelimit), ",",
                conn.sqlIntegerValue(record.possibleScore), ",",
                conn.sqlIntegerValue(record.nbrQuestions), ",",
                conn.sqlStringValue(record.unitType), ")");

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
    public static boolean delete(final Cache cache, final RawCunit record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE course=", conn.sqlStringValue(record.course),
                "  AND unit=", conn.sqlIntegerValue(record.unit),
                "  AND term=", conn.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear));

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
    public static List<RawCunit> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return executeListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Retrieves all course units in a given term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @return the list of course units; {@code null} on any error
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawCunit> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE term='", termKey.termCode, "' AND term_yr=", termKey.shortYear);

        return executeListQuery(cache, sql);
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param cache the data cache
     * @param sql   the query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawCunit> executeListQuery(final Cache cache, final String sql) throws SQLException {

        final List<RawCunit> result = new ArrayList<>(50);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawCunit.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
