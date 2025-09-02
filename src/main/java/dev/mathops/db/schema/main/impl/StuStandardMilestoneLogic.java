package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.term.rec.StuStandardMilestoneRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * A utility class to work with stu_std_milestone records.
 */
public final class StuStandardMilestoneLogic implements ILegacyRecLogic<StuStandardMilestoneRec> {

    /** A single instance. */
    public static final StuStandardMilestoneLogic INSTANCE = new StuStandardMilestoneLogic();

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_PACE_TRACK = "pace_track";

    /** A field name. */
    private static final String FLD_PACE = "pace";

    /** A field name. */
    private static final String FLD_PACE_INDEX = "pace_index";

    /** A field name. */
    private static final String FLD_UNIT = "unit";

    /** A field name. */
    private static final String FLD_OBJECTIVE = "objective";

    /** A field name. */
    private static final String FLD_MS_TYPE = "ms_type";

    /** A field name. */
    private static final String FLD_MS_DATE = "ms_date";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private StuStandardMilestoneLogic() {

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

        return schemaPrefix == null ? "stu_std_milestone" : (schemaPrefix + ".stu_std_milestone");
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
    public boolean insert(final Cache cache, final StuStandardMilestoneRec record) throws SQLException {

        if (record.stuId == null || record.paceTrack == null || record.pace == null
            || record.paceIndex == null || record.unit == null || record.objective == null
            || record.msType == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName,
                " (stu_id,pace_track,pace,pace_index,unit,objective,ms_type,ms_date) VALUES (",
                sqlStringValue(record.stuId), ",",
                sqlStringValue(record.paceTrack), ",",
                sqlIntegerValue(record.pace), ",",
                sqlIntegerValue(record.paceIndex), ",",
                sqlIntegerValue(record.unit), ",",
                sqlIntegerValue(record.objective), ",",
                sqlStringValue(record.msType), ",",
                sqlDateValue(record.msDate), ")");

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
    public boolean delete(final Cache cache, final StuStandardMilestoneRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND pace_track=", sqlStringValue(record.paceTrack),
                " AND pace=", sqlIntegerValue(record.pace),
                " AND pace_index=", sqlIntegerValue(record.paceIndex),
                " AND unit=", sqlIntegerValue(record.unit),
                " AND objective=", sqlIntegerValue(record.objective),
                " AND ms_type=", sqlStringValue(record.msType));

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
    public List<StuStandardMilestoneRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Updates the milestone date on a student standard milestone record.
     *
     * @param cache   the data cache
     * @param record  the record to be updated
     * @param newDate the new date
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public boolean updateDate(final Cache cache, final StuStandardMilestoneRec record,
                              final LocalDate newDate) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                " SET ms_date=", sqlDateValue(newDate),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND pace_track=", sqlStringValue(record.paceTrack),
                " AND pace=", sqlIntegerValue(record.pace),
                " AND pace_index=", sqlIntegerValue(record.paceIndex),
                " AND unit=", sqlIntegerValue(record.unit),
                " AND objective=", sqlIntegerValue(record.objective),
                " AND ms_type=", sqlStringValue(record.msType));

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Queries for all student standard milestones for a specified student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuStandardMilestoneRec> queryByStudent(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all standard milestones for a specified pace track and pace. This will include milestones for all
     * pace indexes in that pace.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @param track the pace track
     * @param pace  the pace
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuStandardMilestoneRec> queryByStuPaceTrackPace(final Cache cache, final String stuId,
                                                                 final String track, final Integer pace)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId),
                " AND pace_track=", sqlStringValue(track),
                " AND pace=", sqlIntegerValue(pace));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all standard milestones for a specified pace track, pace, and pace index.
     *
     * @param cache     the data cache
     * @param stuId     the student ID
     * @param track     the pace track
     * @param pace      the pace
     * @param paceIndex the pace index
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<StuStandardMilestoneRec> queryByStuPaceTrackPaceIndex(final Cache cache, final String stuId,
                                                                      final String track, final Integer pace,
                                                                      final Integer paceIndex)
            throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId),
                " AND pace_track=", sqlStringValue(track),
                " AND pace=", sqlIntegerValue(pace),
                " AND pace_index=", sqlIntegerValue(paceIndex));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single standard milestone pace track, pace, pace index, unit, objective, and milestone type.
     *
     * @param cache     the data cache
     * @param stuId     the student ID
     * @param track     the pace track
     * @param pace      the pace
     * @param paceIndex the pace index
     * @param unit      the unit
     * @param objective the objective
     * @param msType    the milestone type
     * @return the record; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public StuStandardMilestoneRec query(final Cache cache, final String stuId, final String track,
                                         final Integer pace, final Integer paceIndex, final Integer unit,
                                         final Integer objective, final String msType) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE stu_id=", sqlStringValue(stuId),
                " AND pace_track=", sqlStringValue(track),
                " AND pace=", sqlIntegerValue(pace),
                " AND pace_index=", sqlIntegerValue(paceIndex),
                " AND unit=", sqlIntegerValue(unit),
                " AND objective=", sqlIntegerValue(objective),
                " AND ms_type=", sqlStringValue(msType));

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
    public StuStandardMilestoneRec fromResultSet(final ResultSet rs) throws SQLException {

        final StuStandardMilestoneRec result = new StuStandardMilestoneRec();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.paceTrack = getStringField(rs, FLD_PACE_TRACK);
        result.pace = getIntegerField(rs, FLD_PACE);
        result.paceIndex = getIntegerField(rs, FLD_PACE_INDEX);
        result.unit = getIntegerField(rs, FLD_UNIT);
        result.objective = getIntegerField(rs, FLD_OBJECTIVE);
        result.msType = getStringField(rs, FLD_MS_TYPE);
        result.msDate = getDateField(rs, FLD_MS_DATE);

        return result;
    }
}
