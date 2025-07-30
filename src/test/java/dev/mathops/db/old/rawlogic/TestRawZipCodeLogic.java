package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.old.TestUtils;
import dev.mathops.db.old.rawrecord.RawZipCode;
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
 * Tests for the {@code RawZipCodeLogic} class.
 */
final class TestRawZipCodeLogic {

    /** The database profile. */
    private static Profile profile = null;

    /** Initialize the test class. */
    @BeforeAll
    static void initTests() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        profile = cache.getProfile();

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawZipCodeLogic.getTableName(cache);
            stmt.executeUpdate("DELETE FROM " + tableName);
            conn.commit();

            final RawZipCode raw1 = new RawZipCode("80111", "City1", "AA");
            final RawZipCode raw2 = new RawZipCode("80222", "City2", "BB");
            final RawZipCode raw3 = new RawZipCode("80333", "City3", "CC");

            assertTrue(RawZipCodeLogic.insert(cache, raw1), "Failed to insert zip code 1");
            assertTrue(RawZipCodeLogic.insert(cache, raw2), "Failed to insert zip code 2");
            assertTrue(RawZipCodeLogic.insert(cache, raw3), "Failed to insert zip code 3");
        } catch (final SQLException ex) {
            throw new IllegalArgumentException(ex);
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
            final List<RawZipCode> all = RawZipCodeLogic.queryAll(cache);

            assertEquals(3, all.size(), "Incorrect record count from queryAll");

            boolean found1 = false;
            boolean found2 = false;
            boolean found3 = false;

            for (final RawZipCode test : all) {

                if ("80111".equals(test.zipCode) && "City1".equals(test.city) && "AA".equals(test.state)) {
                    found1 = true;
                } else if ("80222".equals(test.zipCode) && "City2".equals(test.city) && "BB".equals(test.state)) {
                    found2 = true;
                } else if ("80333".equals(test.zipCode) && "City3".equals(test.city) && "CC".equals(test.state)) {
                    found3 = true;
                } else {
                    Log.warning("Unexpected zipCode ", test.zipCode);
                    Log.warning("Unexpected test.city ", test.city);
                    Log.warning("Unexpected state ", test.state);
                }
            }

            assertTrue(found1, "ZipCode 1 not found");
            assertTrue(found2, "ZipCode 2 not found");
            assertTrue(found3, "ZipCode 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while querying all zip_code rows: " + ex.getMessage());
        }
    }

    /** Test case. */
    @Test
    @DisplayName("delete results were correct")
    void test0004() {

        final Cache cache = new Cache(profile);

        try {
            final RawZipCode raw2 = new RawZipCode("80222", "City2", "BB");

            final boolean result = RawZipCodeLogic.delete(cache, raw2);
            assertTrue(result, "delete returned false");

            final List<RawZipCode> all = RawZipCodeLogic.queryAll(cache);

            assertEquals(2, all.size(), "Incorrect record count from queryAll after delete");

            boolean found1 = false;
            boolean found3 = false;

            for (final RawZipCode test : all) {
                if ("80111".equals(test.zipCode) && "City1".equals(test.city) && "AA".equals(test.state)) {
                    found1 = true;
                } else if ("80333".equals(test.zipCode) && "City3".equals(test.city) && "CC".equals(test.state)) {
                    found3 = true;
                } else {
                    Log.warning("Unexpected zipCode ", test.zipCode);
                    Log.warning("Unexpected test.city ", test.city);
                    Log.warning("Unexpected state ", test.state);
                }
            }

            assertTrue(found1, "ZipCode 1 not found");
            assertTrue(found3, "ZipCode 3 not found");
        } catch (final SQLException ex) {
            Log.warning(ex);
            fail("Exception while deleting zip_code: " + ex.getMessage());
        }
    }

    /** Clean up. */
    @AfterAll
    static void cleanUp() {

        final Cache cache = TestUtils.ensureConnectedToTest();
        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try (final Statement stmt = conn.createStatement()) {
            final String tableName = RawZipCodeLogic.getTableName(cache);
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
