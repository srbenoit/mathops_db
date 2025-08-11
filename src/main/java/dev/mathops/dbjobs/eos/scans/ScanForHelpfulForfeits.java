package dev.mathops.dbjobs.eos.scans;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.rawlogic.RawStcourseLogic;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.reclogic.TermLogic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A scan of registrations that checks for situations where a student did not complete a course, did not Forfeit the
 * course, but forfeiting would have improved the grade in an earlier course due to a different deadline schedule.
 *
 * <p>
 * If run in DEBUG mode, the program will simply scan for such cases and print them out.  In NORMAL mode, it will apply
 * the forfeits where beneficial (and print out what was done).
 * <p>
 * TODO: It might be useful to assign a different "open_status" code for forfeits applied this way to allow us to
 *  distinguish from forfeits based on student request.
 */
public class ScanForHelpfulForfeits implements Runnable {

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.DEBUG;

    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code ScanForHelpfulForfeits}.
     *
     * @param theCache the data cache
     */
    private ScanForHelpfulForfeits(final Cache theCache) {

        this.cache = theCache;
    }

    /**
     * Runs the process.
     */
    public void run() {

        try {
            final TermRec active = TermLogic.INSTANCE.queryActive(this.cache);

            if (active == null) {
                Log.warning("Unable to query the active term");
            } else {
                final List<RawStcourse> regs = getPacedRegs(active);
                final int count = regs.size();

                if (count > 0) {
                    processRegs(regs);
                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to query the active term.", ex);
        }
    }

    /**
     * Loads all registration records for the current term that are "paced" (where forfeiting one would potentially
     * change the deadline schedule for the rest).
     *
     * @param active the active term record
     * @return the list of Incomplete records
     */
    private List<RawStcourse> getPacedRegs(final TermRec active) {

        final List<RawStcourse> incRegs = new ArrayList<>(100);

        try {
            final List<RawStcourse> allRegs = RawStcourseLogic.queryByTerm(this.cache, active.term, false, false);
            for (final RawStcourse reg : allRegs) {
                if ("Y".equals(reg.iInProgress) || "D".equals(reg.openStatus) || "G".equals(reg.openStatus)
                    || "550".equals(reg.sect)) {
                    continue;
                }
                if ("Y".equals(reg.finalClassRoll) && RawRecordConstants.isOneCreditCourse(reg.course)) {
                    incRegs.add(reg);
                }
            }
        } catch (final SQLException ex) {
            Log.warning("Failed to scan for paced course registrations.", ex);
        }

        final int count = incRegs.size();
        final String countStr = Integer.toString(count);
        Log.info("Found ", countStr, " paced registrations to process...");

        return incRegs;
    }

    /**
     * Processes the list of registrations.
     *
     * @param incRegs the list of "Incomplete" registration records to process
     */
    private void processRegs(final Iterable<RawStcourse> incRegs) {

        // Sort registrations by student
        final Map<String, List<RawStcourse>> studentRegs = new HashMap<>(100);
        for (final RawStcourse reg : incRegs) {
            final List<RawStcourse> list = studentRegs.computeIfAbsent(reg.stuId, r -> new ArrayList<>(5));
            list.add(reg);
        }

        // Filter this set for students who completed at least one course but not all courses
        final Set<Map.Entry<String, List<RawStcourse>>> entrySet = studentRegs.entrySet();
        final Iterator<Map.Entry<String, List<RawStcourse>>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, List<RawStcourse>> entry = iter.next();
            final List<RawStcourse> list = entry.getValue();
            if (isFilteredOut(list)) {
                iter.remove();
            }
        }

        final int numStudentsToConsider = studentRegs.size();
        Log.info("Found " + numStudentsToConsider + " students to consider for helpful forfeits:");

        final Set<Map.Entry<String, List<RawStcourse>>> entrySet2 = studentRegs.entrySet();
        for (int size = 2; size < 6; ++size) {
            Log.fine(CoreConstants.EMPTY);
            Log.fine(size + "-course pace:");
            for (final Map.Entry<String, List<RawStcourse>> entry : entrySet2) {
                final String stuId = entry.getKey();
                final List<RawStcourse> list = entry.getValue();
                if (list.size() == size) {
                    Log.fine("    ", stuId, ":");
                    list.sort(null);
                    for (final RawStcourse reg : list) {
                        Log.fine("        ", reg.course, " (", reg.sect, "): Completed='", reg.completed, "'");
                    }
                }
            }
        }
    }

    /**
     * Scans a list of student course registrations to see if the list should be "filtered out" because no change is
     * needed.  We filter out lists where the number of completed courses is either 0 or equal to the number of
     * courses.
     *
     * @param list the list to process
     * @return true to filter out the list
     */
    private static boolean isFilteredOut(final Collection<RawStcourse> list) {

        final int size = list.size();
        int numComplete = 0;

        boolean gradeOfAInAllComplete = true;
        for (final RawStcourse row : list) {
            if ("Y".equals(row.completed)) {
                ++numComplete;
                if (row.score != null && row.score.intValue() < 65) {
                    gradeOfAInAllComplete = false;
                }
            }
        }

        return numComplete == 0 || numComplete == size || gradeOfAInAllComplete;
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

        final Runnable obj = new ScanForHelpfulForfeits(cache);
        obj.run();
    }
}