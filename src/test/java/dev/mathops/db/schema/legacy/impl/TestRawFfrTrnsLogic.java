package dev.mathops.db.schema.legacy.impl;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.TestUtils;
import dev.mathops.db.schema.legacy.rec.RawFfrTrns;
import dev.mathops.db.schema.RawRecordConstants;
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
 * Tests for the {@code RawFfrTrnsLogic} class.
 */
final class TestRawFfrTrnsLogic {

    /** A date used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 1, 2);

    /** A date used in test records. */
    private static final LocalDate date2 = LocalDate.of(2021, 3, 4);

    /** A date used in test records. */
    private static final LocalDate date3 = LocalDate.of(2021, 5, 6);

    /** A date used in test records. */
    private static final LocalDate date4 = LocalDate.of(2021, 7, 8);

    /** A date used in test records. */
    private static final LocalDate date5 = LocalDate.of(2021, 9, 10);

    /** A date used in test records. */
    private static final LocalDate date6 = LocalDate.of(2021, 11, 12);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawFfrTrnsLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawFfrTrns raw1 = new RawFfrTrns("111111111", RawRecordConstants.M117, "X", date1, date2, "A");
            final RawFfrTrns raw2 = new RawFfrTrns("111111111", RawRecordConstants.M118, "Y", date3, date4, "B");
            final RawFfrTrns raw3 = new RawFfrTrns("222222222", RawRecordConstants.M124, "Z", date5, date6, "C");

            assertTrue(RawFfrTrnsLogic.insert(cache, raw1), "Failed to insert ffr_trns 1");
            assertTrue(RawFfrTrnsLogic.insert(cache, raw2), "Failed to insert ffr_trns 2");
            assertTrue(RawFfrTrnsLogic.insert(cache, raw3), "Failed to insert ffr_trns 3");
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
            final List<RawFfrTrns> all = RawFfrTrnsLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawFfrTrns test : all) {
                if ("111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "X".equals(test.examPlaced)
                    && date1.equals(test.examDt)
                    && date2.equals(test.dtCrRefused)
                    && "A".equals(test.grade)) {

                    found1 = true;
                } else if ("111111111".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && "Y".equals(test.examPlaced)
                           && date3.equals(test.examDt)
                           && date4.equals(test.dtCrRefused)
                           && "B".equals(test.grade)) {

                    found2 = true;
                } else if ("222222222".equals(test.stuId)
                           && RawRecordConstants.M124.equals(test.course)
                           && "Z".equals(test.examPlaced)
                           && date5.equals(test.examDt)
                           && date6.equals(test.dtCrRefused)
                           && "C".equals(test.grade)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected examPlaced ", test.examPlaced);
                    Log.warning("Unexpected examDt ", test.examDt);
                    Log.warning("Unexpected dtCrRefused ", test.dtCrRefused);
                    Log.warning("Unexpected grade ", test.grade);
                }
            }

            assertTrue(found1, "ffr_trns 1 not found");
            assertTrue(found2, "ffr_trns 2 not found");
            assertTrue(found3, "ffr_trns 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all ffr_trns rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryAllByTerm results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawFfrTrns> all = RawFfrTrnsLogic.queryByStudent(cache, "111111111");

            assertEquals(2, all.size(), "Incorrect record count from queryByStudent");

            boolean found1 = false;
            boolean found2 = false;

            for (final RawFfrTrns test : all) {
                if ("111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "X".equals(test.examPlaced)
                    && date1.equals(test.examDt)
                    && date2.equals(test.dtCrRefused)
                    && "A".equals(test.grade)) {

                    found1 = true;
                } else if ("111111111".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && "Y".equals(test.examPlaced)
                           && date3.equals(test.examDt)
                           && date4.equals(test.dtCrRefused)
                           && "B".equals(test.grade)) {

                    found2 = true;
                } else {
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected examPlaced ", test.examPlaced);
                    Log.warning("Unexpected examDt ", test.examDt);
                    Log.warning("Unexpected dtCrRefused ", test.dtCrRefused);
                    Log.warning("Unexpected grade ", test.grade);
                }
            }

            assertTrue(found1, "ffr_trns 1 not found");
            assertTrue(found2, "ffr_trns 2 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all ffr_trns rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final RawFfrTrns raw2 = new RawFfrTrns("111111111", RawRecordConstants.M118, "Y", date3, date4, "B");

            final boolean result = RawFfrTrnsLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawFfrTrns> all = RawFfrTrnsLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawFfrTrns test : all) {
                if ("111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "X".equals(test.examPlaced)
                    && date1.equals(test.examDt)
                    && date2.equals(test.dtCrRefused)
                    && "A".equals(test.grade)) {

                    found1 = true;
                } else if ("222222222".equals(test.stuId)
                           && RawRecordConstants.M124.equals(test.course)
                           && "Z".equals(test.examPlaced)
                           && date5.equals(test.examDt)
                           && date6.equals(test.dtCrRefused)
                           && "C".equals(test.grade)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected examPlaced ", test.examPlaced);
                    Log.warning("Unexpected examDt ", test.examDt);
                    Log.warning("Unexpected dtCrRefused ", test.dtCrRefused);
                    Log.warning("Unexpected grade ", test.grade);
                }
            }

            assertTrue(found1, "ffr_trns 1 not found");
            assertTrue(found3, "ffr_trns 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting ffr_trns: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawFfrTrnsLogic.getTableName(cache);
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
