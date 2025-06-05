package dev.mathops.db.old.rawrecord;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.old.rawrecord.RawPlcFee;
import dev.mathops.db.rec.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawPlcFee} class.
 */
final class TestRawPlcFee {

    /** A test field value. */
    private static final String TEST_STU_ID = "818283848";

    /** A test field value. */
    private static final String TEST_COURSE = "M 117";

    /** A test field value. */
    private static final LocalDate TEST_EXAM_DT = LocalDate.of(2022, 1, 2);

    /** A test field value. */
    private static final LocalDate TEST_BILL_DT = LocalDate.of(2023, 3, 4);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER1 = String.join(RecBase.DIVIDER,
            "stu_id=818283848",
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER2 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY, //
            "course=M 117",
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER3 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY, //
            "exam_dt=2022-01-02",
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER4 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY, //
            CoreConstants.EMPTY, //
            "bill_dt=2023-03-04");

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER5 = String.join(RecBase.DIVIDER,
            "stu_id=818283848",
            "course=M 117",
            "exam_dt=2022-01-02",
            "bill_dt=2023-03-04");

    /**
     * Constructs a new {@code IvtRawPlcFee}.
     */
    TestRawPlcFee() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawPlcFee obj = new RawPlcFee(TEST_STU_ID, TEST_COURSE, TEST_EXAM_DT, TEST_BILL_DT);

        assertEquals(TEST_STU_ID, obj.stuId, "Invalid stu_id value after constructor");
        assertEquals(TEST_COURSE, obj.course, "Invalid course value after constructor");
        assertEquals(TEST_EXAM_DT, obj.examDt, "Invalid exam_dt value after constructor");
        assertEquals(TEST_BILL_DT, obj.billDt, "Invalid bill_dt value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0002() {

        final RawPlcFee obj = new RawPlcFee(TEST_STU_ID, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER1, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0004() {

        final RawPlcFee obj = new RawPlcFee(null, TEST_COURSE, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER2, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0006() {

        final RawPlcFee obj = new RawPlcFee(null, null, TEST_EXAM_DT, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER3, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0008() {

        final RawPlcFee obj = new RawPlcFee(null, null, null, TEST_BILL_DT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER4, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0010() {

        final RawPlcFee obj = new RawPlcFee(TEST_STU_ID, TEST_COURSE, TEST_EXAM_DT, TEST_BILL_DT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER5, ser, "Invalid serialized string");
    }
}
