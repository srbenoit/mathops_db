package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.old.rawrecord.RawHoldType;
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
 * Tests for the {@code RawHoldTypeLogic} class.
 */
final class TestRawHoldTypeLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawHoldTypeLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawHoldType raw1 = new RawHoldType("AA", "B", "C", "D", "E");
            final RawHoldType raw2 = new RawHoldType("FF", "G", "H", "I", "J");

            assertTrue(RawHoldTypeLogic.insert(cache, raw1), "Failed to insert hold_type");
            assertTrue(RawHoldTypeLogic.insert(cache, raw2), "Failed to insert hold_type");
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
            final List<RawHoldType> all = RawHoldTypeLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;

            for (final RawHoldType r : all) {

                if ("AA".equals(r.holdId)
                    && "B".equals(r.sevAdminHold)
                    && "C".equals(r.holdType)
                    && "D".equals(r.addHold)
                    && "E".equals(r.deleteHold)) {

                    found1 = true;

                } else if ("FF".equals(r.holdId)
                           && "G".equals(r.sevAdminHold)
                           && "H".equals(r.holdType)
                           && "I".equals(r.addHold)
                           && "J".equals(r.deleteHold)) {

                    found2 = true;

                } else {
                    Log.warning("Unexpected holdId ", r.holdId);
                    Log.warning("Unexpected sevAdminHold ", r.sevAdminHold);
                    Log.warning("Unexpected holdType ", r.holdType);
                    Log.warning("Unexpected addHold ", r.addHold);
                    Log.warning("Unexpected deleteHold ", r.deleteHold);
                }
            }

            assertTrue(found1, "HoldType 1 not found");
            assertTrue(found2, "HoldType 2 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all hold_type rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final RawHoldType raw2 = new RawHoldType("FF", "G", "H", "I", "J");

            final boolean result = RawHoldTypeLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawHoldType> all = RawHoldTypeLogic.queryAll(cache);

            assertEquals(1, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;

            for (final RawHoldType r : all) {

                if ("AA".equals(r.holdId)
                    && "B".equals(r.sevAdminHold)
                    && "C".equals(r.holdType)
                    && "D".equals(r.addHold)
                    && "E".equals(r.deleteHold)) {

                    found1 = true;

                } else {
                    Log.warning("Unexpected holdId ", r.holdId);
                    Log.warning("Unexpected sevAdminHold ", r.sevAdminHold);
                    Log.warning("Unexpected holdType ", r.holdType);
                    Log.warning("Unexpected addHold ", r.addHold);
                    Log.warning("Unexpected deleteHold ", r.deleteHold);
                }
            }

            assertTrue(found1, "hold_type 1 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting hold_type: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawHoldTypeLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while cleaning table: " + ex.getMessage());
        } finally {
            Cache.checkInConnection(conn);
        }
    }
}
