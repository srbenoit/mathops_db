package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.main.rec.AssignmentRec;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * A utility class to work with assignment records.
 */
public final class AssignmentLogic implements ILegacyRecLogic<AssignmentRec> {

    /** A single instance. */
    public static final AssignmentLogic INSTANCE = new AssignmentLogic();

    /** A field name. */
    private static final String FLD_VERSION = "version";

    /** A field name. */
    private static final String FLD_HW_TYPE = "hw_type";

    /** A field name. */
    private static final String FLD_COURSE = "course";

    /** A field name. */
    private static final String FLD_UNIT = "unit";

    /** A field name. */
    private static final String FLD_OBJECTIVE = "objective";

    /** A field name. */
    private static final String FLD_TREE_REF = "tree_ref";

    /** A field name. */
    private static final String FLD_TITLE = "title";

    /** A field name. */
    private static final String FLD_ACTIVE_DT = "active_dt";

    /** A field name. */
    private static final String FLD_PULL_DT = "pull_dt";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private AssignmentLogic() {

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

        return schemaPrefix == null ? "homework" : (schemaPrefix + ".homework");
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
    public boolean insert(final Cache cache, final AssignmentRec record) throws SQLException {

        if (record.assignmentId == null || record.assignmentType == null
            || record.courseId == null || record.unit == null || record.objective == null) {
            throw new SQLException("Null value in required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (version,course,unit,objective,title,tree_ref,hw_type,active_dt,pull_dt) VALUES (",
                sqlStringValue(record.assignmentId), ",",
                sqlStringValue(record.courseId), ",",
                sqlIntegerValue(record.unit), ",",
                sqlIntegerValue(record.objective), ",",
                sqlStringValue(record.title), ",",
                sqlStringValue(record.treeRef), ",",
                sqlStringValue(record.assignmentType), ",",
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
    public boolean delete(final Cache cache, final AssignmentRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE version=",
                sqlStringValue(record.assignmentId));

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
    public List<AssignmentRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Queries for all active (having null pull date) assignments in a course. Results are ordered by unit and then by
     * objective.
     *
     * @param cache          the data cache
     * @param courseId       the course ID for which to query
     * @param assignmentType null to retrieve all assignment types; or the type to retrieve
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<AssignmentRec> queryActiveByCourse(final Cache cache, final String courseId,
                                                   final String assignmentType) throws SQLException {

        final String tableName = getTableName(cache);

        final HtmlBuilder sql = new HtmlBuilder(100);

        sql.add("SELECT * FROM ", tableName, " WHERE course=", sqlStringValue(courseId), " AND pull_dt IS NULL");

        if (assignmentType != null) {
            sql.add(" AND hw_type=", sqlStringValue(assignmentType));
        }

        sql.add(" ORDER BY unit,objective");

        return doListQuery(cache, sql.toString());
    }

    /**
     * Queries for a homework by its version.
     *
     * @param cache        the data cache
     * @param assignmentId the version of the homework to query
     * @return the homework; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public AssignmentRec query(final Cache cache, final String assignmentId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE version=",
                sqlStringValue(assignmentId));

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
    public AssignmentRec fromResultSet(final ResultSet rs) throws SQLException {

        final String id = getStringField(rs, FLD_VERSION);
        final String type = getStringField(rs, FLD_HW_TYPE);
        final String course = getStringField(rs, FLD_COURSE);
        final Integer unit = getIntegerField(rs, FLD_UNIT);
        final Integer objective = getIntegerField(rs, FLD_OBJECTIVE);
        final String treeRef = getStringField(rs, FLD_TREE_REF);
        final String title = getStringField(rs, FLD_TITLE);

        final LocalDate active = getDateField(rs, FLD_ACTIVE_DT);
        final LocalDateTime whenActive = active == null ? null : LocalDateTime.of(active, LocalTime.of(0, 0));

        final LocalDate pulled = getDateField(rs, FLD_PULL_DT);
        final LocalDateTime whenPulled = pulled == null ? null : LocalDateTime.of(pulled, LocalTime.of(0, 0));

        return new AssignmentRec(id, type, course, unit, objective, treeRef, title, whenActive, whenPulled);
    }
}
