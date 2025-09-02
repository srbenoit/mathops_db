package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.schema.main.rec.TermRec;
import dev.mathops.db.schema.term.rec.TermWeekRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * A utility class to work with term week records.
 */
public final class TermWeekLogic implements ILegacyRecLogic<TermWeekRec> {

    /** A single instance. */
    public static final TermWeekLogic INSTANCE = new TermWeekLogic();

    /** The table name. */
    public static final String TABLE_NAME = "semester_calendar";

    /** A field name. */
    private static final String FLD_WEEK_NBR = "week_nbr";

    /** A field name. */
    private static final String FLD_START_DT = "start_dt";

    /** A field name. */
    private static final String FLD_END_DT = "end_dt";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private TermWeekLogic() {

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

        return schemaPrefix == null ? "semester_calendar" : (schemaPrefix + ".semester_calendar");
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
    public boolean insert(final Cache cache, final TermWeekRec record) throws SQLException {

        if (record.weekNbr == null || record.startDate == null || record.endDate == null) {
            throw new SQLException("Null value in required field.");
        }

        final SystemData systemData = cache.getSystemData();
        final TermRec activeTerm = systemData.getActiveTerm();
        if (activeTerm == null) {
            throw new SQLException("No active term found.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (term,term_yr,week_nbr,start_dt,end_dt) VALUES (",
                sqlStringValue(activeTerm.term.termCode), ",",
                sqlIntegerValue(activeTerm.term.shortYear), ",",
                sqlIntegerValue(record.weekNbr), ",",
                sqlDateValue(record.startDate), ",",
                sqlDateValue(record.endDate), ")");

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
    public boolean delete(final Cache cache, final TermWeekRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE week_nbr=",
                sqlIntegerValue(record.weekNbr));

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
    public List<TermWeekRec> queryAll(final Cache cache) throws SQLException {

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
    public TermWeekRec fromResultSet(final ResultSet rs) throws SQLException {

        final Integer weekNbr = getIntegerField(rs, FLD_WEEK_NBR);
        final LocalDate startDt = getDateField(rs, FLD_START_DT);
        final LocalDate endDt = getDateField(rs, FLD_END_DT);

        if (weekNbr == null || startDt == null || startDt == endDt) {
            throw new SQLException("TermWeek record found with null week number, start date, or end date");
        }

        return new TermWeekRec(weekNbr, startDt, endDt);
    }
}
