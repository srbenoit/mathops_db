package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.enums.ETermName;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.schema.legacy.RawStpaceSummary;
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
 * Tests for the {@code RawStpaceSummaryLogic} class.
 */
final class TestRawStpaceSummaryLogic {

    /** A term key used in test records. */
    private static final TermKey SP21 = new TermKey(ETermName.SPRING, 2021);

    /** A term key used in test records. */
    private static final TermKey FA23 = new TermKey(ETermName.FALL, 2023);

    /** A term key used in test records. */
    private static final TermKey SM22 = new TermKey(ETermName.SUMMER, 2022);

    /** A date used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 1, 4);

    /** A date used in test records. */
    private static final LocalDate date2 = LocalDate.of(2021, 1, 6);

    /** A date used in test records. */
    private static final LocalDate date3 = LocalDate.of(2022, 6, 7);

    /** A date used in test records. */
    private static final LocalDate date4 = LocalDate.of(2022, 6, 8);

    /** A date used in test records. */
    private static final LocalDate date5 = LocalDate.of(2023, 9, 10);

    /** A date used in test records. */
    private static final LocalDate date6 = LocalDate.of(2023, 10, 11);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawStpaceSummaryLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawStpaceSummary raw1 = new RawStpaceSummary(SP21, "111111111", "M 117", "001", "N",
                    Integer.valueOf(5), "A", Integer.valueOf(4), Integer.valueOf(541), Integer.valueOf(1), date1,
                    "Y", date2, Integer.valueOf(2));

            final RawStpaceSummary raw2 = new RawStpaceSummary(SM22, "222222222", "M 118", "002", "Y",
                    Integer.valueOf(4), "B", Integer.valueOf(3), Integer.valueOf(432), Integer.valueOf(2), date3,
                    "N", date4, Integer.valueOf(5));

            final RawStpaceSummary raw3 = new RawStpaceSummary(FA23, "333333333", "M 124", "003", "A",
                    Integer.valueOf(3), "C", Integer.valueOf(2), Integer.valueOf(321), Integer.valueOf(1), date5,
                    "D", date6, Integer.valueOf(9));

