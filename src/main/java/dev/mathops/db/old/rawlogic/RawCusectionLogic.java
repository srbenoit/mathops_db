package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawCusection;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with cusection records.
 *
 * <pre>
 * Table:  'cusection'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * unit                 smallint                  no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * timeout              smallint                  no
 * re_mastery_score     smallint                  yes
 * ue_mastery_score     smallint                  yes
 * hw_mastery_score     smallint                  no
 * hw_moveon_score      smallint                  no
 * nbr_atmpts_allow     smallint                  no
 * atmpts_per_review    smallint                  no
 * first_test_dt        date                      no
 * last_test_dt         date                      no
 * begin_test_period    integer                   no
 * end_test_period      integer                   no
 * coupon_cost          smallint                  yes
 * last_coupon_dt       date                      yes
 * show_test_window     char(1)                   yes
 * unproctored_exam     char(1)                   yes
 * re_points_ontime     smallint                  yes
 * </pre>
 */
public enum RawCusectionLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "cusection" : (schemaPrefix + ".cusection");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawCusection record) throws SQLException {

        if (record.course == null || record.sect == null || record.termKey == null) {
            throw new SQLException("Null value in primary key field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (course,sect,unit,term,term_yr,timeout,re_mastery_score,ue_mastery_score,",
                "hw_mastery_score,hw_moveon_score,nbr_atmpts_allow,atmpts_per_review,first_test_dt,last_test_dt,",
                "begin_test_period,end_test_period,coupon_cost,last_coupon_dt,show_test_window,unproctored_exam,",
                "re_points_ontime) VALUES (",
                conn.sqlStringValue(record.course), ",",
                conn.sqlStringValue(record.sect), ",",
                conn.sqlIntegerValue(record.unit), ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                conn.sqlIntegerValue(record.termKey.shortYear), ",",
                conn.sqlIntegerValue(record.timeout), ",",
                conn.sqlIntegerValue(record.reMasteryScore), ",",
                conn.sqlIntegerValue(record.ueMasteryScore), ",",
                conn.sqlIntegerValue(record.hwMasteryScore), ",",
                conn.sqlIntegerValue(record.hwMoveonScore), ",",
                conn.sqlIntegerValue(record.nbrAtmptsAllow), ",",
                conn.sqlIntegerValue(record.atmptsPerReview), ",",
                conn.sqlDateValue(record.firstTestDt), ",",
                conn.sqlDateValue(record.lastTestDt), ",",
                conn.sqlIntegerValue(record.beginTestPeriod), ",",
                conn.sqlIntegerValue(record.endTestPeriod), ",",
                conn.sqlIntegerValue(record.couponCost), ",",
                conn.sqlDateValue(record.lastCouponDt), ",",
                conn.sqlStringValue(record.showTestWindow), ",",
                conn.sqlStringValue(record.unproctoredExam), ",",
                conn.sqlIntegerValue(record.rePointsOntime), ")");

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
    public static boolean delete(final Cache cache, final RawCusection record) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE course=", conn.sqlStringValue(record.course),
                "  AND sect=", conn.sqlStringValue(record.sect),
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
    public static List<RawCusection> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all cusection records for a particular term.
     *
     * @param cache   the data cache
     * @param termKey the term key
     * @return the list of all course section unit records found
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawCusection> queryByTerm(final Cache cache, final TermKey termKey) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term=", conn.sqlStringValue(termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear));

        try {
            return executeListQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the query
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawCusection> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawCusection> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawCusection.fromResultSet(rs));
            }
        }

        return result;
    }
}
