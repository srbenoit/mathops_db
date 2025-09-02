package dev.mathops.db.schema.legacy.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.legacy.rec.RawApplicant;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to look up applicant records.
 *
 * <pre>
 * Table:  'applicant'
 *
 * Column name          Type              Nulls   Key
 * -------------------  ----------------  ------  -----
 * stu_id               char(9)           no      PK
 * first_name           char(30)          no
 * last_name            char(30)          no
 * birthdate            date              yes
 * ethnicity            char(2)           yes
 * gender               char(1)           yes
 * college              char(2)           yes
 * prog_study           char(16)          yes
 * hs_code              char(6)           yes
 * tr_credits           char(5)           yes
 * resident             char(4)           yes
 * resident_state       char(4)           yes
 * resident_county      char(6)           yes
 * hs_gpa               char(4)           yes
 * hs_class_rank        smallint          yes
 * hs_size_class        smallint          yes
 * act_score            smallint          yes
 * sat_score            smallint          yes
 * pidm                 integer           no
 * apln_term            char(6)           no
 * </pre>
 */
public enum RawApplicantLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "applicant" : (schemaPrefix + ".applicant");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawApplicant record) throws SQLException {

        if (record.stuId == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        if (record.stuId.startsWith("99")) {
            result = false;
        } else {
            final String tableName = getTableName(cache);

            final int aplnValue = record.aplnTerm.toNumeric();
            final String apln = Integer.toString(aplnValue);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat(
                    "INSERT INTO ", tableName, " (stu_id,first_name,last_name,birthdate,ethnicity,gender,college,",
                    "prog_study,hs_code,tr_credits,resident,resident_state,resident_county,hs_gpa,hs_class_rank,",
                    "hs_size_class,act_score,sat_score,pidm,apln_term) VALUES (",
                    conn.sqlStringValue(record.stuId), ",",
                    conn.sqlStringValue(record.firstName), ",",
                    conn.sqlStringValue(record.lastName), ",",
                    conn.sqlDateValue(record.birthdate), ",",
                    conn.sqlStringValue(record.ethnicity), ",",
                    conn.sqlStringValue(record.gender), ",",
                    conn.sqlStringValue(record.college), ",",
                    conn.sqlStringValue(record.progStudy), ",",
                    conn.sqlStringValue(record.hsCode), ",",
                    conn.sqlStringValue(record.trCredits), ",",
                    conn.sqlStringValue(record.resident), ",",
                    conn.sqlStringValue(record.residentState), ",",
                    conn.sqlStringValue(record.residentCounty), ",",
                    conn.sqlStringValue(record.hsGpa), ",",
                    conn.sqlIntegerValue(record.hsClassRank), ",",
                    conn.sqlIntegerValue(record.hsSizeClass), ",",
                    conn.sqlIntegerValue(record.actScore), ",",
                    conn.sqlIntegerValue(record.satScore), ",",
                    conn.sqlIntegerValue(record.pidm), ",",
                    conn.sqlStringValue(apln), ")");

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
        }

        return result;
    }

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean delete(final Cache cache, final RawApplicant record) throws SQLException {

        final boolean result;

        final HtmlBuilder sql = new HtmlBuilder(100);
        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("DELETE FROM ", tableName, " WHERE stu_id=", conn.sqlStringValue(record.stuId));

        try (final Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql.toString()) == 1;

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
    public static List<RawApplicant> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets all applicant records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of admin_hold records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawApplicant> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                    conn.sqlStringValue(stuId));

            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawApplicant> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawApplicant> result = new ArrayList<>(50);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawApplicant.fromResultSet(rs));
            }
        }

        return result;
    }
}
