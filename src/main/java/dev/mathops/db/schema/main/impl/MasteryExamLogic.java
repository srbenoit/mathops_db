package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.main.rec.MasteryExamRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * A utility class to work with mastery_exam records.
 */
public final class MasteryExamLogic implements ILegacyRecLogic<MasteryExamRec> {

    /** A single instance. */
    public static final MasteryExamLogic INSTANCE = new MasteryExamLogic();

    /** A field name. */
    private static final String FLD_EXAM_ID = "exam_id";

    /** A field name. */
    private static final String FLD_EXAM_TYPE = "exam_type";

    /** A field name. */
    private static final String FLD_COURSE_ID = "course_id";

    /** A field name. */
    private static final String FLD_UNIT = "unit";

    /** A field name. */
    private static final String FLD_OBJECTIVE = "objective";

    /** A field name. */
    private static final String FLD_TREE_REF = "tree_ref";

    /** A field name. */
    private static final String FLD_TITLE = "title";

    /** A field name. */
    private static final String FLD_BUTTON_LABEL = "button_label";

    /** A field name. */
    private static final String FLD_WHEN_ACTIVE = "when_active";

    /** A field name. */
    private static final String FLD_WHEN_PULLED = "when_pulled";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private MasteryExamLogic() {

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

        return schemaPrefix == null ? "mastery_exam" : (schemaPrefix + ".mastery_exam");
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
    public boolean insert(final Cache cache, final MasteryExamRec record) throws SQLException {

        if (record.examId == null || record.examType == null || record.courseId == null
            || record.unit == null || record.objective == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (exam_id,exam_type,course_id,unit,objective,tree_ref,title,button_label,",
                "when_active,when_pulled) VALUES (",
                sqlStringValue(record.examId), ",",
                sqlStringValue(record.examType), ",",
                sqlStringValue(record.courseId), ",",
                sqlIntegerValue(record.unit), ",",
                sqlIntegerValue(record.objective), ",",
                sqlStringValue(record.treeRef), ",",
                sqlStringValue(record.title), ",",
                sqlStringValue(record.buttonLabel), ",",
                sqlDateValue(record.whenActive == null ? null : record.whenActive.toLocalDate()), ",",
                sqlDateValue(record.whenPulled == null ? null : record.whenPulled.toLocalDate()), ")");

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
    public boolean delete(final Cache cache, final MasteryExamRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE exam_id=",
                sqlStringValue(record.examId));

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
    public List<MasteryExamRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Queries for all active (having null pull date) mastery exams in a course. Results are ordered by unit and then by
     * objective.
     *
     * @param cache    the data cache
     * @param courseId the course ID for which to query
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryExamRec> queryActiveByCourse(final Cache cache, final String courseId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE course_id=",
                sqlStringValue(courseId), " AND when_pulled IS NULL ORDER BY unit,objective");

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all active (having null pull date) mastery exams in a course unit. Results are ordered by objective.
     *
     * @param cache    the data cache
     * @param courseId the course for which to query
     * @param unit     the unit for which to query
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryExamRec> queryActiveByCourseUnit(final Cache cache, final String courseId,
                                                        final Integer unit) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE course_id=",
                sqlStringValue(courseId), " AND unit=", sqlIntegerValue(unit),
                " AND when_pulled IS NULL ORDER BY objective");

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all active (having null pull date) mastery exams in a course unit objective.
     *
     * @param cache     the data cache
     * @param courseId  the course for which to query
     * @param unit      the unit for which to query
     * @param objective the objective for which to query
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<MasteryExamRec> queryActiveByCourseUnitObjective(final Cache cache, final String courseId,
                                                                 final Integer unit, final Integer objective)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE course_id=",
                sqlStringValue(courseId), " AND unit=", sqlIntegerValue(unit),
                " AND objective=", sqlIntegerValue(objective), " AND when_pulled IS NULL");

        return doListQuery(cache, sql);
    }

    /**
     * Queries for an active (having null pull date) mastery exam by its type, course, unit, and objective (which should
     * produce a unique result or nothing).
     *
     * @param cache     the data cache
     * @param courseId  the course for which to query
     * @param unit      the unit for which to query
     * @param objective the objective for which to query
     * @param examType  the assignment type for which to query
     * @return the assignment; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public MasteryExamRec queryActive(final Cache cache, final String courseId, final Integer unit,
                                      final Integer objective, final String examType) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE course_id=",
                sqlStringValue(courseId), " AND unit=", sqlIntegerValue(unit),
                " AND objective=", sqlIntegerValue(objective),
                " AND exam_type=", sqlStringValue(examType), " AND when_pulled IS NULL");

        return doSingleQuery(cache, sql);
    }

    /**
     * Queries for a mastery exam by its exam ID.
     *
     * @param cache  the data cache
     * @param examId the version of the exam to query
     * @return the exam; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public MasteryExamRec query(final Cache cache, final String examId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE exam_id=",
                sqlStringValue(examId));

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
    public MasteryExamRec fromResultSet(final ResultSet rs) throws SQLException {

        final MasteryExamRec result = new MasteryExamRec();

        result.examId = getStringField(rs, FLD_EXAM_ID);
        result.examType = getStringField(rs, FLD_EXAM_TYPE);
        result.courseId = getStringField(rs, FLD_COURSE_ID);
        result.unit = getIntegerField(rs, FLD_UNIT);
        result.objective = getIntegerField(rs, FLD_OBJECTIVE);
        result.treeRef = getStringField(rs, FLD_TREE_REF);
        result.title = getStringField(rs, FLD_TITLE);
        result.buttonLabel = getStringField(rs, FLD_BUTTON_LABEL);

        final LocalDate active = getDateField(rs, FLD_WHEN_ACTIVE);
        result.whenActive = active == null ? null : LocalDateTime.of(active, LocalTime.of(0, 0));

        final LocalDate pulled = getDateField(rs, FLD_WHEN_PULLED);
        result.whenPulled = pulled == null ? null : LocalDateTime.of(pulled, LocalTime.of(0, 0));

        return result;
    }
}
