package dev.mathops.db.schema.legacy.impl;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.TestUtils;
import dev.mathops.db.schema.legacy.rec.RawCalcs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@code RawCalcsLogic} class.
 */
final class TestRawCalcsLogic {

    /** A date used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 11, 12);

    /** A date used in test records. */
    private static final LocalDate date2 = LocalDate.of(2020, 10, 11);

    /** A date used in test records. */
    private static final LocalDate date3 = LocalDate.of(2019, 9, 10);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawCalcsLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawCalcs raw1 = new RawCalcs("111111111", "123A", "RET1234", Long.valueOf(100L), date1);
            final RawCalcs raw2 = new RawCalcs("222222222", "987B", "RET5678", Long.valueOf(200L), date2);
            final RawCalcs raw3 = new RawCalcs("111111111", "AAAA", "BBBBBBB", Long.valueOf(300L), date3);

            assertTrue(RawCalcsLogic.insert(cache, raw1), "Failed to insert calcs");
            assertTrue(RawCalcsLogic.insert(cache, raw2), "Failed to insert calcs");
            assertTrue(RawCalcsLogic.insert(cache, raw3), "Failed to insert calcs");
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
            final List<RawCalcs> all = RawCalcsLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawCalcs r : all) {

                if ("111111111".equals(r.stuId)
                    && "123A".equals(r.issuedNbr)
                    && "RET1234".equals(r.returnNbr)
                    && Long.valueOf(100L).equals(r.serialNbr)
                    && date1.equals(r.examDt)) {

                    found1 = true;

                } else if ("222222222".equals(r.stuId)
                           && "987B".equals(r.issuedNbr)
                           && "RET5678".equals(r.returnNbr)
                           && Long.valueOf(200L).equals(r.serialNbr)
                           && date2.equals(r.examDt)) {

                    found2 = true;

                } else if ("111111111".equals(r.stuId)
                           && "AAAA".equals(r.issuedNbr)
                           && "BBBBBBB".equals(r.returnNbr)
                           && Long.valueOf(300L).equals(r.serialNbr)
                           && date3.equals(r.examDt)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected stuId ", r.stuId);
                    Log.warning("Unexpected issuedNbr ", r.issuedNbr);
                    Log.warning("Unexpected returnNbr ", r.returnNbr);
                    Log.warning("Unexpected serialNbr ", r.serialNbr);
                    Log.warning("Unexpected examDt ", r.examDt);
                }
            }

            assertTrue(found1, "calcs 1 not found");
            assertTrue(found2, "calcs 2 not found");
            assertTrue(found3, "calcs 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all calcs rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryByStudent results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawCalcs> all = RawCalcsLogic.queryByStudent(cache, "111111111");

            assertEquals(2, all.size(), "Incorrect record count from queryByStudent");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawCalcs r : all) {

                if ("111111111".equals(r.stuId)
                    && "123A".equals(r.issuedNbr)
                    && "RET1234".equals(r.returnNbr)
                    && Long.valueOf(100L).equals(r.serialNbr)
                    && date1.equals(r.examDt)) {

                    found1 = true;

                } else if ("111111111".equals(r.stuId)
                           && "AAAA".equals(r.issuedNbr)
                           && "BBBBBBB".equals(r.returnNbr)
                           && Long.valueOf(300L).equals(r.serialNbr)
                           && date3.equals(r.examDt)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected stuId ", r.stuId);
                    Log.warning("Unexpected issuedNbr ", r.issuedNbr);
                    Log.warning("Unexpected returnNbr ", r.returnNbr);
                    Log.warning("Unexpected serialNbr ", r.serialNbr);
                    Log.warning("Unexpected examDt ", r.examDt);
                }
            }

            assertTrue(found1, "calcs 1 not found");
            assertTrue(found3, "calcs 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying calcs by student ID: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryByCalculatorId results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final RawCalcs r = RawCalcsLogic.queryByCalculatorId(cache, "123A");

            assertNotNull(r, "Null result from queryByCalculatorId");

            boolean found = false;

            if ("111111111".equals(r.stuId)
                && "123A".equals(r.issuedNbr)
                && "RET1234".equals(r.returnNbr)
                && Long.valueOf(100L).equals(r.serialNbr)
                && date1.equals(r.examDt)) {

                found = true;

            } else {
                Log.warning("Unexpected stuId ", r.stuId);
                Log.warning("Unexpected issuedNbr ", r.issuedNbr);
                Log.warning("Unexpected returnNbr ", r.returnNbr);
                Log.warning("Unexpected serialNbr ", r.serialNbr);
                Log.warning("Unexpected examDt ", r.examDt);
            }

            assertTrue(found, "calcs 1 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying calcs by calculator ID: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0006() {

        final Cache cache = new Cache(profile);

        try {
            final RawCalcs raw2 = new RawCalcs("222222222", "987B", "RET5678", Long.valueOf(200L), date2);

            final boolean result = RawCalcsLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawCalcs> all = RawCalcsLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawCalcs r : all) {

                if ("111111111".equals(r.stuId)
                    && "123A".equals(r.issuedNbr)
                    && "RET1234".equals(r.returnNbr)
                    && Long.valueOf(100L).equals(r.serialNbr)
                    && date1.equals(r.examDt)) {

                    found1 = true;

                } else if ("111111111".equals(r.stuId)
                           && "AAAA".equals(r.issuedNbr)
                           && "BBBBBBB".equals(r.returnNbr)
                           && Long.valueOf(300L).equals(r.serialNbr)
                           && date3.equals(r.examDt)) {

                    found3 = true;

                } else {
                    Log.warning("Unexpected stuId ", r.stuId);
                    Log.warning("Unexpected issuedNbr ", r.issuedNbr);
                    Log.warning("Unexpected returnNbr ", r.returnNbr);
                    Log.warning("Unexpected serialNbr ", r.serialNbr);
                    Log.warning("Unexpected examDt ", r.examDt);
                }
            }

            assertTrue(found1, "calcs 1 not found");
            assertTrue(found3, "calcs 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting calcs: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawCalcsLogic.getTableName(cache);
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
