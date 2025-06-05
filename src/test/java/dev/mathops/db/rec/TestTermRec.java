package dev.mathops.db.rec;

import dev.mathops.db.enums.ETermName;
import dev.mathops.db.rec.RecBase;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.type.TermKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code TermRec} class.
 */
final class TestTermRec {

    /** A field name. */
    private static final TermKey TEST_TERM = new TermKey(ETermName.FALL, 2023);

    /** A field name. */
    private static final LocalDate TEST_START_DATE = LocalDate.of(2022, 1, 2);

    /** A field name. */
    private static final LocalDate TEST_END_DATE = LocalDate.of(2022, 3, 4);

    /** A field name. */
    private static final String TEST_ACADEMIC_YEAR = "2324";

    /** A field name. */
    private static final Integer TEST_ACTIVE_INDEX = Integer.valueOf(3);

    /** A field name. */
    private static final LocalDate TEST_DROP_DEADLINE = LocalDate.of(2022, 9, 9);

    /** A field name. */
    private static final LocalDate TEST_WITHDRAW_DEADLINE = LocalDate.of(2022, 9, 10);

    /** A field name. */
    private static final LocalDate TEST_INC_DEADLINE = LocalDate.of(2023, 11, 12);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "term=202390",
            "start_date=2022-01-02",
            "end_date=2022-03-04",
            "academic_year=2324",
            "active_index=3",
            "drop_deadline=2022-09-09",
            "withdraw_deadline=2022-09-10",
            "inc_deadline=2023-11-12");

    /**
     * Constructs a new {@code IvtTermRec}.
     */
    TestTermRec() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final TermRec obj = new TermRec(TEST_TERM, TEST_START_DATE, TEST_END_DATE, TEST_ACADEMIC_YEAR,
                TEST_ACTIVE_INDEX, TEST_DROP_DEADLINE, TEST_WITHDRAW_DEADLINE, TEST_INC_DEADLINE);

        assertEquals(TEST_TERM, obj.term, "Invalid term_key value after constructor");
        assertEquals(TEST_START_DATE, obj.startDate, "Invalid start_date value after constructor");
        assertEquals(TEST_END_DATE, obj.endDate, "Invalid end_date value after constructor");
        assertEquals(TEST_ACADEMIC_YEAR, obj.academicYear, "Invalid academic_year value after constructor");
        assertEquals(TEST_ACTIVE_INDEX, obj.activeIndex, "Invalid active_index value after constructor");
        assertEquals(TEST_DROP_DEADLINE, obj.dropDeadline, "Invalid drop_deadline value after constructor");
        assertEquals(TEST_WITHDRAW_DEADLINE, obj.withdrawDeadline, "Invalid withdraw_deadline value after constructor");
        assertEquals(TEST_INC_DEADLINE, obj.incDeadline, "Invalid inc_deadline value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final TermRec obj = new TermRec(TEST_TERM, TEST_START_DATE, TEST_END_DATE, TEST_ACADEMIC_YEAR,
                TEST_ACTIVE_INDEX, TEST_DROP_DEADLINE, TEST_WITHDRAW_DEADLINE, TEST_INC_DEADLINE);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
