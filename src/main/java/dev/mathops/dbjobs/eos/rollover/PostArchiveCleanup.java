package dev.mathops.dbjobs.eos.rollover;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Performs some cleanup of data after archiving.  This is based on commands in the old "eos_arc" and "eos_load"
 * Informix batches.
 */
public final class PostArchiveCleanup implements Runnable {

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.DEBUG;
    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code PostArchiveCleanup}.
     *
     * @param theCache the data cache
     */
    private PostArchiveCleanup(final Cache theCache) {

        this.cache = theCache;
    }

    /**
     * Runs the process.
     */
    public void run() {

        final DbConnection prodConn = this.cache.checkOutConnection(ESchema.LEGACY);
        try {
//            copyActiveTermToNextTerm(prodConn, "cunit");
//            deleteOldRecords(prodConn, "cunit", 2);
//            copyActiveTermToNextTerm(prodConn, "cuobjective");
//            deleteOldRecords(prodConn, "cuobjective", 2);
//            copyActiveTermToNextTerm(prodConn, "cusection");
//            cleanCampusCalendar(prodConn);
//            copyTable(prodConn, "milestone_appeal", "prev_milestone_appeal");

//        } catch (final SQLException ex) {
//            Log.warning("Exception performing post-archive cleanup.", ex);
        } finally {
            Cache.checkInConnection(prodConn);
        }
    }

    /**
     * Deletes CAMPUS_CALENDAR records.
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private void cleanCampusCalendar(final DbConnection prodConn) throws SQLException {

        Log.info("> Deleting all 'campus_calendar' records.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM campus_calendar");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("  Found " + count + " 'campus_calendar' records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM campus_calendar");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("  Deleted " + count + " 'campus_calendar' records.");
            }
        }

    }

    /**
     * Copies all active-term records in a specified table to next-term records.
     *
     * @param prodConn  the connection to the production database
     * @param tableName the table name
     * @throws SQLException if there is an error accessing the database
     */
    private void copyActiveTermToNextTerm(final DbConnection prodConn, final String tableName) throws SQLException {

        final SystemData systemData = this.cache.getSystemData();
        final TermRec nextTerm = systemData.getNextTerm();

        Log.info("> Copying active-term '", tableName, "' records to next-term records.");

        if (nextTerm == null) {
            Log.warning("  Failed to query 'next' term.");
        } else {
            final TermKey nextKey = nextTerm.term;

            try (final Statement statement = prodConn.createStatement()) {

                if (DEBUG_MODE == EDebugMode.DEBUG) {
                    final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", tableName,
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");
                    try (final ResultSet rs = statement.executeQuery(sql)) {
                        rs.next();
                        final int count = rs.getInt(1);
                        final String countStr = Integer.toString(count);
                        Log.info("  Found ", countStr, " '", tableName, "' records to copy.");
                    }
                } else {
                    final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");

                    try (final ResultSet rs = statement.executeQuery(sql);) {
                        final ResultSetMetaData meta = rs.getMetaData();
                        final int cols = meta.getColumnCount();
                        final int[] types = new int[cols];

                        final HtmlBuilder builder = new HtmlBuilder(200);
                        builder.add("INSERT INTO ", tableName, " (");
                        for (int i = 1; i <= cols; ++i) {
                            final String name = meta.getColumnName(i);
                            types[i - 1] = meta.getColumnType(i);
                            if (i > 1) {
                                builder.add(',');
                            }
                            builder.add(name);
                        }
                        builder.add(") VALUES (?");
                        for (int i = 2; i <= cols; ++i) {
                            builder.add(",?");
                        }
                        builder.add(")");

                        final String insert = builder.toString();
                        try (final PreparedStatement prepared = prodConn.prepareStatement(insert)) {
                            int numRows = 0;

                            while (rs.next()) {
                                for (int i = 1; i <= cols; ++i) {
                                    final String name = meta.getColumnName(i);
                                    if ("term".equalsIgnoreCase(name)) {
                                        prepared.setString(i, nextKey.termCode);
                                    } else if ("term_yr".equalsIgnoreCase(name)) {
                                        final int shortYear = nextKey.shortYear.intValue();
                                        prepared.setInt(i, shortYear);
                                    } else {
                                        final Object fieldValue = rs.getObject(i);
                                        if (fieldValue == null) {
                                            prepared.setNull(i, types[i - 1]);
                                        } else {
                                            prepared.setObject(i, fieldValue, types[i - 1]);
                                        }
                                    }
                                }

                                ++numRows;
                                if (DEBUG_MODE == EDebugMode.NORMAL) {
                                    final int count = prepared.executeUpdate();
                                    if (count != 1) {
                                        Log.warning("  FAILED to insert record into '", tableName, "' table");
                                    }
                                }
                            }

                            prodConn.commit();
                            Log.info("  Copied " + numRows + " records within '", tableName, "' table.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Deletes records in a specified table that are some number of years old (or older).
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteOldRecords(final DbConnection prodConn, final String tableName, final int yearsAgo)
            throws SQLException {

        final String yearsStr = Integer.toString(yearsAgo);
        Log.info("> Deleting '", tableName, "' records that are more than ", yearsStr, " years old.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", tableName,
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("  Found " + count + " old '", tableName, "' records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("  Deleted " + count + " old '", tableName, "' records.");
            }
        }
    }

    /**
     * Copies all rows from one table to another.
     *
     * @param prodConn        the connection to the production database
     * @param sourceTableName the source table name
     * @param targetTableName the target table name
     * @throws SQLException if there is an error accessing the database
     */
    private static void copyTable(final DbConnection prodConn, final String sourceTableName,
                                  final String targetTableName) throws SQLException {

        Log.info("> Copying all records from '", sourceTableName, "' to '", targetTableName, "'.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", sourceTableName);
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    final String countStr = Integer.toString(count);
                    Log.info("  Found ", countStr, " '", sourceTableName, "' records to copy.");
                }
            } else {
                final String sql = SimpleBuilder.concat("SELECT * FROM ", sourceTableName);

                try (final ResultSet rs = statement.executeQuery(sql)) {
                    final ResultSetMetaData meta = rs.getMetaData();
                    final int cols = meta.getColumnCount();
                    final int[] types = new int[cols];

                    final HtmlBuilder builder = new HtmlBuilder(200);
                    builder.add("INSERT INTO ", targetTableName, " (");
                    for (int i = 1; i <= cols; ++i) {
                        final String name = meta.getColumnName(i);
                        types[i - 1] = meta.getColumnType(i);
                        if (i > 1) {
                            builder.add(',');
                        }
                        builder.add(name);
                    }
                    builder.add(") VALUES (?");
                    for (int i = 2; i <= cols; ++i) {
                        builder.add(",?");
                    }
                    builder.add(")");

                    final String insert = builder.toString();
                    try (final PreparedStatement prepared = prodConn.prepareStatement(insert)) {
                        int numRows = 0;

                        while (rs.next()) {
                            for (int i = 1; i <= cols; ++i) {
                                final Object fieldValue = rs.getObject(i);
                                if (fieldValue == null) {
                                    prepared.setNull(i, types[i - 1]);
                                } else {
                                    prepared.setObject(i, fieldValue, types[i - 1]);
                                }
                            }

                            final int count = prepared.executeUpdate();
                            if (count == 1) {
                                ++numRows;
                            } else {
                                Log.warning("  FAILED to insert record into '", targetTableName, "' table");
                            }
                        }

                        prodConn.commit();
                        Log.info("  Copied " + numRows + " records from '", sourceTableName, "' to '", targetTableName,
                                "' table.");
                    }
                }
            }
        }
    }

    /**
     * Main method to execute the batch job.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();

        final DatabaseConfig config = DatabaseConfig.getDefault();
        final Profile profile = config.getCodeProfile(Contexts.BATCH_PATH);
        final Cache cache = new Cache(profile);

        final Runnable obj = new PostArchiveCleanup(cache);
        obj.run();
    }
}