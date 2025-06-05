package dev.mathops.db.old.rawrecord;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.old.rawrecord.RawCalcs;
import dev.mathops.db.rec.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawCalcs} class.
 */
final class TestRawCalcs {

    /** A test field value. */
    private static final String TEST_STU_ID = "81818181";

    /** A test field value. */
    private static final String TEST_ISSUED_NBR = "12";

    /** A test field value. */
    private static final String TEST_RETURN_NBR = "34";

    /** A test field value. */
    private static final Long TEST_SERIAL_NBR = Long.valueOf(1234567890123L);

    /** A test field value. */
    private static final LocalDate TEST_EXAM_DT = LocalDate.of(2023, 2, 3);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER1 = String.join(RecBase.DIVIDER,
            "stu_id=81818181",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER2 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            "issued_nbr=12",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER3 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "return_nbr=34",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER4 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "serial_nbr=1234567890123",
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER5 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "exam_dt=2023-02-03");

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER6 = String.join(RecBase.DIVIDER,
            "stu_id=81818181",
            "issued_nbr=12",
            "return_nbr=34",
            "serial_nbr=1234567890123",
            "exam_dt=2023-02-03");

    /**
     * Constructs a new {@code IvtRawCalcs}.
     */
    TestRawCalcs() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawCalcs obj = new RawCalcs(TEST_STU_ID, TEST_ISSUED_NBR, TEST_RETURN_NBR,
                TEST_SERIAL_NBR, TEST_EXAM_DT);

        assertEquals(TEST_STU_ID, obj.stuId, "Invalid stu_id value after constructor");
        assertEquals(TEST_ISSUED_NBR, obj.issuedNbr, "Invalid issued_nbr value after constructor");
        assertEquals(TEST_RETURN_NBR, obj.returnNbr, "Invalid return_nbr value after constructor");
        assertEquals(TEST_SERIAL_NBR, obj.serialNbr, "Invalid serial_nbr value after constructor");
        assertEquals(TEST_EXAM_DT, obj.examDt, "Invalid exam_dt value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0002() {

        final RawCalcs obj = new RawCalcs(TEST_STU_ID, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER1, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0004() {

        final RawCalcs obj = new RawCalcs(null, TEST_ISSUED_NBR, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER2, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0006() {

        final RawCalcs obj = new RawCalcs(null, null, TEST_RETURN_NBR, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER3, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0008() {

        final RawCalcs obj = new RawCalcs(null, null, null, TEST_SERIAL_NBR, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER4, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0010() {

        final RawCalcs obj = new RawCalcs(null, null, null, null, TEST_EXAM_DT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER5, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0012() {

        final RawCalcs obj = new RawCalcs(TEST_STU_ID, TEST_ISSUED_NBR, TEST_RETURN_NBR,
                TEST_SERIAL_NBR, TEST_EXAM_DT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER6, ser, "Invalid serialized string");
    }
}
