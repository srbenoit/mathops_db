package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.enums.ETermName;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.schema.legacy.RawMilestone;
import dev.mathops.db.type.TermKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@code RawMilestoneLogic} class.
 */
final class TestRawMilestoneLogic {

    /** A term key used in test records. */
    private static final TermKey fa21 = new TermKey(ETermName.FALL, 2021);

    /** A term key used in test records. */
    private static final TermKey fa20 = new TermKey(ETermName.FALL, 2020);

    /** A date used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 2, 3);

    /** A date used in test records. */
    private static final LocalDate date2 = LocalDate.of(2021, 2, 4);

    /** A date used in test records. */
    private static final LocalDate date3 = LocalDate.of(2021, 2, 5);

    /** A date used in test records. */
    private static final LocalDate date4 = LocalDate.of(2022, 3, 4);

    /** A date used in test records. */
    private static final LocalDate date5 = LocalDate.of(2023, 4, 5);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMilestoneLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawMilestone raw1 = new RawMilestone(fa21, Integer.valueOf(1), "A", Integer.valueOf(111),
                    "RE", date1, Integer.valueOf(0));

            final RawMilestone raw2 = new RawMilestone(fa21, Integer.valueOf(1), "B", Integer.valueOf(121),
                    "R1", date2, Integer.valueOf(1));

            final RawMilestone raw3 = new RawMilestone(fa21, Integer.valueOf(2), "A", Integer.valueOf(131),
                    "R2", date3, Integer.valueOf(2));

            final RawMilestone raw4 = new RawMilestone(fa21, Integer.valueOf(2), "B", Integer.valueOf(141),
                    "UE", date4, Integer.valueOf(3));

            final RawMilestone raw5 = new RawMilestone(fa20, Integer.valueOf(3), "C", Integer.valueOf(151),
                    "FE", date5, Integer.valueOf(4));

            assertTrue(RawMilestoneLogic.insert(cache, raw1), "Failed to insert milestone 1");
            assertTrue(RawMilestoneLogic.insert(cache, raw2), "Failed to insert milestone 2");
            assertTrue(RawMilestoneLogic.insert(cache, raw3), "Failed to insert milestone 3");
            assertTrue(RawMilestoneLogic.insert(cache, raw4), "Failed to insert milestone 4");
            assertTrue(RawMilestoneLogic.insert(cache, raw5), "Failed to insert milestone 5");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while initializing tables: " + ex.getMessage());
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryAll results")
    void test0003() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawMilestone> all = RawMilestoneLogic.queryAll(cache);

