package dev.mathops.db.schema.main.impl;

import dev.mathops.db.Cache;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.schema.system.rec.ReportPermsRec;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A utility class to work with report_perms records.
 */
public final class ReportPermsLogic implements ILegacyRecLogic<ReportPermsRec> {

    /** A single instance. */
    public static final ReportPermsLogic INSTANCE = new ReportPermsLogic();

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_RPT_ID = "rpt_id";

    /** A field name. */
    private static final String FLD_PERM_LEVEL = "perm_level";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private ReportPermsLogic() {

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

        return schemaPrefix == null ? "report_perms" : (schemaPrefix + ".report_perms");
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
    public boolean insert(final Cache cache, final ReportPermsRec record) throws SQLException {

        if (record.stuId == null || record.rptId == null || record.permLevel == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("INSERT INTO ", tableName, " (stu_id,rpt_id,perm_level) VALUES (",
                sqlStringValue(record.stuId), ",",
                sqlStringValue(record.rptId), ",",
                sqlIntegerValue(record.permLevel), ")");

        return doUpdateOneRow(cache, sql);
    }

    /**
     * Updates the permission level in a record.
     *
     * @param cache  the data cache
     * @param record the record to update
     * @return {@code true} if successful; {@code false} otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public boolean updatePermLevel(final Cache cache, final ReportPermsRec record) throws SQLException {

        if (record.stuId == null || record.rptId == null || record.permLevel == null) {
            throw new SQLException("Null value in primary key or required field.");
        }

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("UPDATE ", tableName, " set perm_level=",
                sqlIntegerValue(record.permLevel),
                " WHERE stu_id=", sqlStringValue(record.stuId),
                " AND rpt_id=", sqlStringValue(record.rptId));

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
    public boolean delete(final Cache cache, final ReportPermsRec record) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("DELETE FROM ", tableName, " WHERE stu_id=",
                sqlStringValue(record.stuId), " AND rpt_id=", sqlStringValue(record.rptId));

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
    public List<ReportPermsRec> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        return doListQuery(cache, "SELECT * FROM " + tableName);
    }

    /**
     * Queries for all report permissions for a single student.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<ReportPermsRec> queryByStuId(final Cache cache, final String stuId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for all report permissions for a single report.
     *
     * @param cache the data cache
     * @param rptId the report ID
     * @return the list of records returned
     * @throws SQLException if there is an error performing the query
     */
    public List<ReportPermsRec> queryByRptId(final Cache cache, final String rptId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM  ", tableName, " WHERE rpt_id=", sqlStringValue(rptId));

        return doListQuery(cache, sql);
    }

    /**
     * Queries for a single report permission record.
     *
     * @param cache the data cache
     * @param stuId the student ID
     * @param rptId the report ID
     * @return the record; {@code null} if not found
     * @throws SQLException if there is an error performing the query
     */
    public ReportPermsRec query(final Cache cache, final String stuId, final String rptId) throws SQLException {

        final String tableName = getTableName(cache);

        final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE stu_id=", sqlStringValue(stuId),
                " AND rpt_id=", sqlStringValue(rptId));

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
    public ReportPermsRec fromResultSet(final ResultSet rs) throws SQLException {

        final ReportPermsRec result = new ReportPermsRec();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.rptId = getStringField(rs, FLD_RPT_ID);
        result.permLevel = getIntegerField(rs, FLD_PERM_LEVEL);

        return result;
    }
}
