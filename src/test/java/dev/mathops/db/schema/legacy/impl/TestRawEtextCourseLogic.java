package dev.mathops.db.schema.legacy.impl;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.TestUtils;
import dev.mathops.db.schema.legacy.rec.RawEtextCourse;
import dev.mathops.db.schema.RawRecordConstants;
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
 * Tests for the {@code RawEtextCourseLogic} class.
 */
final class TestRawEtextCourseLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawEtextCourseLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawEtextCourse raw1 = new RawEtextCourse("PACE", RawRecordConstants.M117);
            final RawEtextCourse raw2 = new RawEtextCourse("PACE", RawRecordConstants.M118);
            final RawEtextCourse raw3 = new RawEtextCourse("ET117", RawRecordConstants.M117);

            assertTrue(RawEtextCourseLogic.insert(cache, raw1), "Failed to insert etext_course");
            assertTrue(RawEtextCourseLogic.insert(cache, raw2), "Failed to insert etext_course");
            assertTrue(RawEtextCourseLogic.insert(cache, raw3), "Failed to insert etext_course");
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
            final List<RawEtextCourse> all = RawEtextCourseLogic.queryAll(cache);

            assertEquals(3, all.size(), //
                    "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawEtextCourse r : all) {

                if ("PACE".equals(r.etextId)
                    && RawRecordConstants.M117.equals(r.course)) {

                    found1 = true;
                } else if ("PACE".equals(r.etextId)
                           && RawRecordConstants.M118.equals(r.course)) {

                    found2 = true;
                } else if ("ET117".equals(r.etextId)
                           && RawRecordConstants.M117.equals(r.course)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected etextId ", r.etextId);
                    Log.warning("Unexpected course ", r.course);
                }
            }

            assertTrue(found1, "etext_course 1 not found");
            assertTrue(found2, "etext_course 2 not found");
            assertTrue(found3, "etext_course 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all etext_course rows: "
                 + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryByEtext results")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawEtextCourse> all = RawEtextCourseLogic.queryByEtext(cache, "PACE");

            assertEquals(2, all.size(), "Incorrect record count from queryByEtext");

            boolean found1 = false;
            boolean found2 = false;

            for (final RawEtextCourse r : all) {

                if ("PACE".equals(r.etextId) && RawRecordConstants.M117.equals(r.course)) {

                    found1 = true;
                } else if ("PACE".equals(r.etextId) && RawRecordConstants.M118.equals(r.course)) {

                    found2 = true;
                } else {
                    Log.warning("Unexpected etextId ", r.etextId);
                    Log.warning("Unexpected course ", r.course);
                }
            }

            assertTrue(found1, "etext_course 1 not found");
            assertTrue(found2, "etext_course 2 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying etext_course by etext: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("queryByCourse results")
    void test0005() {

        final Cache cache = new Cache(profile);

        try {
            final List<RawEtextCourse> all = RawEtextCourseLogic.queryByCourse(cache, RawRecordConstants.M117);

            assertEquals(2, all.size(), "Incorrect record count from queryByCourse");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawEtextCourse r : all) {

                if ("PACE".equals(r.etextId) && RawRecordConstants.M117.equals(r.course)) {

                    found1 = true;
                } else if ("ET117".equals(r.etextId) && RawRecordConstants.M117.equals(r.course)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected etextId ", r.etextId);
                    Log.warning("Unexpected course ", r.course);
                }
            }

            assertTrue(found1, "etext_course 1 not found");
            assertTrue(found3, "etext_course 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying etext_course by course: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results")
    void test0006() {

        final Cache cache = new Cache(profile);

        try {
            final RawEtextCourse raw2 = new RawEtextCourse("PACE", RawRecordConstants.M118);

            final boolean result = RawEtextCourseLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawEtextCourse> all = RawEtextCourseLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawEtextCourse r : all) {

                if ("PACE".equals(r.etextId) && RawRecordConstants.M117.equals(r.course)) {

                    found1 = true;
                } else if ("ET117".equals(r.etextId) && RawRecordConstants.M117.equals(r.course)) {

                    found3 = true;
                } else {
                    Log.warning("Unexpected etextId ", r.etextId);
                    Log.warning("Unexpected course ", r.course);
                }
            }

            assertTrue(found1, "etext_course 1 not found");
            assertTrue(found3, "etext_course 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting etext_course: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawEtextCourseLogic.getTableName(cache);
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
