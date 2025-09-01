package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.schema.legacy.RawMsg;
import dev.mathops.db.type.TermKey;
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
 * Tests for the {@code RawMsgLogic} class.
 */
final class TestRawMsgLogic {

    /** A term key. */
    private static final TermKey fa20 = new TermKey("FA20");

    /** A term key. */
    private static final TermKey fa21 = new TermKey("FA21");

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMsgLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawMsg raw1 = new RawMsg(fa20, "TP1", "MSg01", "Subject1a", "Template1a");
            final RawMsg raw2 = new RawMsg(fa21, "TP1", "MSg01", "Subject1b", "Template1b");
            final RawMsg raw3 = new RawMsg(fa21, "TP2", "MSg02", "Subject2", "Template2");

            assertTrue(RawMsgLogic.insert(cache, raw1), "Failed to insert msg 1");
            assertTrue(RawMsgLogic.insert(cache, raw2), "Failed to insert msg 2");
            assertTrue(RawMsgLogic.insert(cache, raw3), "Failed to insert msg 3");
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
            final List<RawMsg> all = RawMsgLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawMsg r : all) {

                if (fa20.equals(r.termKey)
                    && "TP1".equals(r.touchPoint)
                    && "MSg01".equals(r.msgCode)
                    && "Subject1a".equals(r.subject)
                    && "Template1a".equals(r.template)) {

                    found1 = true;

                } else if (fa21.equals(r.termKey)
                           && "TP1".equals(r.touchPoint)
                           && "MSg01".equals(r.msgCode)
                           && "Subject1b".equals(r.subject)
                           && "Template1b".equals(r.template)) {

                    found2 = true;

                } else if (fa21.equals(r.termKey)
                           && "TP2".equals(r.touchPoint)
                           && "MSg02".equals(r.msgCode)
                           && "Subject2".equals(r.subject)
                           && "Template2".equals(r.template)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected touchPoint ", r.touchPoint);
                    Log.warning("Unexpected msgCode ", r.msgCode);
                    Log.warning("Unexpected subject ", r.subject);
                    Log.warning("Unexpected template ", r.template);
                }
            }

            assertTrue(found1, "msg 1 not found");
            assertTrue(found2, "msg 2 not found");
            assertTrue(found3, "msg 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all msg rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawMsg raw2 = new RawMsg(fa21, "TP1", "MSg01", "Subject1b", "Template1b");

            final boolean result = RawMsgLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawMsg> all = RawMsgLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawMsg r : all) {

                if (fa20.equals(r.termKey)
                    && "TP1".equals(r.touchPoint)
                    && "MSg01".equals(r.msgCode)
                    && "Subject1a".equals(r.subject)
                    && "Template1a".equals(r.template)) {

                    found1 = true;

                } else if (fa21.equals(r.termKey)
                           && "TP2".equals(r.touchPoint)
                           && "MSg02".equals(r.msgCode)
                           && "Subject2".equals(r.subject)
                           && "Template2".equals(r.template)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected termKey ", r.termKey);
                    Log.warning("Unexpected touchPoint ", r.touchPoint);
                    Log.warning("Unexpected msgCode ", r.msgCode);
                    Log.warning("Unexpected subject ", r.subject);
                    Log.warning("Unexpected template ", r.template);
                }
            }

            assertTrue(found1, "msg 1 not found");
            assertTrue(found3, "msg 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting msg: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawMsgLogic.getTableName(cache);
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