            assertEquals(5, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;
            boolean found4 = false;
            boolean found5 = false;

            for (final RawMilestone r : all) {

                if (fa21.equals(r.termKey)
                    && Integer.valueOf(1).equals(r.pace)
                    && "A".equals(r.paceTrack)
                    && Integer.valueOf(111).equals(r.msNbr)
                    && "RE".equals(r.msType)
                    && date1.equals(r.msDate)
                    && Integer.valueOf(0).equals(r.nbrAtmptsAllow)) {

                    found1 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(1).equals(r.pace)
                           && "B".equals(r.paceTrack)
                           && Integer.valueOf(121).equals(r.msNbr)
                           && "R1".equals(r.msType)
                           && date2.equals(r.msDate)
                           && Integer.valueOf(1).equals(r.nbrAtmptsAllow)) {

                    found2 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "A".equals(r.paceTrack)
                           && Integer.valueOf(131).equals(r.msNbr)
                           && "R2".equals(r.msType)
                           && date3.equals(r.msDate)
                           && Integer.valueOf(2).equals(r.nbrAtmptsAllow)) {

                    found3 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "B".equals(r.paceTrack)
                           && Integer.valueOf(141).equals(r.msNbr)
                           && "UE".equals(r.msType)
                           && date4.equals(r.msDate)
                           && Integer.valueOf(3).equals(r.nbrAtmptsAllow)) {

                    found4 = true;

                } else if (fa20.equals(r.termKey)
                           && Integer.valueOf(3).equals(r.pace)
                           && "C".equals(r.paceTrack)
                           && Integer.valueOf(151).equals(r.msNbr)
                           && "FE".equals(r.msType)
                           && date5.equals(r.msDate)
                           && Integer.valueOf(4).equals(r.nbrAtmptsAllow)) {

                    found5 = true;
                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected pace ", r.pace);
                    Log.warning("Unexpected paceTrack ", r.paceTrack);
                    Log.warning("Unexpected msNbr ", r.msNbr);
                    Log.warning("Unexpected msType ", r.msType);
                    Log.warning("Unexpected msDate ", r.msDate);
                    Log.warning("Unexpected nbrAtmptsAllow ", r.nbrAtmptsAllow);
                }
            }

            assertTrue(found1, "milestone 1 not found");
            assertTrue(found2, "milestone 2 not found");
            assertTrue(found3, "milestone 3 not found");
            assertTrue(found4, "milestone 4 not found");
            assertTrue(found5, "milestone 5 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all milestone rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("getAllMilestones results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawMilestone> all = RawMilestoneLogic.getAllMilestones(cache, fa21);

            assertEquals(4, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;
            boolean found4 = false;

            for (final RawMilestone r : all) {

                if (fa21.equals(r.termKey)
                    && Integer.valueOf(1).equals(r.pace)
                    && "A".equals(r.paceTrack)
                    && Integer.valueOf(111).equals(r.msNbr)
                    && "RE".equals(r.msType)
                    && date1.equals(r.msDate)
                    && Integer.valueOf(0).equals(r.nbrAtmptsAllow)) {

                    found1 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(1).equals(r.pace)
                           && "B".equals(r.paceTrack)
                           && Integer.valueOf(121).equals(r.msNbr)
                           && "R1".equals(r.msType)
                           && date2.equals(r.msDate)
                           && Integer.valueOf(1).equals(r.nbrAtmptsAllow)) {

                    found2 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "A".equals(r.paceTrack)
                           && Integer.valueOf(131).equals(r.msNbr)
                           && "R2".equals(r.msType)
                           && date3.equals(r.msDate)
                           && Integer.valueOf(2).equals(r.nbrAtmptsAllow)) {

                    found3 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "B".equals(r.paceTrack)
                           && Integer.valueOf(141).equals(r.msNbr)
                           && "UE".equals(r.msType)
                           && date4.equals(r.msDate)
                           && Integer.valueOf(3).equals(r.nbrAtmptsAllow)) {

                    found4 = true;
                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected pace ", r.pace);
                    Log.warning("Unexpected paceTrack ", r.paceTrack);
                    Log.warning("Unexpected msNbr ", r.msNbr);
                    Log.warning("Unexpected msType ", r.msType);
                    Log.warning("Unexpected msDate ", r.msDate);
                    Log.warning("Unexpected nbrAtmptsAllow ", r.nbrAtmptsAllow);
                }
            }

            assertTrue(found1, "milestone 1 not found");
            assertTrue(found2, "milestone 2 not found");
            assertTrue(found3, "milestone 3 not found");
            assertTrue(found4, "milestone 4 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying milestone rows for term: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("getAllMilestones results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawMilestone> all = RawMilestoneLogic.getAllMilestones(cache, fa21, 1, "A");

            assertEquals(1, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;

            for (final RawMilestone r : all) {

                if (fa21.equals(r.termKey)
                    && Integer.valueOf(1).equals(r.pace)
                    && "A".equals(r.paceTrack)
                    && Integer.valueOf(111).equals(r.msNbr)
                    && "RE".equals(r.msType)
                    && date1.equals(r.msDate)
                    && Integer.valueOf(0).equals(r.nbrAtmptsAllow)) {

                    found1 = true;

                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected pace ", r.pace);
                    Log.warning("Unexpected paceTrack ", r.paceTrack);
                    Log.warning("Unexpected msNbr ", r.msNbr);
                    Log.warning("Unexpected msType ", r.msType);
                    Log.warning("Unexpected msDate ", r.msDate);
                    Log.warning("Unexpected nbrAtmptsAllow ", r.nbrAtmptsAllow);
                }
            }

            assertTrue(found1, "milestone 1 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying milestone rows for term/pace/track: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0006() {

        final Cache cache = new Cache(profile);

        try {
            final RawMilestone raw2 = new RawMilestone(fa21, Integer.valueOf(1), "B", Integer.valueOf(121),
                    "R1", date2, Integer.valueOf(1));

            final boolean result = RawMilestoneLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawMilestone> all = RawMilestoneLogic.queryAll(cache);

            assertEquals(4, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;
            boolean found4 = false;
            boolean found5 = false;

            for (final RawMilestone r : all) {

                if (fa21.equals(r.termKey)
                    && Integer.valueOf(1).equals(r.pace)
                    && "A".equals(r.paceTrack)
                    && Integer.valueOf(111).equals(r.msNbr)
                    && "RE".equals(r.msType)
                    && date1.equals(r.msDate)
                    && Integer.valueOf(0).equals(r.nbrAtmptsAllow)) {

                    found1 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "A".equals(r.paceTrack)
                           && Integer.valueOf(131).equals(r.msNbr)
                           && "R2".equals(r.msType)
                           && date3.equals(r.msDate)
                           && Integer.valueOf(2).equals(r.nbrAtmptsAllow)) {

                    found3 = true;

                } else if (fa21.equals(r.termKey)
                           && Integer.valueOf(2).equals(r.pace)
                           && "B".equals(r.paceTrack)
                           && Integer.valueOf(141).equals(r.msNbr)
                           && "UE".equals(r.msType)
                           && date4.equals(r.msDate)
                           && Integer.valueOf(3).equals(r.nbrAtmptsAllow)) {

                    found4 = true;

                } else if (fa20.equals(r.termKey)
                           && Integer.valueOf(3).equals(r.pace)
                           && "C".equals(r.paceTrack)
                           && Integer.valueOf(151).equals(r.msNbr)
                           && "FE".equals(r.msType)
                           && date5.equals(r.msDate)
                           && Integer.valueOf(4).equals(r.nbrAtmptsAllow)) {

                    found5 = true;
                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected pace ", r.pace);
                    Log.warning("Unexpected paceTrack ", r.paceTrack);
                    Log.warning("Unexpected msNbr ", r.msNbr);
                    Log.warning("Unexpected msType ", r.msType);
                    Log.warning("Unexpected msDate ", r.msDate);
                    Log.warning("Unexpected nbrAtmptsAllow ", r.nbrAtmptsAllow);
                }
            }

            assertTrue(found1, "milestone 1 not found");
            assertTrue(found3, "milestone 3 not found");
            assertTrue(found4, "milestone 4 not found");
            assertTrue(found5, "milestone 5 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting milestone: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMilestoneLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while cleaning tables: " + ex.getMessage());
        } finally {
            Cache.checkInConnection(conn);
        }
    }
}
