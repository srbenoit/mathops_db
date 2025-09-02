package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawFinalCroll;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to work with "final_croll" records.
 *
 * <pre>
 * Table:  'final_croll'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * pace_order           smallint                  yes
 * open_status          char(1)                   yes
 * grading_option       char(2)                   yes
 * completed            char(1)                   no
 * score                smallint                  yes
 * course_grade         char(2)                   yes
 * prereq_satis         char(1)                   yes
 * init_class_roll      char(1)                   no
 * stu_provided         char(1)                   yes
 * final_class_roll     char(1)                   no
 * exam_placed          char(1)                   yes
 * zero_unit            smallint                  yes
 * timeout_factor       decimal(3,2)              yes
 * forfeit_i            char(1)                   yes
 * i_in_progress        char(1)                   no
 * i_counted            char(1)                   yes
 * ctrl_test            char(1)                   no
 * deferred_f_dt        date                      yes
 * bypass_timeout       smallint                  no
 * instrn_type          char(2)                   yes
 * registration_stat+   char(2)                   yes
 * last_class_roll_dt   date                      yes      PK
 * i_term               char(2)                   yes
 * i_term_yr            smallint                  yes
 * i_deadline_dt        date                      yes
 * </pre>
 */
public enum RawFinalCrollLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "final_croll" : (schemaPrefix + ".final_croll");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawFinalCroll record) throws SQLException {

        if (record.stuId == null || record.course == null || record.sect == null || record.termKey == null
            || record.completed == null || record.initClassRoll == null || record.finalClassRoll == null
            || record.iInProgress == null || record.ctrlTest == null || record.bypassTimeout == null
            || record.lastClassRollDt == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,course,sect,term,term_yr,pace_order,open_status,grading_option,",
                "completed,score,course_grade,prereq_satis,init_class_roll,stu_provided,final_class_roll,exam_placed,",
                "zero_unit,timeout_factor,forfeit_i,i_in_progress,i_counted,ctrl_test,deferred_f_dt,bypass_timeout,",
                "instrn_type,registration_status,last_class_roll_dt,i_term,i_term_yr,i_deadline_dt) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlStringValue(record.sect), ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                conn.sqlIntegerValue(record.termKey.shortYear), ",",
                conn.sqlIntegerValue(record.paceOrder), ",",
                conn.sqlStringValue(record.openStatus), ",",
                conn.sqlStringValue(record.gradingOption), ",",
                conn.sqlStringValue(record.completed), ",",
                conn.sqlIntegerValue(record.score), ",",
                conn.sqlStringValue(record.courseGrade), ",",
                conn.sqlStringValue(record.prereqSatis), ",",
                conn.sqlStringValue(record.initClassRoll), ",",
                conn.sqlStringValue(record.stuProvided), ",",
                conn.sqlStringValue(record.finalClassRoll), ",",
                conn.sqlStringValue(record.examPlaced), ",",
                conn.sqlIntegerValue(record.zeroUnit), ",",
                conn.sqlFloatValue(record.timeoutFactor), ",",
                conn.sqlStringValue(record.forfeitI), ",",
                conn.sqlStringValue(record.iInProgress), ",",
                conn.sqlStringValue(record.iCounted), ",",
                conn.sqlStringValue(record.ctrlTest), ",",
                conn.sqlDateValue(record.deferredFDt), ",",
                conn.sqlIntegerValue(record.bypassTimeout), ",",
                conn.sqlStringValue(record.instrnType), ",",
                conn.sqlStringValue(record.registrationStatus), ",",
                conn.sqlDateValue(record.lastClassRollDt), ",",
                conn.sqlStringValue(record.iTermKey == null ? null : record.iTermKey.termCode), ",",
                conn.sqlIntegerValue(record.iTermKey == null ? null : record.iTermKey.shortYear), ",",
                conn.sqlDateValue(record.iDeadlineDt), ")");

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
    public static boolean delete(final Cache cache, final RawFinalCroll record) throws SQLException {

        final boolean result;

        final HtmlBuilder builder = new HtmlBuilder(100);

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        builder.add("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND course=", conn.sqlStringValue(record.course),
                "   AND sect=", conn.sqlStringValue(record.sect),
                "   AND term=", conn.sqlStringValue(record.termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear));

        if (record.openStatus == null) {
            builder.add("   AND open_status IS NULL");
        } else {
            builder.add("   AND open_status=", conn.sqlStringValue(record.openStatus));
        }

        if (record.lastClassRollDt == null) {
            builder.add("   AND last_class_roll_dt IS NULL");
        } else {
            builder.add("   AND last_class_roll_dt=", conn.sqlDateValue(record.lastClassRollDt));
        }

        final String sql = builder.toString();

        try (final Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql) == 1;

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawFinalCroll> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = "SELECT * FROM " + tableName;

        final List<RawFinalCroll> result = new ArrayList<>(1000);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawFinalCroll.fromResultSet(rs));
            }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }
}
