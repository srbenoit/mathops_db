package dev.mathops.dbjobs.eos.rollover;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Performs some cleanup of data before archiving.  This is based on commands in the old "eos_arc" and "eos_load" *
 * Informix batches.
 */
public final class PreArchiveCleanup implements Runnable {

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.DEBUG;

    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code PreArchiveCleanup}.
     *
     * @param theCache the data cache
     */
    private PreArchiveCleanup(final Cache theCache) {

        this.cache = theCache;
    }

    /**
     * Runs the process.
     */
    public void run() {

        final DbConnection prodConn = this.cache.checkOutConnection(ESchema.LEGACY);
        try {
            cleanBogusRegistrations(prodConn);
            cleanDroppedRegistrations(prodConn);
        } catch (final SQLException ex) {
            Log.warning("Exception performing pre-archive cleanup.", ex);
        } finally {
            Cache.checkInConnection(prodConn);
        }
    }

    /**
     * Deletes STCOURSE records from the active term whose 'sect' field matches that in a 'bogus_mapping' record.
     *
     * @param prodConn the connection to the production database
     */
    private static void cleanBogusRegistrations(final DbConnection prodConn) throws SQLException {

        Log.info("> Cleaning registrations in 'bogus' sections.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM stcourse WHERE sect IN ",
                        "(SELECT bogus_sect FROM bogus_mapping",
                        "  WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        "    AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y'))",
                        " AND term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("Found " + count + " records to delete from STCOURSE associated with 'bogus' sections.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM stcourse WHERE sect IN ",
                        "(SELECT bogus_sect FROM bogus_mapping",
                        "  WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        "    AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y'))",
                        " AND term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("Deleted " + count + " records from STCOURSE associated with 'bogus' sections.");
            }
        }
    }

    /**
     * Deletes STCOURSE records that are dropped (open_status = 'D') and whose 'last_class_roll_dt' is either null or
     * before the start of the active term.
     *
     * @param prodConn the connection to the production database
     */
    private static void cleanDroppedRegistrations(final DbConnection prodConn) throws SQLException {

        Log.info("> Cleaning dropped registrations.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM stcourse",
                        " WHERE (last_class_roll_dt < (SELECT last_rec_dt FROM term WHERE active = 'Y')",
                        "        OR last_class_roll_dt IS NULL)",
                        " AND (open_status = 'D') ",
                        " AND final_class_roll = 'N';");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("Found " + count + " dropped records to delete from STCOURSE.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM stcourse",
                        " WHERE (last_class_roll_dt < (SELECT last_rec_dt FROM term WHERE active = 'Y')",
                        "        OR last_class_roll_dt IS NULL)",
                        " AND (open_status = 'D') ",
                        " AND final_class_roll = 'N';");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("Deleted " + count + " dropped records from STCOURSE");
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

        final Runnable obj = new PreArchiveCleanup(cache);
        obj.run();
    }
}