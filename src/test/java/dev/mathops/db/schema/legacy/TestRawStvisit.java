package dev.mathops.db.schema.legacy;

import dev.mathops.db.rec.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawStvisit} class.
 */
final class TestRawStvisit {

    /** A field name. */
    private static final LocalDateTime TEST_WHEN_STARTED = LocalDateTime.of(2022, 12, 31, 23, 50, 45);

    /** A field name. */
    private static final LocalDateTime TEST_WHEN_ENDED = LocalDateTime.of(2023, 1, 2, 3, 4, 5);

    /** A field name. */
    private static final String TEST_LOCATION = "TC";

    /** A field name. */
    private static final String TEST_SEAT = "49";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "stu_id=888888888",
            "when_started=2022-12-31T23:50:45",
            "when_ended=2023-01-02T03:04:05",
            "location=TC",
            "seat=49");

    /**
     * Constructs a new {@code IvtRawStvisit}.
     */
    TestRawStvisit() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawStvisit obj = new RawStvisit(RawStudent.TEST_STUDENT_ID, TEST_WHEN_STARTED, TEST_WHEN_ENDED,
                TEST_LOCATION, TEST_SEAT);

        assertEquals(RawStudent.TEST_STUDENT_ID, obj.stuId, "Invalid std_id value after constructor");
        assertEquals(TEST_WHEN_STARTED, obj.whenStarted, "Invalid when_started value after constructor");
        assertEquals(TEST_WHEN_ENDED, obj.whenEnded, "Invalid when_ended value after constructor");
        assertEquals(TEST_LOCATION, obj.location, "Invalid location value after constructor");
        assertEquals(TEST_SEAT, obj.seat, "Invalid seat value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final RawStvisit obj = new RawStvisit(RawStudent.TEST_STUDENT_ID, TEST_WHEN_STARTED, TEST_WHEN_ENDED,
                TEST_LOCATION, TEST_SEAT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
