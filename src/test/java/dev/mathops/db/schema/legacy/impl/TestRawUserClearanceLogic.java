package dev.mathops.db.schema.legacy.impl;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.TestUtils;
import dev.mathops.db.schema.legacy.rec.RawUserClearance;
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
 * Tests for the {@code RawUserClearanceLogic} class.
 */
final class TestRawUserClearanceLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawUserClearanceLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawUserClearance raw1 = new RawUserClearance("user01", "fxn001", Integer.valueOf(1), "pwd01");
            final RawUserClearance raw2 = new RawUserClearance("user01", "fxn002", Integer.valueOf(2), "pwd02");
            final RawUserClearance raw3 = new RawUserClearance("user02", "fxn003", Integer.valueOf(3), "pwd03");

            assertTrue(RawUserClearanceLogic.insert(cache, raw1), "Failed to insert user_clearance 1");
            assertTrue(RawUserClearanceLogic.insert(cache, raw2), "Failed to insert user_clearance 2");
            assertTrue(RawUserClearanceLogic.insert(cache, raw3), "Failed to insert user_clearance 3");
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
            final List<RawUserClearance> all = RawUserClearanceLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawUserClearance test : all) {

                if ("user01".equals(test.login) && "fxn001".equals(test.clearFunction)
                    && Integer.valueOf(1).equals(test.clearType) && "pwd01".equals(test.clearPasswd)) {

                    found1 = true;

                } else if ("user01".equals(test.login) && "fxn002".equals(test.clearFunction)
                           && Integer.valueOf(2).equals(test.clearType) && "pwd02".equals(test.clearPasswd)) {

                    found2 = true;

                } else if ("user02".equals(test.login) && "fxn003".equals(test.clearFunction)
                           && Integer.valueOf(3).equals(test.clearType) && "pwd03".equals(test.clearPasswd)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected login ", test.login);
                    Log.warning("Unexpected clearFunction ", test.clearFunction);
                    Log.warning("Unexpected clearType ", test.clearType);
                    Log.warning("Unexpected clearPasswd ", test.clearPasswd);
                }
            }

            assertTrue(found1, "user clearance 1 not found");
            assertTrue(found2, "user clearance 2 not found");
            assertTrue(found3, "user clearance 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all user_clearance rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryAllForLogin results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawUserClearance> all = RawUserClearanceLogic.queryAllForLogin(cache, "user01");

            assertEquals(2, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;

            for (final RawUserClearance test : all) {

                if ("user01".equals(test.login) && "fxn001".equals(test.clearFunction)
                    && Integer.valueOf(1).equals(test.clearType) && "pwd01".equals(test.clearPasswd)) {

                    found1 = true;

                } else if ("user01".equals(test.login) && "fxn002".equals(test.clearFunction)
                           && Integer.valueOf(2).equals(test.clearType) && "pwd02".equals(test.clearPasswd)) {

                    found2 = true;

                } else {
                    Log.warning("Unexpected login ", test.login);
                    Log.warning("Unexpected clearFunction ", test.clearFunction);
                    Log.warning("Unexpected clearType ", test.clearType);
                    Log.warning("Unexpected clearPasswd ", test.clearPasswd);
                }
            }

            assertTrue(found1, "user clearance 1 not found");
            assertTrue(found2, "user clearance 2 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all user_clearance rows for login: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final RawUserClearance raw2 = new RawUserClearance("user01", "fxn002", Integer.valueOf(2), "pwd02");

            final boolean result = RawUserClearanceLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawUserClearance> all = RawUserClearanceLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawUserClearance test : all) {

                if ("user01".equals(test.login) && "fxn001".equals(test.clearFunction)
                    && Integer.valueOf(1).equals(test.clearType) && "pwd01".equals(test.clearPasswd)) {

                    found1 = true;

                } else if ("user02".equals(test.login) && "fxn003".equals(test.clearFunction)
                           && Integer.valueOf(3).equals(test.clearType) && "pwd03".equals(test.clearPasswd)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected login ", test.login);
                    Log.warning("Unexpected clearFunction ", test.clearFunction);
                    Log.warning("Unexpected clearType ", test.clearType);
                    Log.warning("Unexpected clearPasswd ", test.clearPasswd);
                }
            }

            assertTrue(found1, "user clearance 1 not found");
            assertTrue(found3, "user clearance 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting user_clearance: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawUserClearanceLogic.getTableName(cache);
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
