package dev.mathops.db.reclogic.term;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.EDbUse;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Facet;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.rec.term.StandardAssignmentAttemptQaRec;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@code StandardAssignmentAttemptQaLogic} class.
 */
final class TestStandardAssignmentAttemptQaLogic {

    /** A raw test record. */
    private static final StandardAssignmentAttemptQaRec RAW1 =
            new StandardAssignmentAttemptQaRec(Integer.valueOf(111222330), Integer.valueOf(1), Integer.valueOf(2),
                    "Item-001");

    /** A raw test record. */
    private static final StandardAssignmentAttemptQaRec RAW2 =
            new StandardAssignmentAttemptQaRec(Integer.valueOf(111222330), Integer.valueOf(2), Integer.valueOf(3),
                    "Item-002");

    /** A raw test record. */
    private static final StandardAssignmentAttemptQaRec RAW3 =
            new StandardAssignmentAttemptQaRec(Integer.valueOf(111222331), Integer.valueOf(1), Integer.valueOf(5),
                    "Item-003");

    /** A raw test record. */
    private static final StandardAssignmentAttemptQaRec UPD3 =
            new StandardAssignmentAttemptQaRec(Integer.valueOf(111222331), Integer.valueOf(1), Integer.valueOf(6),
                    "Item-004");

    /** The database profile. */
    static Profile profile;

    /** The database login. */
    static Login login;

    /**
     * Prints an indication of an unexpected record.
     *
     * @param r the unexpected record
     */
    private static void printUnexpected(final StandardAssignmentAttemptQaRec r) {

        Log.warning("Unexpected serial number ", r.serialNbr);
        Log.warning("Unexpected question number ", r.questionNbr);
        Log.warning("Unexpected points ", r.points);
        Log.warning("Unexpected item ID ", r.itemId);
    }

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        profile = config.getCodeProfile(Contexts.POSTGRES_TEST_PATH);
        if (profile == null) {
            throw new IllegalArgumentException(TestRes.get(TestRes.ERR_NO_PGTEST_PROFILE));
        }
        login = profile.getLogin(ESchema.LEGACY);
        if (login == null) {
            throw new IllegalArgumentException(TestRes.get(TestRes.ERR_NO_PGPRIMARY_CONTEXT));
        }

        // Make sure the connection is accessing the TEST database
        final Facet facet = profile.getFacet(ESchema.LEGACY);
        if (facet.data.use != EDbUse.TEST) {
            throw new IllegalArgumentException(TestRes.fmt(TestRes.ERR_NOT_CONNECTED_TO_TEST, facet.data.use));
        }

        final Cache cache = new Cache(profile);
        final String prefix = cache.getSchemaPrefix(ESchema.TERM);
        if (prefix == null) {
            fail(TestRes.get(TestRes.ERR_NO_TERM_PREFIX));
        }

        final DbConnection conn = login.checkOutConnection();

        final String sql = "SELECT descr FROM " + prefix + ".which_db";

