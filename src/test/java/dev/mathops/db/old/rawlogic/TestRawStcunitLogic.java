package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcunit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@code RawStcunitLogic} class.
 */
final class TestRawStcunitLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawStcunitLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawStcunit raw1 = new RawStcunit(
                    "111111111",
                    RawRecordConstants.M117,
                    Integer.valueOf(1), // Unit
                    "Y",
                    Integer.valueOf(8), // RE Score
                    Integer.valueOf(3), // RE Points
                    "P",
                    Integer.valueOf(16), // UE Score
                    Integer.valueOf(10)); // UE Points

            final RawStcunit raw2 = new RawStcunit(
                    "111111111",
                    RawRecordConstants.M117,
                    Integer.valueOf(2), // Unit
                    "N",
                    Integer.valueOf(9), // RE Score
                    Integer.valueOf(4), // RE Points
                    "Q",
                    Integer.valueOf(17), // UE Score
                    Integer.valueOf(11)); // UE Points

            final RawStcunit raw3 = new RawStcunit(
                    "222222222",
                    RawRecordConstants.M118,
                    Integer.valueOf(3), // Unit
                    "G",
                    Integer.valueOf(10), // RE Score
                    Integer.valueOf(5), // RE Points
                    "R",
                    Integer.valueOf(18), // UE Score
                    Integer.valueOf(12)); // UE Points

            assertTrue(RawStcunitLogic.insert(cache, raw1), "Failed to insert stcunit 1");
            assertTrue(RawStcunitLogic.insert(cache, raw2), "Failed to insert stcunit 2");
            assertTrue(RawStcunitLogic.insert(cache, raw3), "Failed to insert stcunit 3");
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
            final List<RawStcunit> all = RawStcunitLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawStcunit test : all) {
                if ("111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && Integer.valueOf(1).equals(test.unit)
                    && "Y".equals(test.reviewStatus)
                    && Integer.valueOf(8).equals(test.reviewScore)
                    && Integer.valueOf(3).equals(test.reviewPoints)
                    && "P".equals(test.proctoredStatus)
                    && Integer.valueOf(16).equals(test.proctoredScore)
                    && Integer.valueOf(10).equals(test.proctoredPoints)) {

                    found1 = true;

                } else if ("111111111".equals(test.stuId)
                           && RawRecordConstants.M117.equals(test.course)
                           && Integer.valueOf(2).equals(test.unit)
                           && "N".equals(test.reviewStatus)
                           && Integer.valueOf(9).equals(test.reviewScore)
                           && Integer.valueOf(4).equals(test.reviewPoints)
                           && "Q".equals(test.proctoredStatus)
                           && Integer.valueOf(17).equals(test.proctoredScore)
                           && Integer.valueOf(11).equals(test.proctoredPoints)) {

                    found2 = true;

                } else if ("222222222".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && Integer.valueOf(3).equals(test.unit)
                           && "G".equals(test.reviewStatus)
                           && Integer.valueOf(10).equals(test.reviewScore)
                           && Integer.valueOf(5).equals(test.reviewPoints)
                           && "R".equals(test.proctoredStatus)
                           && Integer.valueOf(18).equals(test.proctoredScore)
                           && Integer.valueOf(12).equals(test.proctoredPoints)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected unit ", test.unit);
                    Log.warning("Unexpected reviewStatus ", test.reviewStatus);
                    Log.warning("Unexpected reviewScore ", test.reviewScore);
                    Log.warning("Unexpected reviewPoints ", test.reviewPoints);
                    Log.warning("Unexpected proctoredStatus ", test.proctoredStatus);
                    Log.warning("Unexpected proctoredScore ", test.proctoredScore);
                    Log.warning("Unexpected proctoredPoints ", test.proctoredPoints);
                }
            }

            assertTrue(found1, "Stcunit 1 not found");
            assertTrue(found2, "Stcunit 2 not found");
            assertTrue(found3, "Stcunit 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all stcunit rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final RawStcunit raw2 = new RawStcunit(
                    "111111111",
                    RawRecordConstants.M117,
                    Integer.valueOf(2), // Unit
                    "N",
                    Integer.valueOf(9), // RE Score
                    Integer.valueOf(4), // RE Points
                    "Q",
                    Integer.valueOf(17), // UE Score
                    Integer.valueOf(11)); // UE Points

            final boolean result = RawStcunitLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawStcunit> all = RawStcunitLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawStcunit test : all) {
                if ("111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && Integer.valueOf(1).equals(test.unit)
                    && "Y".equals(test.reviewStatus)
                    && Integer.valueOf(8).equals(test.reviewScore)
                    && Integer.valueOf(3).equals(test.reviewPoints)
                    && "P".equals(test.proctoredStatus)
                    && Integer.valueOf(16).equals(test.proctoredScore)
                    && Integer.valueOf(10).equals(test.proctoredPoints)) {

                    found1 = true;

                } else if ("222222222".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && Integer.valueOf(3).equals(test.unit)
                           && "G".equals(test.reviewStatus)
                           && Integer.valueOf(10).equals(test.reviewScore)
                           && Integer.valueOf(5).equals(test.reviewPoints)
                           && "R".equals(test.proctoredStatus)
                           && Integer.valueOf(18).equals(test.proctoredScore)
                           && Integer.valueOf(12).equals(test.proctoredPoints)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected unit ", test.unit);
                    Log.warning("Unexpected reviewStatus ", test.reviewStatus);
                    Log.warning("Unexpected reviewScore ", test.reviewScore);
                    Log.warning("Unexpected reviewPoints ", test.reviewPoints);
                    Log.warning("Unexpected proctoredStatus ", test.proctoredStatus);
                    Log.warning("Unexpected proctoredScore ", test.proctoredScore);
                    Log.warning("Unexpected proctoredPoints ", test.proctoredPoints);
                }
            }

            assertTrue(found1, "Stcunit 1 not found");
            assertTrue(found3, "Stcunit 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting users: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawStcunitLogic.getTableName(cache);
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
