package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawCunit;
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

        final String sql = SimpleBuilder.concat(
                "INSERT INTO cunit (course,unit,term,term_yr,unit_exam_wgt,unit_desc,unit_timelimit,possible_score,",
                "nbr_questions,unit_type) VALUES (",
                LogicUtils.sqlStringValue(record.course), ",",
                record.unit, ",",
                LogicUtils.sqlStringValue(record.termKey.termCode), ",",
                record.termKey.shortYear, ",",
                LogicUtils.sqlFloatValue(record.unitExamWgt), ",",
                LogicUtils.sqlStringValue(record.unitDesc), ",",
                LogicUtils.sqlIntegerValue(record.unitTimelimit), ",",
                LogicUtils.sqlIntegerValue(record.possibleScore), ",",
                LogicUtils.sqlIntegerValue(record.nbrQuestions), ",",
                LogicUtils.sqlStringValue(record.unitType), ")");

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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

        final String sql = SimpleBuilder.concat("DELETE FROM cunit ",
                "WHERE course=", LogicUtils.sqlStringValue(record.course),
                "  AND unit=", LogicUtils.sqlIntegerValue(record.unit),
                "  AND term=", LogicUtils.sqlStringValue(record.termKey.termCode),
                "  AND term_yr=", LogicUtils.sqlIntegerValue(record.termKey.shortYear));

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final boolean result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
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

        return executeListQuery(cache, "SELECT * FROM cunit");
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

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM cunit WHERE term='", termKey.termCode, "' AND term_yr=", termKey.shortYear);

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
