package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.old.rawrecord.RawDupRegistr;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
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
 * Tests for the {@code RawDupRegistrLogic} class.
 */
final class TestRawDupRegistrLogic {

    /** A term key used in test records. */
    private static final TermKey sm21 = new TermKey("SM21");

    /** A term key used in test records. */
    private static final TermKey fa21 = new TermKey("FA21");

    /** A float used in test records. */
    private static final Float float15 = Float.valueOf(1.5f);

    /** A float used in test records. */
    private static final Float float2 = Float.valueOf(2.0f);

    /** A date used in test records. */
    private static final LocalDate date1 = LocalDate.of(2021, 1, 2);

    /** A date used in test records. */
    private static final LocalDate date2 = LocalDate.of(2021, 2, 3);

    /** A date used in test records. */
    private static final LocalDate date3 = LocalDate.of(2021, 3, 4);

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawDupRegistrLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawDupRegistr raw1 = new RawDupRegistr(sm21, // term
                    "111111111", // stu_id
                    RawRecordConstants.M117, // course
                    "001", // sect
                    Integer.valueOf(1), // pace_order
                    "N", // open_status
                    "A", // grading_option
                    "Y", // completed
                    Integer.valueOf(62), // score
                    "B", // course_grade
                    "Y", // prereq_satis
                    "Y", // init_class_roll
                    "N", // stu_provided
                    "Y", // final_class_roll
                    "B", // exam_placed
                    Integer.valueOf(1), // zero_unit
                    float15, // timeout_factor
                    null, // forfeit_i
                    "N", // i_in_progress
                    null, // i_counted
                    "C", // ctrl_test
                    null, // deferred_f_dt
                    Integer.valueOf(5), // bypass_timeout
                    "D", // instrn_type
                    "E", // registration_status
                    date1, // last_class_roll_dt
                    null, // i_term / i_term_yr
                    null); // i_deadline_dt

            final RawDupRegistr raw2 = new RawDupRegistr(fa21, // term
                    "111111111", // stu_id
                    RawRecordConstants.M118, // course
                    "001", // sect
                    Integer.valueOf(1), // pace_order
                    "Y", // open_status
                    "F", // grading_option
                    "N", // completed
                    null, // score
                    null, // course_grade
                    "Y", // prereq_satis
                    "N", // init_class_roll
                    "N", // stu_provided
                    "Y", // final_class_roll
                    "G", // exam_placed
                    null, // zero_unit
                    float2, // timeout_factor
                    null, // forfeit_i
                    "N", // i_in_progress
                    null, // i_counted
                    "H", // ctrl_test
                    date2, // deferred_f_dt
                    Integer.valueOf(0), // bypass_timeout
                    "I", // instrn_type
                    "RS", // registration_status
                    date3, // last_class_roll_dt
                    null, // i_term / i_term_yr
                    null); // i_deadline_dt

            assertTrue(RawDupRegistrLogic.insert(cache, raw1), "Failed to insert dup_registr 1");
            assertTrue(RawDupRegistrLogic.insert(cache, raw2), "Failed to insert dup_registr 2");
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
            final List<RawDupRegistr> all = RawDupRegistrLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;

