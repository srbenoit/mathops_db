package dev.mathops.db.schema.legacy.rec;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.schema.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawCohort} class.
 */
final class TestRawCohort {

    /** A test field value. */
    private static final String TEST_COHORT = "ABCDEF";

    /** A test field value. */
    private static final Integer TEST_SIZE = Integer.valueOf(150);

    /** A test field value. */
    private static final String TEST_INSTRUCTOR = "John Doe";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER1 = String.join(RecBase.DIVIDER,
            "cohort=ABCDEF",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER2 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            "size=150",
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER3 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "instructor=John Doe");

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER4 = String.join(RecBase.DIVIDER,
            "cohort=ABCDEF",
            "size=150",
            "instructor=John Doe");

    /**
     * Constructs a new {@code IvtRawCohort}.
     */
    TestRawCohort() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawCohort obj = new RawCohort(TEST_COHORT, TEST_SIZE, TEST_INSTRUCTOR);

        assertEquals(TEST_COHORT, obj.cohort, "Invalid cohort value after constructor");
        assertEquals(TEST_SIZE, obj.size, "Invalid size value after constructor");
        assertEquals(TEST_INSTRUCTOR, obj.instructor, "Invalid instructor value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0002() {

        final RawCohort obj = new RawCohort(TEST_COHORT, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER1, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0004() {

        final RawCohort obj = new RawCohort(null, TEST_SIZE, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER2, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0006() {

        final RawCohort obj = new RawCohort(null, null, TEST_INSTRUCTOR);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER3, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0008() {

        final RawCohort obj = new RawCohort(TEST_COHORT, TEST_SIZE, TEST_INSTRUCTOR);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER4, ser, "Invalid serialized string");
    }
}
