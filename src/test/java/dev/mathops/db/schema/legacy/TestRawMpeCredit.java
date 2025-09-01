package dev.mathops.db.schema.legacy;

import dev.mathops.db.rec.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawMpeCredit} class.
 */
final class TestRawMpeCredit {

    /** A field name. */
    private static final String TEST_COURSE = "M 117";

    /** A field name. */
    private static final String TEST_EXAM_PLACED = "P";

    /** A field name. */
    private static final LocalDate TEST_EXAM_DT = LocalDate.of(2022, 1, 2);

    /** A field name. */
    private static final LocalDate TEST_DT_CR_REFUSED = LocalDate.of(2023, 3, 4);

    /** A field name. */
    private static final Long TEST_SERIAL_NBR = Long.valueOf(123456L);

    /** A field name. */
    private static final String TEST_VERSION = "MPTTC";

    /** A field name. */
    private static final String TEST_EXAM_SOURCE = "SY";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "stu_id=888888888",
            "course=M 117",
            "exam_placed=P",
            "exam_dt=2022-01-02",
            "dt_cr_refused=2023-03-04",
            "serial_nbr=123456",
            "version=MPTTC",
            "exam_source=SY");

    /**
     * Constructs a new {@code IvtRawMpeCredit}.
     */
    TestRawMpeCredit() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawMpeCredit obj = new RawMpeCredit(RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_EXAM_PLACED,
                TEST_EXAM_DT, TEST_DT_CR_REFUSED, TEST_SERIAL_NBR, TEST_VERSION, TEST_EXAM_SOURCE);

        assertEquals(RawStudent.TEST_STUDENT_ID, obj.stuId, "Invalid stu_id value after constructor");
        assertEquals(TEST_COURSE, obj.course, "Invalid course value after constructor");
        assertEquals(TEST_EXAM_PLACED, obj.examPlaced, "Invalid exam_placed value after constructor");
        assertEquals(TEST_EXAM_DT, obj.examDt, "Invalid exam_dt value after constructor");
        assertEquals(TEST_DT_CR_REFUSED, obj.dtCrRefused, "Invalid why_denied value after constructor");
        assertEquals(TEST_SERIAL_NBR, obj.serialNbr, "Invalid serial_nbr value after constructor");
        assertEquals(TEST_VERSION, obj.version, "Invalid version value after constructor");
        assertEquals(TEST_EXAM_SOURCE, obj.examSource, "Invalid exam_source value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final RawMpeCredit obj = new RawMpeCredit(RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_EXAM_PLACED,
                TEST_EXAM_DT, TEST_DT_CR_REFUSED, TEST_SERIAL_NBR, TEST_VERSION, TEST_EXAM_SOURCE);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
