package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.enums.ETermName;
import dev.mathops.db.old.rawrecord.RawCusection;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.type.TermKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@code RawCusectionLogic} class.
 */
final class TestRawCusectionLogic {

    /** A term key. */
    private static final TermKey fa21 = new TermKey(ETermName.FALL, 2021);

    /** A term key used in test records. */
    private static final TermKey fa20 = new TermKey(ETermName.FALL, 2020);

    /** A date  used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 1, 2);

    /** A date  used in test records. */
    private static final LocalDate date2 = LocalDate.of(2021, 3, 4);

    /** A date  used in test records. */
    private static final LocalDate date3 = LocalDate.of(2021, 4, 3);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        profile = config.getCodeProfile(Contexts.INFORMIX_TEST_PATH);
        if (profile == null) {
            throw new IllegalArgumentException(TestRes.get(TestRes.ERR_NO_TEST_PROFILE));
        }

        final Login login = profile.getLogin(ESchema.LEGACY);
        final DbConnection conn = login.checkOutConnection();
        final Cache cache = new Cache(profile);

        final String whichDbName = RawWhichDbLogic.getTableName(cache);

        // Make sure we're in the TEST database
        try {
            try (final Statement stmt = conn.createStatement();
                 final ResultSet rs = stmt.executeQuery("SELECT descr FROM " + whichDbName)) {

                if (rs.next()) {
                    final String which = rs.getString(1);
                    if (which != null && !"TEST".equals(which.trim())) {
                        throw new IllegalArgumentException(TestRes.fmt(TestRes.ERR_NOT_CONNECTED_TO_TEST, which));
                    }
                } else {
                    throw new IllegalArgumentException(TestRes.get(TestRes.ERR_CANT_QUERY_WHICH_DB));
                }
            }

            try (final Statement stmt = conn.createStatement()) {
                final String tableName = RawCusectionLogic.getTableName(cache);
                stmt.executeUpdate("DELETE FROM " + tableName);
            }
            conn.commit();

            // 117/001 unit 1 in FA21
            final RawCusection raw1 = new RawCusection(fa21, RawRecordConstants.M117, "001", Integer.valueOf(1),
                    Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(4),
                    Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2), date1, date2, Integer.valueOf(100),
                    Integer.valueOf(200), Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            // 117/001 unit 2 in FA21
            final RawCusection raw2 = new RawCusection(fa21, RawRecordConstants.M117, "001", Integer.valueOf(2),
                    Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(4),
                    Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2), date1, date2, Integer.valueOf(100),
                    Integer.valueOf(200), Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            // 117/001 unit 3 in FA20
            final RawCusection raw3 = new RawCusection(fa20, RawRecordConstants.M117, "001", Integer.valueOf(3),
                    Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(4),
                    Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2), date1, date2, Integer.valueOf(100),
                    Integer.valueOf(200), Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            // 117/002 unit 3 in FA21
            final RawCusection raw4 = new RawCusection(fa21, RawRecordConstants.M117, "002", Integer.valueOf(3),
                    Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(4),
                    Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2), date1, date2, Integer.valueOf(100),
                    Integer.valueOf(200), Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            // 118/001 unit 4 in FA21
            final RawCusection raw5 = new RawCusection(fa21, RawRecordConstants.M118, "001", Integer.valueOf(4),
                    Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(4),
                    Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2), date1, date2, Integer.valueOf(100),
                    Integer.valueOf(200), Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            assertTrue(RawCusectionLogic.insert(cache, raw1), "Failed to insert cusection 1");
            assertTrue(RawCusectionLogic.insert(cache, raw2), "Failed to insert cusection 2");
            assertTrue(RawCusectionLogic.insert(cache, raw3), "Failed to insert cusection 3");
            assertTrue(RawCusectionLogic.insert(cache, raw4), "Failed to insert cusection 4");
            assertTrue(RawCusectionLogic.insert(cache, raw5), "Failed to insert cusection 5");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while initializing tables: " + ex.getMessage());
        } finally {
            login.checkInConnection(conn);
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryAll results")
    void test0003() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawCusection> all = RawCusectionLogic.queryAll(cache);

            assertEquals(5, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;
            boolean found4 = false;
            boolean found5 = false;

            for (final RawCusection r : all) {

                if (fa21.equals(r.termKey)
                    && RawRecordConstants.M117.equals(r.course)
                    && "001".equals(r.sect)
                    && Integer.valueOf(1).equals(r.unit)
                    && Integer.valueOf(100).equals(r.timeout)
                    && Integer.valueOf(7).equals(r.reMasteryScore)
                    && Integer.valueOf(8).equals(r.ueMasteryScore)
                    && Integer.valueOf(4).equals(r.hwMasteryScore)
                    && Integer.valueOf(1).equals(r.hwMoveonScore)
                    && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                    && Integer.valueOf(2).equals(r.atmptsPerReview)
                    && date1.equals(r.firstTestDt)
                    && date2.equals(r.lastTestDt)
                    && Integer.valueOf(100).equals(r.beginTestPeriod)
                    && Integer.valueOf(200).equals(r.endTestPeriod)
                    && Integer.valueOf(5).equals(r.couponCost)
                    && date3.equals(r.lastCouponDt)
                    && "Y".equals(r.showTestWindow)
                    && "N".equals(r.unproctoredExam)
                    && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found1 = true;
                } else if (fa21.equals(r.termKey)
                           && RawRecordConstants.M117.equals(r.course)
                           && "001".equals(r.sect)
                           && Integer.valueOf(2).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found2 = true;
                } else if (fa20.equals(r.termKey)
                           && RawRecordConstants.M117.equals(r.course)
                           && "001".equals(r.sect)
                           && Integer.valueOf(3).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found3 = true;
                } else if (fa21.equals(r.termKey)
                           && RawRecordConstants.M117.equals(r.course)
                           && "002".equals(r.sect)
                           && Integer.valueOf(3).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found4 = true;
                } else if (fa21.equals(r.termKey)
                           && RawRecordConstants.M118.equals(r.course)
                           && "001".equals(r.sect)
                           && Integer.valueOf(4).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found5 = true;
                }
            }

            assertTrue(found1, "cusection 1 not found");
            assertTrue(found2, "cusection 2 not found");
            assertTrue(found3, "cusection 3 not found");
            assertTrue(found4, "cusection 4 not found");
            assertTrue(found5, "cusection 5 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all cusection rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0006() {

        final Cache cache = new Cache(profile);

        try {
            final RawCusection raw2 = new RawCusection(fa21, RawRecordConstants.M117, "001",
                    Integer.valueOf(2), Integer.valueOf(100), Integer.valueOf(7), Integer.valueOf(8),
                    Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(90), Integer.valueOf(2),
                    date1, date2, Integer.valueOf(100), Integer.valueOf(200),
                    Integer.valueOf(5), date3, "Y", "N", Integer.valueOf(3));

            final boolean result = RawCusectionLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawCusection> all = RawCusectionLogic.queryAll(cache);

            assertEquals(4, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;
            boolean found4 = false;
            boolean found5 = false;

            for (final RawCusection r : all) {

                if (fa21.equals(r.termKey)
                    && RawRecordConstants.M117.equals(r.course)
                    && "001".equals(r.sect)
                    && Integer.valueOf(1).equals(r.unit)
                    && Integer.valueOf(100).equals(r.timeout)
                    && Integer.valueOf(7).equals(r.reMasteryScore)
                    && Integer.valueOf(8).equals(r.ueMasteryScore)
                    && Integer.valueOf(4).equals(r.hwMasteryScore)
                    && Integer.valueOf(1).equals(r.hwMoveonScore)
                    && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                    && Integer.valueOf(2).equals(r.atmptsPerReview)
                    && date1.equals(r.firstTestDt)
                    && date2.equals(r.lastTestDt)
                    && Integer.valueOf(100).equals(r.beginTestPeriod)
                    && Integer.valueOf(200).equals(r.endTestPeriod)
                    && Integer.valueOf(5).equals(r.couponCost)
                    && date3.equals(r.lastCouponDt)
                    && "Y".equals(r.showTestWindow)
                    && "N".equals(r.unproctoredExam)
                    && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found1 = true;
                } else if (fa20.equals(r.termKey)
                           && RawRecordConstants.M117.equals(r.course)
                           && "001".equals(r.sect)
                           && Integer.valueOf(3).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found3 = true;
                } else if (fa21.equals(r.termKey)
                           && RawRecordConstants.M117.equals(r.course)
                           && "002".equals(r.sect)
                           && Integer.valueOf(3).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found4 = true;
                } else if (fa21.equals(r.termKey)
                           && RawRecordConstants.M118.equals(r.course)
                           && "001".equals(r.sect)
                           && Integer.valueOf(4).equals(r.unit)
                           && Integer.valueOf(100).equals(r.timeout)
                           && Integer.valueOf(7).equals(r.reMasteryScore)
                           && Integer.valueOf(8).equals(r.ueMasteryScore)
                           && Integer.valueOf(4).equals(r.hwMasteryScore)
                           && Integer.valueOf(1).equals(r.hwMoveonScore)
                           && Integer.valueOf(90).equals(r.nbrAtmptsAllow)
                           && Integer.valueOf(2).equals(r.atmptsPerReview)
                           && date1.equals(r.firstTestDt)
                           && date2.equals(r.lastTestDt)
                           && Integer.valueOf(100).equals(r.beginTestPeriod)
                           && Integer.valueOf(200).equals(r.endTestPeriod)
                           && Integer.valueOf(5).equals(r.couponCost)
                           && date3.equals(r.lastCouponDt)
                           && "Y".equals(r.showTestWindow)
                           && "N".equals(r.unproctoredExam)
                           && Integer.valueOf(3).equals(r.rePointsOntime)) {

                    found5 = true;
                }
            }

            assertTrue(found1, "cusection 1 not found");
            assertTrue(found3, "cusection 3 not found");
            assertTrue(found4, "cusection 4 not found");
            assertTrue(found5, "cusection 5 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting cusection: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Login login = profile.getLogin(ESchema.LEGACY);
        final DbConnection conn = login.checkOutConnection();
        final Cache cache = new Cache(profile);

        try {
            try (final Statement stmt = conn.createStatement()) {
                final String tableName = RawCusectionLogic.getTableName(cache);
                stmt.executeUpdate("DELETE FROM " + tableName);
            }

            conn.commit();
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while cleaning tables: " + ex.getMessage());
        } finally {
            login.checkInConnection(conn);
        }
    }
}
