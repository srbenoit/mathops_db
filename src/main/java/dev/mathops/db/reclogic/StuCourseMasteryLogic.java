package dev.mathops.db.reclogic;

import dev.mathops.db.Cache;
import dev.mathops.db.ESchema;
import dev.mathops.db.rec.StuCourseMasteryRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with stu_course_mastery records.
 */
public final class StuCourseMasteryLogic implements ILegacyRecLogic<StuCourseMasteryRec> {

    /** A single instance. */
    public static final StuCourseMasteryLogic INSTANCE = new StuCourseMasteryLogic();

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_COURSE_ID = "course_id";

    /** A field name. */
    private static final String FLD_SCORE = "score";

    /** A field name. */
    private static final String FLD_NBR_MASTERED_H1 = "nbr_mastered_h1";

    /** A field name. */
    private static final String FLD_NBR_MASTERED_H2 = "nbr_mastered_h2";

    /** A field name. */
    private static final String FLD_NBR_ELIGIBLE = "nbr_eligible";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private StuCourseMasteryLogic() {

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

        return schemaPrefix == null ? "stu_course_mastery" : (schemaPrefix + ".stu_course_mastery");
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
    public boolean insert(final Cache cache, final StuCourseMasteryRec record) throws SQLException {

        if (record.stuId == null || record.courseId == null || record.score == null
            || record.nbrMasteredH1 == null || record.nbrMasteredH2 == null
            || record.nbrEligible == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,course_id,score,nbr_mastered_h1,nbr_mastered_h2,nbr_eligible) VALUES (",
                sqlStringValue(record.stuId), ",",
                sqlStringValue(record.courseId), ",",
                sqlIntegerValue(record.score), ",",
                sqlIntegerValue(record.nbrMasteredH1), ",",
                sqlIntegerValue(record.nbrMasteredH2), ",",
                sqlIntegerValue(record.nbrEligible), ")");

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
    public boolean delete(final Cache cache, final StuCourseMasteryRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId));

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
    public List<StuCourseMasteryRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Updates fields related to a student's current mastery relative to a course.
     *
     * @param cache            the data cache
     * @param record           the record to be updated
     * @param newNbrMasteredH1 the new number of standards mastered in the first half of the course
     * @param newNbrMasteredH2 the new number of standards mastered in the second half of the course
     * @param newNbrEligible   the new number of standards for which the student is eligible but has not demonstrated
     *                         mastery
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateMastery(final Cache cache, final StuCourseMasteryRec record, final Integer newNbrMasteredH1,
                                 final Integer newNbrMasteredH2, final Integer newNbrEligible) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET nbr_mastered_h1=", sqlIntegerValue(newNbrMasteredH1),
                ", nbr_mastered_h2=", sqlIntegerValue(newNbrMasteredH2),
                ", nbr_eligible=", sqlIntegerValue(newNbrEligible),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates a student's score in a course.
     *
     * @param cache    the data cache
     * @param record   the record to be updated
     * @param newScore the new score
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateScore(final Cache cache, final StuCourseMasteryRec record, final Integer newScore)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName, " SET score=", sqlIntegerValue(newScore),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND course_id=", sqlStringValue(record.courseId));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Queries for all course mastery records for a student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuCourseMasteryRec> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single course mastery record by student and course.
     *
     * @param cache    the data cache
     * @param stuId    the student ID
     * @param courseId the course ID
     * @return the assignment; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public StuCourseMasteryRec query(final Cache cache, final String stuId, final String courseId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId),
                " AND course_id=", sqlStringValue(courseId));

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
    public StuCourseMasteryRec fromResultSet(final ResultSet rs) throws SQLException {

        final StuCourseMasteryRec result = new StuCourseMasteryRec();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.courseId = getStringField(rs, FLD_COURSE_ID);
        result.score = getIntegerField(rs, FLD_SCORE);
        result.nbrMasteredH1 = getIntegerField(rs, FLD_NBR_MASTERED_H1);
        result.nbrMasteredH2 = getIntegerField(rs, FLD_NBR_MASTERED_H2);
        result.nbrEligible = getIntegerField(rs, FLD_NBR_ELIGIBLE);

        return result;
    }
}
