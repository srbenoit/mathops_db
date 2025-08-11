package dev.mathops.dbjobs.eos.scans;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.reclogic.TermLogic;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Performs some cleanup before final grading:
 * <ul>
 *     <li>For any STCOURSE rows with prereq_satis set to "B", resets those to null</li>
 *     <li>Sets open status to "N" for any STCOURSE with i_in_progress='Y', an i_deadline_dt before today, and
 *     open_status either null or "Y".</li>
 *     <li>Removes old (from dates before today) roes from PENDING_EXAM.</li>
 *     <li>Removes old rows from FCR</li>
 *     <li></li>
 * </ul>
 */
public class Cleanup implements Runnable {

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.NORMAL;

    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code Cleanup}.
     *
     * @param theCache the data cache
     */
    private Cleanup(final Cache theCache) {

        this.cache = theCache;
    }

    /**
     * Runs the process.
     */
    public void run() {

        final DbConnection conn = this.cache.checkOutConnection(ESchema.LEGACY);
        try {
            final TermRec active = TermLogic.INSTANCE.queryActive(this.cache);

            if (active == null) {
                Log.warning("Unable to query the active term");
            } else {
                clearPrereqSatisB(conn, active.term);
                closeIncompletes(conn);
                cleanOldPending(conn);
                cleanFinalCRoll(conn);
                cleanFcrStudent(conn);
                cleanStc(conn);
                cleanDelphi(conn);
                cleanDelphiCheck(conn);
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to query the active term.", ex);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Resets 'prereq_satis' to null where 'prereq_satis' is 'B' in the active term.
     *
     * @param conn      the database connection
     * @param activeKey the active term key
     */
    private void clearPrereqSatisB(final DbConnection conn, final TermKey activeKey) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT * FROM stcourse WHERE prereq_satis = 'B' AND term=",
                        conn.sqlStringValue(activeKey.termCode), " AND term_yr=",
                        conn.sqlIntegerValue(activeKey.shortYear));
                try (final ResultSet rs = stmt.executeQuery(sql)) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        final String course = getStringField(rs, "course");
                        final String sect = getStringField(rs, "sect");
                        Log.info("Found 'B' in prereq_satis for student ", stuId, " in ", course, " (", sect, ").");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No records found with 'B' in prereq_satis.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat(
                        "UPDATE stcourse SET prereq_satis=NULL WHERE prereq_satis = 'B' AND term=",
                        conn.sqlStringValue(activeKey.termCode), " AND term_yr=",
                        conn.sqlIntegerValue(activeKey.shortYear));
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Updated " + count + " rows from prereq_satis='B' to prereq_satis=NULL.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to update 'prereq_satis' to NULL where it had been 'B'", ex);
        }
    }

    /**
     * Sets 'open_status' to 'N' for all STCOURSE records that have 'i_in_progress' of 'Y' and open_status that is
     * either 'Y' or null.
     */
    private void closeIncompletes(final DbConnection conn) {

        final LocalDate today = LocalDate.now();

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat(
                        "SELECT * FROM stcourse WHERE i_in_progress = 'Y' AND i_deadline_dt<",
                        conn.sqlDateValue(today), " AND (open_status='Y' OR open_status IS NULL)");
                try (final ResultSet rs = stmt.executeQuery(sql)) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        final String course = getStringField(rs, "course");
                        final String sect = getStringField(rs, "sect");
                        Log.info("Found Incomplete to close for student ", stuId, " in ", course, " (", sect, ").");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No incompletes found to close.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat(
                        "UPDATE stcourse SET open_status='N' WHERE i_in_progress = 'Y' AND i_deadline_dt<",
                        conn.sqlDateValue(today), " AND (open_status='Y' OR open_status IS NULL)");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Closed " + count + " incompletes that were past their deadline.");
            }

            // In either case, print out all incompletes that are still not in the 'N" state...
            final String sql = SimpleBuilder.concat(
                    "SELECT * FROM stcourse WHERE i_in_progress = 'Y' and open_status <> 'N'");
            try (final ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    final String stuId = getStringField(rs, "stu_id");
                    final String course = getStringField(rs, "course");
                    final String sect = getStringField(rs, "sect");
                    final String openStatus = getStringField(rs, "open_status");
                    Log.info("  NOTE: There is still an Incomplete for student ", stuId, " in ", course, " (", sect,
                            ") with open_status '", openStatus, "'.");

                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to close Incompletes that are beyond deadline date", ex);
        }
    }

    /**
     * Deletes any 'pending_exam' rows whose exam date is before today.
     */
    private void cleanOldPending(final DbConnection conn) {

        final LocalDate today = LocalDate.now();

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT * FROM pending_exam WHERE exam_dt<",
                        conn.sqlDateValue(today));
                try (final ResultSet rs = stmt.executeQuery(sql)) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found old PENDING_EXAM row student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old PENDING_EXAM rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM pending_exam WHERE exam_dt<",
                        conn.sqlDateValue(today));
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " old pending exam rows.");
            }

            // In either case, print out any current pending exam rows (there should be none)
            final String sql = SimpleBuilder.concat("SELECT * FROM pending_exam");
            try (final ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    final String stuId = getStringField(rs, "stu_id");
                    Log.info("  WARNING: There is a pending_exam row for student ", stuId, "!");

                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to close Incompletes that are beyond deadline date", ex);
        }
    }

    /**
     * Deletes any rows from the FINAL_CROLL.
     */
    private void cleanFinalCRoll(final DbConnection conn) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                try (final ResultSet rs = stmt.executeQuery("SELECT * FROM final_croll WHERE stu_id IS NOT NULL")) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found FINAL_CROLL record to delete for student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old FINAL_CROLL rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM final_croll WHERE stu_id IS NOT NULL");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " FINAL_CROLL rows.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to delete FINAL_CROLL records", ex);
        }
    }

    /**
     * Deletes all rows from the FCR_STUDENT table.
     */
    private void cleanFcrStudent(final DbConnection conn) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                try (final ResultSet rs = stmt.executeQuery("SELECT * FROM fcr_student WHERE stu_id IS NOT NULL")) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found FCR_STUDENT record to delete for student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old FCR_STUDENT rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM fcr_student WHERE stu_id IS NOT NULL");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " FCR_STUDENT rows.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to delete FCR_STUDENT records", ex);
        }
    }

    /**
     * Deletes all rows from the STC table.
     */
    private void cleanStc(final DbConnection conn) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                try (final ResultSet rs = stmt.executeQuery("SELECT * FROM stc WHERE stu_id IS NOT NULL")) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found STC record to delete for student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old STC rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM stc WHERE stu_id IS NOT NULL");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " STC rows.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to delete STC records", ex);
        }
    }

    /**
     * Deletes all rows from the DELPHI table.
     */
    private void cleanDelphi(final DbConnection conn) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                try (final ResultSet rs = stmt.executeQuery("SELECT * FROM delphi WHERE stu_id IS NOT NULL")) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found DELPHI record to delete for student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old DELPHI rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM delphi WHERE stu_id IS NOT NULL");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " DELPHI rows.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to delete DELPHI records", ex);
        }
    }

    /**
     * Deletes all rows from the DELPHI_CHECK table.
     */
    private void cleanDelphiCheck(final DbConnection conn) {

        try (final Statement stmt = conn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                try (final ResultSet rs = stmt.executeQuery("SELECT * FROM delphi_check WHERE stu_id IS NOT NULL")) {
                    int count = 0;
                    while (rs.next()) {
                        final String stuId = getStringField(rs, "stu_id");
                        Log.info("Found DELPHI_CHECK record to delete for student ", stuId, ".");
                        ++count;
                    }
                    if (count == 0) {
                        Log.info("No old DELPHI_CHECK rows found to delete.");
                    }
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM delphi_check WHERE stu_id IS NOT NULL");
                final int count = stmt.executeUpdate(sql);
                conn.commit();
                Log.info("Deleted " + count + " DELPHI_CHECK rows.");
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to delete DELPHI_CHECK records", ex);
        }
    }

    /**
     * Retrieves a String field value from a result set, returning null if the result set indicates a null value was
     * present. The string is trimmed to remove leading or trailing whitespace.
     *
     * @param rs   the result set
     * @param name the field name
     * @return the value
     * @throws SQLException if there is an error retrieving the value
     */
    private String getStringField(final ResultSet rs, final String name) throws SQLException {

        final String tmp = rs.getString(name);

        return tmp == null ? null : tmp.trim();
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

        final Runnable obj = new Cleanup(cache);
        obj.run();
    }
}