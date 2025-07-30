package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.old.rawrecord.RawGradeRoll;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
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
 * Tests for the {@code RawGradeRollLogic} class.
 */
final class TestRawGradeRollLogic {

    /** A term key. */
    private static final TermKey sm21 = new TermKey("SM21");

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
            final String tableName = RawGradeRollLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawGradeRoll raw1 = new RawGradeRoll(fa21, "111111111", RawRecordConstants.M117, "001",
                    "John Doe", "A");

            final RawGradeRoll raw2 = new RawGradeRoll(fa21, "111111111", RawRecordConstants.M118, "002",
                    "John Doe", "B");

            final RawGradeRoll raw3 = new RawGradeRoll(sm21, "222222222", RawRecordConstants.M124, "003",
                    "Jane Doe", "C");

            assertTrue(RawGradeRollLogic.insert(cache, raw1), "Failed to insert grade_roll 1");
            assertTrue(RawGradeRollLogic.insert(cache, raw2), "Failed to insert grade_roll 2");
            assertTrue(RawGradeRollLogic.insert(cache, raw3), "Failed to insert grade_roll 3");
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
            final List<RawGradeRoll> all = RawGradeRollLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawGradeRoll test : all) {
                if (fa21.equals(test.termKey) && "111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "001".equals(test.sect)
                    && "John Doe".equals(test.fullname)
                    && "A".equals(test.gradeOpt)) {

                    found1 = true;
                } else if (fa21.equals(test.termKey) && "111111111".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && "002".equals(test.sect)
                           && "John Doe".equals(test.fullname)
                           && "B".equals(test.gradeOpt)) {

                    found2 = true;
                } else if (sm21.equals(test.termKey) && "222222222".equals(test.stuId)
                           && RawRecordConstants.M124.equals(test.course)
                           && "003".equals(test.sect)
                           && "Jane Doe".equals(test.fullname)
                           && "C".equals(test.gradeOpt)) {

                    found3 = true;
                }
            }

            assertTrue(found1, "grade_roll 1 not found");
            assertTrue(found2, "grade_roll 2 not found");
            assertTrue(found3, "grade_roll 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all grade_roll rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawGradeRoll raw2 = new RawGradeRoll(fa21, "111111111", RawRecordConstants.M118, "002",
                    "John Doe", "B");

            final boolean result = RawGradeRollLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawGradeRoll> all = RawGradeRollLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawGradeRoll test : all) {
                if (fa21.equals(test.termKey) && "111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "001".equals(test.sect)
                    && "John Doe".equals(test.fullname)
                    && "A".equals(test.gradeOpt)) {

                    found1 = true;
                } else if (sm21.equals(test.termKey) && "222222222".equals(test.stuId)
                           && RawRecordConstants.M124.equals(test.course)
                           && "003".equals(test.sect)
                           && "Jane Doe".equals(test.fullname)
                           && "C".equals(test.gradeOpt)) {

                    found3 = true;
                }
            }

            assertTrue(found1, "grade_roll 1 not found");
            assertTrue(found3, "grade_roll 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting grade_roll: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawGradeRollLogic.getTableName(cache);
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
