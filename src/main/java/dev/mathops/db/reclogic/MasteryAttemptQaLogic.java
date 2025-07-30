package dev.mathops.db.reclogic;

import dev.mathops.db.Cache;
import dev.mathops.db.ESchema;
import dev.mathops.db.rec.MasteryAttemptQaRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with mastery_attempt_qa records.
 */
public final class MasteryAttemptQaLogic implements ILegacyRecLogic<MasteryAttemptQaRec> {

    /** A single instance. */
    public static final MasteryAttemptQaLogic INSTANCE = new MasteryAttemptQaLogic();

    /** A field name for serialization of records. */
    private static final String FLD_SERIAL_NBR = "serial_nbr";

    /** A field name for serialization of records. */
    private static final String FLD_EXAM_ID = "exam_id";

    /** A field name for serialization of records. */
    private static final String FLD_QUESTION_NBR = "question_nbr";

    /** A field name for serialization of records. */
    private static final String FLD_CORRECT = "correct";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private MasteryAttemptQaLogic() {

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

        return schemaPrefix == null ? "mastery_attempt_qa" : (schemaPrefix + ".mastery_attempt_qa");
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
    public boolean insert(final Cache cache, final MasteryAttemptQaRec record) throws SQLException {

        if (record.serialNbr == null || record.examId == null || record.questionNbr == null
            || record.correct == null) {
            throw new SQLException("Null value in required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (serial_nbr,exam_id,question_nbr,correct) VALUES (",
                sqlIntegerValue(record.serialNbr), ",",
                sqlStringValue(record.examId), ",",
                sqlIntegerValue(record.questionNbr), ",",
                sqlStringValue(record.correct), ")");

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
    public boolean delete(final Cache cache, final MasteryAttemptQaRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE serial_nbr=",
                sqlIntegerValue(record.serialNbr), " AND exam_id=",
                sqlStringValue(record.examId), " AND question_nbr=",
                sqlIntegerValue(record.questionNbr));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the "correct" field of a mastery attempt question answer.
     *
     * @param cache      the data cache
     * @param record     the record to update
     * @param newCorrect the new 'correct' field value
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the query
     */
    public boolean updateCorrect(final Cache cache, final MasteryAttemptQaRec record,
                                 final String newCorrect) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName, " SET correct=",
                sqlStringValue(newCorrect), " WHERE serial_nbr=", sqlIntegerValue(record.serialNbr),
                " AND exam_id=", sqlStringValue(record.examId),
                " AND question_nbr=", sqlIntegerValue(record.questionNbr));

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
    public List<MasteryAttemptQaRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Queries for all question answers for a mastery attempt.
     *
     * @param cache     the data cache
     * @param serialNbr the student ID
     * @param examId    the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryAttemptQaRec> queryByAttempt(final Cache cache, final Integer serialNbr,
                                                    final String examId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE serial_nbr=",
                sqlIntegerValue(serialNbr), " AND exam_id=", sqlStringValue(examId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single mastery attempt record.
     *
     * @param cache       the data cache
     * @param serialNbr   the serial number of the attempt for which to query
     * @param examId      the exam ID of the attempt for which to query
     * @param questionNbr the question number of the question answer for which to query
     * @return the exam; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public MasteryAttemptQaRec query(final Cache cache, final Integer serialNbr,
                                     final String examId, final Integer questionNbr) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE serial_nbr=",
                sqlIntegerValue(serialNbr), " AND exam_id=", sqlStringValue(examId),
                " AND question_nbr=", sqlIntegerValue(questionNbr));

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
    public MasteryAttemptQaRec fromResultSet(final ResultSet rs) throws SQLException {

        final MasteryAttemptQaRec result = new MasteryAttemptQaRec();

        result.serialNbr = getIntegerField(rs, FLD_SERIAL_NBR);
        result.examId = getStringField(rs, FLD_EXAM_ID);
        result.questionNbr = getIntegerField(rs, FLD_QUESTION_NBR);
        result.correct = getStringField(rs, FLD_CORRECT);

        return result;
    }
}
