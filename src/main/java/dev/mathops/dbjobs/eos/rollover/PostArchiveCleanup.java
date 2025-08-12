package dev.mathops.dbjobs.eos.rollover;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
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
    private static final EDebugMode DEBUG_MODE = EDebugMode.NORMAL;

    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code PreArchiveCleanup}.
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
//            buildNewCUnit(prodConn);
//            deleteOldCunit(prodConn);

        } catch (final SQLException ex) {
            Log.warning("Exception performing post-archive cleanup.", ex);
        } finally {
            Cache.checkInConnection(prodConn);
        }
    }

    /**
     * Copies all active-term CUNIT records to next-term records.
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private void buildNewCUnit(final DbConnection prodConn) throws SQLException {

        Log.info("> Copying active-term CUNIT records to next-term records.");

        final TermRec nextTerm = this.cache.getSystemData().getNextTerm();

        if (nextTerm == null) {
            Log.warning("Failed to query 'next' term.");
        } else {
            final TermKey nextKey = nextTerm.term;

            try (final Statement statement = prodConn.createStatement()) {

                if (DEBUG_MODE == EDebugMode.DEBUG) {
                    final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM cunit",
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");
                    try (final ResultSet rs = statement.executeQuery(sql)) {
                        rs.next();
                        final int count = rs.getInt(1);
                        Log.info("Found " + count + " CUNIT records to copy.");
                    }
                } else {
                    final String sql = SimpleBuilder.concat("SELECT * FROM cunit",
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");

                    try (final ResultSet rs = statement.executeQuery(sql);) {
                        final ResultSetMetaData meta = rs.getMetaData();
                        final int cols = meta.getColumnCount();
                        final int[] types = new int[cols];

                        final HtmlBuilder builder = new HtmlBuilder(200);
                        builder.add("INSERT INTO cunit (");
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
                                        Log.warning("FAILED to insert record into cunit table");
                                    }
                                }
                            }

                            prodConn.commit();
                            Log.info("Copied " + numRows + " records within 'cunit' table.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Deletes CUNIT records that are 2 or more years old.
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteOldCunit(final DbConnection prodConn) throws SQLException {

        Log.info("> Deleting CUNIT records that are more than 2 years old.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM cunit",
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("Found " + count + " old CUNIT records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM cunit",
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("Deleted " + count + " old CUNIT records.");
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