            for (final RawDupRegistr test : all) {
                if (sm21.equals(test.termKey)
                    && "111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "001".equals(test.sect)
                    && Integer.valueOf(1).equals(test.paceOrder)
                    && "N".equals(test.openStatus)
                    && "A".equals(test.gradingOption)
                    && "Y".equals(test.completed)
                    && Integer.valueOf(62).equals(test.score)
                    && "B".equals(test.courseGrade)
                    && "Y".equals(test.prereqSatis)
                    && "Y".equals(test.initClassRoll)
                    && "N".equals(test.stuProvided)
                    && "Y".equals(test.finalClassRoll)
                    && "B".equals(test.examPlaced)
                    && Integer.valueOf(1).equals(test.zeroUnit)
                    && float15.equals(test.timeoutFactor)
                    && test.forfeitI == null
                    && "N".equals(test.iInProgress)
                    && test.iCounted == null
                    && "C".equals(test.ctrlTest)
                    && test.deferredFDt == null
                    && Integer.valueOf(5).equals(test.bypassTimeout)
                    && "D".equals(test.instrnType)
                    && "E".equals(test.registrationStatus)
                    && date1.equals(test.lastClassRollDt)
                    && test.iTermKey == null
                    && test.iDeadlineDt == null) {

                    found1 = true;
                } else if (fa21.equals(test.termKey)
                           && "111111111".equals(test.stuId)
                           && RawRecordConstants.M118.equals(test.course)
                           && "001".equals(test.sect)
                           && Integer.valueOf(1).equals(test.paceOrder)
                           && "Y".equals(test.openStatus)
                           && "F".equals(test.gradingOption)
                           && "N".equals(test.completed)
                           && test.score == null
                           && test.courseGrade == null
                           && "Y".equals(test.prereqSatis)
                           && "N".equals(test.initClassRoll)
                           && "N".equals(test.stuProvided)
                           && "Y".equals(test.finalClassRoll)
                           && "G".equals(test.examPlaced)
                           && test.zeroUnit == null
                           && float2.equals(test.timeoutFactor)
                           && test.forfeitI == null
                           && "N".equals(test.iInProgress)
                           && test.iCounted == null
                           && "H".equals(test.ctrlTest)
                           && date2.equals(test.deferredFDt)
                           && Integer.valueOf(0).equals(test.bypassTimeout)
                           && "I".equals(test.instrnType)
                           && "RS".equals(test.registrationStatus)
                           && date3.equals(test.lastClassRollDt)
                           && test.iTermKey == null
                           && test.iDeadlineDt == null) {

                    found2 = true;
                }
            }

            assertTrue(found1, "dup_registr 1 not found");
            assertTrue(found2, "dup_registr 2 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all dup_registr rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawDupRegistr raw2 = new RawDupRegistr(fa21, // term
                    "111111111", // stu_id
                    RawRecordConstants.M118, // course
                    "001", // sect
                    Integer.valueOf(1), // pace_order
                    "Y", // open_status
                    "F", // grading_option
                    "N", // completed
                    null, // score
                    null, // course_grade
                    "Y", // prereq_satis
                    "N", // init_class_roll
                    "N", // stu_provided
                    "Y", // final_class_roll
                    "G", // exam_placed
                    null, // zero_unit
                    float2, // timeout_factor
                    null, // forfeit_i
                    "N", // i_in_progress
                    null, // i_counted
                    "H", // ctrl_test
                    date2, // deferred_f_dt
                    Integer.valueOf(0), // bypass_timeout
                    "I", // instrn_type
                    "RS", // registration_status
                    date3, // last_class_roll_dt
                    null, // i_term / i_term_yr
                    null); // i_deadline_dt

            final boolean result = RawDupRegistrLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawDupRegistr> all = RawDupRegistrLogic.queryAll(cache);

            assertEquals(1, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;

            for (final RawDupRegistr test : all) {
                if (sm21.equals(test.termKey)
                    && "111111111".equals(test.stuId)
                    && RawRecordConstants.M117.equals(test.course)
                    && "001".equals(test.sect)
                    && Integer.valueOf(1).equals(test.paceOrder)
                    && "N".equals(test.openStatus)
                    && "A".equals(test.gradingOption)
                    && "Y".equals(test.completed)
                    && Integer.valueOf(62).equals(test.score)
                    && "B".equals(test.courseGrade)
                    && "Y".equals(test.prereqSatis)
                    && "Y".equals(test.initClassRoll)
                    && "N".equals(test.stuProvided)
                    && "Y".equals(test.finalClassRoll)
                    && "B".equals(test.examPlaced)
                    && Integer.valueOf(1).equals(test.zeroUnit)
                    && float15.equals(test.timeoutFactor)
                    && test.forfeitI == null
                    && "N".equals(test.iInProgress)
                    && test.iCounted == null
                    && "C".equals(test.ctrlTest)
                    && test.deferredFDt == null
                    && Integer.valueOf(5).equals(test.bypassTimeout)
                    && "D".equals(test.instrnType)
                    && "E".equals(test.registrationStatus)
                    && date1.equals(test.lastClassRollDt)
                    && test.iTermKey == null
                    && test.iDeadlineDt == null) {

                    found1 = true;
                    break;
                }
            }

            assertTrue(found1, "dup_registr 1 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting dup_registr: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("deleteAll results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            assertTrue(RawDupRegistrLogic.deleteAll(cache), "deleteAll returned false");

            final List<RawDupRegistr> all = RawDupRegistrLogic.queryAll(cache);

            assertTrue(all.isEmpty(), "Incorrect record count after deleteAll");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying dup_registr rows by term: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawDupRegistrLogic.getTableName(cache);
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
