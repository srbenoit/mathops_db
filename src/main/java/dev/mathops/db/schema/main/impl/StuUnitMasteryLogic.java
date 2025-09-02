package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.term.rec.StuUnitMasteryRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with stu_unit_mastery records.
 */
public final class StuUnitMasteryLogic implements ILegacyRecLogic<StuUnitMasteryRec> {

    /** A single instance. */
    public static final StuUnitMasteryLogic INSTANCE = new StuUnitMasteryLogic();

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_COURSE_ID = "course_id";

    /** A field name. */
    private static final String FLD_UNIT = "unit";

    /** A field name. */
    private static final String FLD_SCORE = "score";

    /** A field name. */
    private static final String FLD_SR_STATUS = "sr_status";

    /** A field name. */
    private static final String FLD_S1_STATUS = "s1_status";

    /** A field name. */
    private static final String FLD_S2_STATUS = "s2_status";

    /** A field name. */
    private static final String FLD_S3_STATUS = "s3_status";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private StuUnitMasteryLogic() {

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

        return schemaPrefix == null ? "stu_unit_mastery" : (schemaPrefix + ".stu_unit_mastery");
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
    public boolean insert(final Cache cache, final StuUnitMasteryRec record) throws SQLException {

        if (record.stuId == null || record.courseId == null || record.unit == null || record.score == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,course_id,unit,score,sr_status,s1_status,s2_status,s3_status) VALUES (",
                sqlStringValue(record.stuId), ",",
                sqlStringValue(record.courseId), ",",
                sqlIntegerValue(record.unit), ",",
                sqlIntegerValue(record.score), ",",
                sqlStringValue(record.srStatus), ",",
                sqlStringValue(record.s1Status), ",",
                sqlStringValue(record.s2Status), ",",
                sqlStringValue(record.s3Status), ")");

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
    public boolean delete(final Cache cache, final StuUnitMasteryRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "DELETE FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

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
    public List<StuUnitMasteryRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Updates the student's current score in the unit.
     *
     * @param cache    the data cache
     * @param record   the record to be updated
     * @param newScore the new score
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateScore(final Cache cache, final StuUnitMasteryRec record,
                               final Integer newScore) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName,
                " SET score=", sqlIntegerValue(newScore),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the student's current status in the Skills Review in the unit.
     *
     * @param cache       the data cache
     * @param record      the record to be updated
     * @param newSrStatus the new status
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateSrStatus(final Cache cache, final StuUnitMasteryRec record,
                                  final String newSrStatus) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName,
                " SET sr_status=", sqlStringValue(newSrStatus),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the student's current status in Standard 1 in the unit.
     *
     * @param cache       the data cache
     * @param record      the record to be updated
     * @param newS1Status the new status
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateS1Status(final Cache cache, final StuUnitMasteryRec record,
                                  final String newS1Status) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName,
                " SET s1_status=", sqlStringValue(newS1Status),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the student's current status in Standard 2 in the unit.
     *
     * @param cache       the data cache
     * @param record      the record to be updated
     * @param newS2Status the new status
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateS2Status(final Cache cache, final StuUnitMasteryRec record,
                                  final String newS2Status) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName,
                " SET s2_status=", sqlStringValue(newS2Status),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the student's current status in Standard 3 in the unit.
     *
     * @param cache       the data cache
     * @param record      the record to be updated
     * @param newS3Status the new status
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateS3Status(final Cache cache, final StuUnitMasteryRec record,
                                  final String newS3Status) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "UPDATE ", tableName,
                " SET s3_status=", sqlStringValue(newS3Status),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId),
                " AND unit=", sqlIntegerValue(record.unit));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Queries for all unit mastery status records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuUnitMasteryRec> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all unit mastery status records for a student in a course.
     *
     * @param cache    the data cache
     * @param stuId    the student ID
     * @param courseId the course ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuUnitMasteryRec> queryByStudentCourse(final Cache cache, final String stuId,
                                                        final String courseId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId),
                " AND course_id=", sqlStringValue(courseId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single unit mastery record by student and unit.
     *
     * @param cache    the data cache
     * @param stuId    the student ID
     * @param courseId the course ID
     * @param unit     the unit
     * @return the record; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public StuUnitMasteryRec query(final Cache cache, final String stuId,
                                   final String courseId, final Integer unit) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId),
                " AND course_id=", sqlStringValue(courseId),
                " AND unit=", sqlIntegerValue(unit));

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
    public StuUnitMasteryRec fromResultSet(final ResultSet rs) throws SQLException {

        final StuUnitMasteryRec result = new StuUnitMasteryRec();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.courseId = getStringField(rs, FLD_COURSE_ID);
        result.unit = getIntegerField(rs, FLD_UNIT);
        result.score = getIntegerField(rs, FLD_SCORE);
        result.srStatus = getStringField(rs, FLD_SR_STATUS);
        result.s1Status = getStringField(rs, FLD_S1_STATUS);
        result.s2Status = getStringField(rs, FLD_S2_STATUS);
        result.s3Status = getStringField(rs, FLD_S3_STATUS);

        return result;
    }
}
