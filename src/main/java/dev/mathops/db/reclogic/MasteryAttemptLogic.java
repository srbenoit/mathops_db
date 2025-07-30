package dev.mathops.db.reclogic;

import dev.mathops.db.Cache;
import dev.mathops.db.ESchema;
import dev.mathops.db.rec.MasteryAttemptRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with mastery_attempt records.
 */
public final class MasteryAttemptLogic implements ILegacyRecLogic<MasteryAttemptRec> {

    /** A single instance. */
    public static final MasteryAttemptLogic INSTANCE = new MasteryAttemptLogic();

    /** A field name for serialization of records. */
    private static final String FLD_SERIAL_NBR = "serial_nbr";

    /** A field name for serialization of records. */
    private static final String FLD_EXAM_ID = "exam_id";

    /** A field name for serialization of records. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name for serialization of records. */
    private static final String FLD_WHEN_STARTED = "when_started";

    /** A field name for serialization of records. */
    private static final String FLD_WHEN_FINISHED = "when_finished";

    /** A field name for serialization of records. */
    private static final String FLD_EXAM_SCORE = "exam_score";

    /** A field name for serialization of records. */
    private static final String FLD_MASTERY_SCORE = "mastery_score";

    /** A field name for serialization of records. */
    private static final String FLD_PASSED = "passed";

    /** A field name for serialization of records. */
    private static final String FLD_IS_FIRST_PASSED = "is_first_passed";

    /** A field name for serialization of records. */
    private static final String FLD_EXAM_SOURCE = "exam_source";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private MasteryAttemptLogic() {

        super();
    }

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     * @return the table name
     */
    public static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "mastery_attempt" : (schemaPrefix + ".mastery_attempt");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public boolean insert(final Cache cache, final MasteryAttemptRec record) throws SQLException {

        if (record.serialNbr == null || record.examId == null || record.stuId == null
            || record.whenStarted == null || record.whenFinished == null
            || record.examScore == null || record.passed == null) {
            throw new SQLException("Null value in required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (serial_nbr,exam_id,stu_id,when_started,when_finished,exam_score,",
                "mastery_score,passed,is_first_passed,exam_source) VALUES (",
                sqlIntegerValue(record.serialNbr), ",",
                sqlStringValue(record.examId), ",",
                sqlStringValue(record.stuId), ",",
                sqlDateTimeValue(record.whenStarted), ",",
                sqlDateTimeValue(record.whenFinished), ",",
                sqlIntegerValue(record.examScore), ",",
                sqlIntegerValue(record.masteryScore), ",",
                sqlStringValue(record.passed), ",",
                sqlStringValue(record.isFirstPassed), ",",
                sqlStringValue(record.examSource), ")");

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public boolean delete(final Cache cache, final MasteryAttemptRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE serial_nbr=",
                sqlIntegerValue(record.serialNbr), " AND exam_id=", sqlStringValue(record.examId));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Queries every record in the database.
     *
     * @param cache the data cache
     * @return the complete set of records in the database
     * @throws SQLException if there is an error performing the query
     */
    @Override
    public List<MasteryAttemptRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Queries for all mastery attempts for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryAttemptRec> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=",
                sqlStringValue(stuId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all mastery attempts for an exam.
     *
     * @param cache  the data cache
     * @param examId the exam ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryAttemptRec> queryByExam(final Cache cache, final String examId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE exam_id=",
                sqlStringValue(examId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all mastery attempts for a student on an exam.
     *
     * @param cache      the data cache
     * @param stuId      the student ID
     * @param examId     the exam ID
     * @param passedOnly true to only return records with "passed" set to"Y"
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryAttemptRec> queryByStudentExam(final Cache cache, final String stuId, final String examId,
                                                      final boolean passedOnly) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql;

        if (passedOnly) {
            sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId),
                    " AND exam_id=", sqlStringValue(examId), " AND passed='Y'");
        } else {
            sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId),
                    " AND exam_id=", sqlStringValue(examId));
        }

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single mastery attempt record.
     *
     * @param cache     the data cache
     * @param serialNbr the serial number of the attempt for which to query
     * @param examId    the exam ID of the attempt for which to query
     * @return the exam; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public MasteryAttemptRec query(final Cache cache, final Integer serialNbr,
                                   final String examId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE serial_nbr=",
                sqlIntegerValue(serialNbr), " AND exam_id=", sqlStringValue(examId));

        return doSingleQuery(cache, sql);
    }

    /**
     * Extracts a record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public MasteryAttemptRec fromResultSet(final ResultSet rs) throws SQLException {

        final MasteryAttemptRec result = new MasteryAttemptRec();

        result.serialNbr = getIntegerField(rs, FLD_SERIAL_NBR);
        result.examId = getStringField(rs, FLD_EXAM_ID);
        result.stuId = getStringField(rs, FLD_STU_ID);
        result.whenStarted = getDateTimeField(rs, FLD_WHEN_STARTED);
        result.whenFinished = getDateTimeField(rs, FLD_WHEN_FINISHED);
        result.examScore = getIntegerField(rs, FLD_EXAM_SCORE);
        result.masteryScore = getIntegerField(rs, FLD_MASTERY_SCORE);
        result.passed = getStringField(rs, FLD_PASSED);
        result.isFirstPassed = getStringField(rs, FLD_IS_FIRST_PASSED);
        result.examSource = getStringField(rs, FLD_EXAM_SOURCE);

        return result;
    }
}
