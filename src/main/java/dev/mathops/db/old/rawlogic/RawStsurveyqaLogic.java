package dev.mathops.db.old.rawlogic;

import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.old.rawrecord.RawStsurveyqa;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A utility class to work with "stsurveyqa" records.
 *
 * <pre>
 * Table:  'stsurveyqa'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * resource_id          char(7)                   no      PK
 * loan_dt              date                      no      PK
 * start_time           integer                   no      PK
 * due_dt               date                      no
 * return_dt            date                      yes
 * finish_time          integer                   yes
 * times_display        smallint                  no
 * create_dt            date                      yes
 * </pre>
 */
public enum RawStsurveyqaLogic {
    ;

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stsurveyqa" : (schemaPrefix + ".stsurveyqa");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStsurveyqa record) throws SQLException {

        if (record.stuId == null || record.version == null || record.examDt == null
            || record.surveyNbr == null || record.finishTime == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,version,exam_dt,survey_nbr,stu_answer,finish_time) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.version), ",",
                conn.sqlDateValue(record.examDt), ",",
                conn.sqlIntegerValue(record.surveyNbr), ",",
                conn.sqlStringValue(record.stuAnswer), ",",
                conn.sqlIntegerValue(record.finishTime), ")");

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
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean delete(final Cache cache, final RawStsurveyqa record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "  AND version=", conn.sqlStringValue(record.version),
                "  AND exam_dt=", conn.sqlDateValue(record.examDt),
                "  AND finish_time=", conn.sqlIntegerValue(record.finishTime),
                "  AND survey_nbr=", conn.sqlIntegerValue(record.surveyNbr));

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
    public static List<RawStsurveyqa> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeListQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets the most recent set of survey responses submitted by a student. Responses are ordered by question number.
     *
     * @param cache the data cache
     * @param stuId the ID of the student whose responses to retrieve
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStsurveyqa> queryLatestByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId));

        try {
            final List<RawStsurveyqa> all = executeListQuery(conn, sql);

            // Filter for only the most recent response for each question
            final Map<Integer, RawStsurveyqa> questions = new TreeMap<>();

            for (final RawStsurveyqa record : all) {
                final RawStsurveyqa existing = questions.get(record.surveyNbr);

                if ((existing == null) || (record.compareTo(existing) > 0)) {
                    questions.put(record.surveyNbr, record);
                }
            }

            return new ArrayList<>(questions.values());
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Gets the most recent set of survey responses submitted by a student for a particular survey version. Responses
     * are ordered by question number.
     *
     * @param cache   the data cache
     * @param stuId   the ID of the student whose responses to retrieve
     * @param version the survey version whose responses to retrieve
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStsurveyqa> queryLatestByStudentProfile(final Cache cache, final String stuId,
                                                                  final String version) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(stuId),
                "  AND version=", conn.sqlStringValue(version));

        try {
            final List<RawStsurveyqa> all = executeListQuery(conn, sql);

            // Filter for only the most recent response for each question
            final Map<Integer, RawStsurveyqa> questions = new TreeMap<>();

            for (final RawStsurveyqa record : all) {
                final RawStsurveyqa existing = questions.get(record.surveyNbr);

                if ((existing == null) || (record.compareTo(existing) > 0)) {
                    questions.put(record.surveyNbr, record);
                }
            }

            return new ArrayList<>(questions.values());
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
    private static List<RawStsurveyqa> executeListQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStsurveyqa> result = new ArrayList<>(20);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStsurveyqa.fromResultSet(rs));
            }
        }

        return result;
    }
}