            assertTrue(RawStpaceSummaryLogic.insert(cache, raw1), "Failed to insert stpace_summary 1");
            assertTrue(RawStpaceSummaryLogic.insert(cache, raw2), "Failed to insert stpace_summary 2");
            assertTrue(RawStpaceSummaryLogic.insert(cache, raw3), "Failed to insert stpace_summary 3");
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
            final List<RawStpaceSummary> all = RawStpaceSummaryLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawStpaceSummary test : all) {
                if (SP21.equals(test.termKey)
                    && "111111111".equals(test.stuId)
                    && "M 117".equals(test.course)
                    && "001".equals(test.sect)
                    && "N".equals(test.iInProgress)
                    && Integer.valueOf(5).equals(test.pace)
                    && "A".equals(test.paceTrack)
                    && Integer.valueOf(4).equals(test.paceOrder)
                    && Integer.valueOf(541).equals(test.msNbr)
                    && Integer.valueOf(1).equals(test.msUnit)
                    && date1.equals(test.msDate)
                    && "Y".equals(test.newMsDate)
                    && date2.equals(test.examDt)
                    && Integer.valueOf(2).equals(test.rePoints)) {

                    found1 = true;
                } else if (SM22.equals(test.termKey)
                           && "222222222".equals(test.stuId)
                           && "M 118".equals(test.course)
                           && "002".equals(test.sect)
                           && "Y".equals(test.iInProgress)
                           && Integer.valueOf(4).equals(test.pace)
                           && "B".equals(test.paceTrack)
                           && Integer.valueOf(3).equals(test.paceOrder)
                           && Integer.valueOf(432).equals(test.msNbr)
                           && Integer.valueOf(2).equals(test.msUnit)
                           && date3.equals(test.msDate)
                           && "N".equals(test.newMsDate)
                           && date4.equals(test.examDt)
                           && Integer.valueOf(5).equals(test.rePoints)) {

                    found2 = true;
                } else if (FA23.equals(test.termKey)
                           && "333333333".equals(test.stuId)
                           && "M 124".equals(test.course)
                           && "003".equals(test.sect)
                           && "A".equals(test.iInProgress)
                           && Integer.valueOf(3).equals(test.pace)
                           && "C".equals(test.paceTrack)
                           && Integer.valueOf(2).equals(test.paceOrder)
                           && Integer.valueOf(321).equals(test.msNbr)
                           && Integer.valueOf(1).equals(test.msUnit)
                           && date5.equals(test.msDate)
                           && "D".equals(test.newMsDate)
                           && date6.equals(test.examDt)
                           && Integer.valueOf(9).equals(test.rePoints)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected termKey ", test.termKey);
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected sect ", test.sect);
                    Log.warning("Unexpected iInProgress ", test.iInProgress);
                    Log.warning("Unexpected pace ", test.pace);
                    Log.warning("Unexpected paceTrack ", test.paceTrack);
                    Log.warning("Unexpected paceOrder ", test.paceOrder);
                    Log.warning("Unexpected msNbr ", test.msNbr);
                    Log.warning("Unexpected msUnit ", test.msUnit);
                    Log.warning("Unexpected msDate ", test.msDate);
                    Log.warning("Unexpected newMsDate ", test.newMsDate);
                    Log.warning("Unexpected examDt ", test.examDt);
                    Log.warning("Unexpected rePoints ", test.rePoints);
                }
            }

            assertTrue(found1, "Stpace_summary 1 not found");
            assertTrue(found2, "Stpace_summary 2 not found");
            assertTrue(found3, "Stpace_summary 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all stpace_summary rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawStpaceSummary raw2 = new RawStpaceSummary(SM22, "222222222", "M 118", "002", "Y",
                    Integer.valueOf(4), "B", Integer.valueOf(3), Integer.valueOf(432), Integer.valueOf(2), date3,
                    "N", date4, Integer.valueOf(5));

            final boolean result = RawStpaceSummaryLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawStpaceSummary> all = RawStpaceSummaryLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawStpaceSummary test : all) {
                if (SP21.equals(test.termKey)
                    && "111111111".equals(test.stuId)
                    && "M 117".equals(test.course)
                    && "001".equals(test.sect)
                    && "N".equals(test.iInProgress)
                    && Integer.valueOf(5).equals(test.pace)
                    && "A".equals(test.paceTrack)
                    && Integer.valueOf(4).equals(test.paceOrder)
                    && Integer.valueOf(541).equals(test.msNbr)
                    && Integer.valueOf(1).equals(test.msUnit)
                    && date1.equals(test.msDate)
                    && "Y".equals(test.newMsDate)
                    && date2.equals(test.examDt)
                    && Integer.valueOf(2).equals(test.rePoints)) {

                    found1 = true;
                } else if (FA23.equals(test.termKey)
                           && "333333333".equals(test.stuId)
                           && "M 124".equals(test.course)
                           && "003".equals(test.sect)
                           && "A".equals(test.iInProgress)
                           && Integer.valueOf(3).equals(test.pace)
                           && "C".equals(test.paceTrack)
                           && Integer.valueOf(2).equals(test.paceOrder)
                           && Integer.valueOf(321).equals(test.msNbr)
                           && Integer.valueOf(1).equals(test.msUnit)
                           && date5.equals(test.msDate)
                           && "D".equals(test.newMsDate)
                           && date6.equals(test.examDt)
                           && Integer.valueOf(9).equals(test.rePoints)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected termKey ", test.termKey);
                    Log.warning("Unexpected stuId ", test.stuId);
                    Log.warning("Unexpected course ", test.course);
                    Log.warning("Unexpected sect ", test.sect);
                    Log.warning("Unexpected iInProgress ", test.iInProgress);
                    Log.warning("Unexpected pace ", test.pace);
                    Log.warning("Unexpected paceTrack ", test.paceTrack);
                    Log.warning("Unexpected paceOrder ", test.paceOrder);
                    Log.warning("Unexpected msNbr ", test.msNbr);
                    Log.warning("Unexpected msUnit ", test.msUnit);
                    Log.warning("Unexpected msDate ", test.msDate);
                    Log.warning("Unexpected newMsDate ", test.newMsDate);
                    Log.warning("Unexpected examDt ", test.examDt);
                    Log.warning("Unexpected rePoints ", test.rePoints);
                }
            }

            assertTrue(found1, "Stpace_summary 1 not found");
            assertTrue(found3, "Stpace_summary 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting stpace_summary: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawStpaceSummaryLogic.getTableName(cache);
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
