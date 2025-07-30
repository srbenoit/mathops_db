package dev.mathops.db.reclogic;

import dev.mathops.db.Cache;
import dev.mathops.db.ESchema;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.rec.PaceTrackRuleRec;
import dev.mathops.db.rec.TermRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with pace track rule records.
 */
public final class PaceTrackRuleLogic implements ILegacyRecLogic<PaceTrackRuleRec> {

    /** A single instance. */
    public static final PaceTrackRuleLogic INSTANCE = new PaceTrackRuleLogic();

    /** The table name. */
    public static final String TABLE_NAME = "pace_track_rule";

    /** A field name. */
    private static final String FLD_SUBTERM = "subterm";

    /** A field name. */
    private static final String FLD_PACE = "pace";

    /** A field name. */
    private static final String FLD_PACE_TRACK = "pace_track";

    /** A field name. */
    private static final String FLD_CRITERIA = "criteria";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private PaceTrackRuleLogic() {

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

        return schemaPrefix == null ? "pace_track_rule" : (schemaPrefix + ".pace_track_rule");
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
    public boolean insert(final Cache cache, final PaceTrackRuleRec record) throws SQLException {

        if (record.subterm == null || record.pace == null || record.paceTrack == null || record.criteria == null) {
            throw new SQLException("Null value in required field.");
        }

        final SystemData systemData = cache.getSystemData();
        final TermRec activeTerm = systemData.getActiveTerm();
        if (activeTerm == null) {
            throw new SQLException("No active term found.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (term,term_yr,subterm,pace,pace_track,criteria) VALUES (",
                sqlStringValue(activeTerm.term.termCode), ",",
                sqlIntegerValue(activeTerm.term.shortYear), ",",
                sqlStringValue(record.subterm), ",",
                sqlIntegerValue(record.pace), ",",
                sqlStringValue(record.paceTrack), ",",
                sqlStringValue(record.criteria), ")");

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
    public boolean delete(final Cache cache, final PaceTrackRuleRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                " WHERE subterm=", sqlStringValue(record.subterm),
                "  AND pace=", sqlIntegerValue(record.pace),
                "  AND pace_track=", sqlStringValue(record.paceTrack));

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
    public List<PaceTrackRuleRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Extracts a record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public PaceTrackRuleRec fromResultSet(final ResultSet rs) throws SQLException {

        final String subterm = getStringField(rs, FLD_SUBTERM);
        final Integer pace = getIntegerField(rs, FLD_PACE);
        final String paceTrack = getStringField(rs, FLD_PACE_TRACK);
        final String criteria = getStringField(rs, FLD_CRITERIA);

        if (subterm == null || pace == null || paceTrack == null || criteria == null) {
            throw new SQLException("PaceTrackRule record found with null subterm, pace, pace track, or criteria");
        }

        return new PaceTrackRuleRec(subterm, pace, paceTrack, criteria);
    }
}
