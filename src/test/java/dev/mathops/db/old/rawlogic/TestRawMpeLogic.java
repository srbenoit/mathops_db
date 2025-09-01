package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.schema.legacy.RawMpe;
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
 * Tests for the {@code RawMpeLogic} class.
 */
final class TestRawMpeLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMpeLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawMpe raw1 = new RawMpe("MPTTC", Integer.valueOf(0), Integer.valueOf(2));
            final RawMpe raw2 = new RawMpe("MPTUN", Integer.valueOf(1), Integer.valueOf(0));
            final RawMpe raw3 = new RawMpe("MPTPU", Integer.valueOf(0), Integer.valueOf(1));

            assertTrue(RawMpeLogic.insert(cache, raw1), "Failed to insert mpe 1");
            assertTrue(RawMpeLogic.insert(cache, raw2), "Failed to insert mpe 2");
            assertTrue(RawMpeLogic.insert(cache, raw3), "Failed to insert mpe 3");
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
            final List<RawMpe> all = RawMpeLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawMpe test : all) {

                if ("MPTTC".equals(test.version)
                    && Integer.valueOf(0).equals(test.maxOnlineAtmpts)
                    && Integer.valueOf(2).equals(test.maxProctoredAtmpts)) {

                    found1 = true;
                } else if ("MPTUN".equals(test.version)
                           && Integer.valueOf(1).equals(test.maxOnlineAtmpts)
                           && Integer.valueOf(0).equals(test.maxProctoredAtmpts)) {

                    found2 = true;
                } else if ("MPTPU".equals(test.version)
                           && Integer.valueOf(0).equals(test.maxOnlineAtmpts)
                           && Integer.valueOf(1).equals(test.maxProctoredAtmpts)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected version ", test.version);
                    Log.warning("Unexpected maxOnlineAtmpts ", test.maxOnlineAtmpts);
                    Log.warning("Unexpected maxProctoredAtmpts ", test.maxProctoredAtmpts);
                }
            }

            assertTrue(found1, "mpe 1 not found");
            assertTrue(found2, "mpe 2 not found");
            assertTrue(found3, "mpe 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all mpe rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawMpe raw2 = new RawMpe("MPTUN", Integer.valueOf(1), Integer.valueOf(0));

            final boolean result = RawMpeLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawMpe> all = RawMpeLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawMpe test : all) {

                if ("MPTTC".equals(test.version)
                    && Integer.valueOf(0).equals(test.maxOnlineAtmpts)
                    && Integer.valueOf(2).equals(test.maxProctoredAtmpts)) {

                    found1 = true;
                } else if ("MPTPU".equals(test.version)
                           && Integer.valueOf(0).equals(test.maxOnlineAtmpts)
                           && Integer.valueOf(1).equals(test.maxProctoredAtmpts)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected version ", test.version);
                    Log.warning("Unexpected maxOnlineAtmpts ", test.maxOnlineAtmpts);
                    Log.warning("Unexpected maxProctoredAtmpts ", test.maxProctoredAtmpts);
                }
            }

            assertTrue(found1, "mpe 1 not found");
            assertTrue(found3, "mpe 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting mpe: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMpeLogic.getTableName(cache);
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