        try {
            try (final Statement stmt = conn.createStatement();
                 final ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    final String which = rs.getString(1);
                    if (which == null || !"TEST".equals(which.trim())) {
                        throw new IllegalArgumentException(TestRes.fmt(TestRes.ERR_NOT_CONNECTED_TO_TEST, which));
                    }
                } else {
                    throw new IllegalArgumentException(TestRes.get(TestRes.ERR_CANT_QUERY_WHICH_DB));
                }
            }

            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM " + prefix + ".standard_assignment_attempt_qa");
            }
            conn.commit();

            assertTrue(StandardAssignmentAttemptQaLogic.INSTANCE.insert(cache, RAW1),
                    "Failed to insert standard assignment attempt qa");
            assertTrue(StandardAssignmentAttemptQaLogic.INSTANCE.insert(cache, RAW2),
                    "Failed to insert standard assignment attempt qa");
            assertTrue(StandardAssignmentAttemptQaLogic.INSTANCE.insert(cache, RAW3),
                    "Failed to insert standard assignment attempt qa");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while initializing 'standard_assignment_attempt_qa' table: " + ex.getMessage());
            throw new IllegalArgumentException(ex);
        } finally {
            login.checkInConnection(conn);
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryAll results")
    void test0001() {
        final Cache cache = new Cache(profile);

        try {
            final List<StandardAssignmentAttemptQaRec> all = StandardAssignmentAttemptQaLogic.INSTANCE.queryAll(
                    cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final StandardAssignmentAttemptQaRec r : all) {
                if (RAW1.equals(r)) {
                    found1 = true;
                } else if (RAW2.equals(r)) {
                    found2 = true;
                } else if (RAW3.equals(r)) {
                    found3 = true;
                } else {
                    printUnexpected(r);
                    fail("Extra record found");
                }
            }

            assertTrue(found1, "standard_assignment_attempt_qa 1 not found");
            assertTrue(found2, "standard_assignment_attempt_qa 2 not found");
            assertTrue(found3, "standard_assignment_attempt_qa 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all 'standard_assignment_attempt_qa' rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("query results")
    void test0002() {
        final Cache cache = new Cache(profile);

        try {
            final StandardAssignmentAttemptQaRec r = StandardAssignmentAttemptQaLogic.INSTANCE.query(cache,
                    RAW1.serialNbr, RAW1.questionNbr);

            assertNotNull(r, "No record returned by query");

            if (!RAW1.equals(r)) {
                printUnexpected(r);
                fail("Extra record found");
            }
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying standard_assignment_attempt_qa: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("update results")
    void test0003() {
        final Cache cache = new Cache(profile);

        try {
            if (StandardAssignmentAttemptQaLogic.INSTANCE.update(cache, UPD3)) {
                final StandardAssignmentAttemptQaRec r = StandardAssignmentAttemptQaLogic.INSTANCE.query(cache,
                        UPD3.serialNbr, UPD3.questionNbr);

                assertNotNull(r, "No record returned by query after update");

                if (!UPD3.equals(r)) {
                    printUnexpected(r);
                    fail("Incorrect results after update of standard_assignment_attempt_qa");
                }
            } else {
                fail("Failed to update standard_assignment_attempt_qa row");
            }
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while updating standard_assignment_attempt_qa: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {
        final Cache cache = new Cache(profile);

        try {
            final boolean result = StandardAssignmentAttemptQaLogic.INSTANCE.delete(cache, RAW2);
            assertTrue(result, "delete returned false");

            final List<StandardAssignmentAttemptQaRec> all = StandardAssignmentAttemptQaLogic.INSTANCE.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final StandardAssignmentAttemptQaRec r : all) {
                if (RAW1.equals(r)) {
                    found1 = true;
                } else if (UPD3.equals(r)) {
                    found3 = true;
                } else {
                    printUnexpected(r);
                    fail("Extra record found");
                }
            }

            assertTrue(found1, "standard_assignment_attempt_qa 1 not found");
            assertTrue(found3, "standard_assignment_attempt_qa 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting standard_assignment_attempt_qas: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = new Cache(profile);
        final String prefix = cache.getSchemaPrefix(ESchema.TERM);
        if (prefix == null) {
            fail(TestRes.get(TestRes.ERR_NO_TERM_PREFIX));
        }

        try {
            final DbConnection conn = login.checkOutConnection();

            final String sql = "SELECT descr FROM " + prefix + ".which_db";

            try {
                try (final Statement stmt = conn.createStatement();
                     final ResultSet rs = stmt.executeQuery(sql)) {

                    if (rs.next()) {
                        final String which = rs.getString(1);
                        if (which == null || !"TEST".equals(which.trim())) {
                            throw new IllegalArgumentException(
                                    TestRes.fmt(TestRes.ERR_NOT_CONNECTED_TO_TEST, which));
                        }
                    } else {
                        throw new IllegalArgumentException(TestRes.get(TestRes.ERR_CANT_QUERY_WHICH_DB));
                    }
                }

                try (final Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM " + prefix + ".standard_assignment_attempt_qa");
                }

                conn.commit();
            } finally {
                login.checkInConnection(conn);
            }
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while cleaning tables: " + ex.getMessage());
        }
    }
}
