package dev.mathops.db.rec;

import dev.mathops.db.rec.RecBase;
import dev.mathops.db.rec.StandardMilestoneRec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code StandardMilestoneRec} class.
 */
final class TestStandardMilestoneRec {

    /** A field name. */
    private static final String TEST_PACE_TRACK = "A";

    /** A field name. */
    private static final Integer TEST_PACE = Integer.valueOf(3);

    /** A field name. */
    private static final Integer TEST_PACE_INDEX = Integer.valueOf(2);

    /** A field name. */
    private static final Integer TEST_UNIT = Integer.valueOf(8);

    /** A field name. */
    private static final Integer TEST_OBJECTIVE = Integer.valueOf(1);

    /** A field name. */
    private static final String TEST_MS_TYPE = "MA";

    /** A field name. */
    private static final LocalDate TEST_MS_DATE = LocalDate.of(2023, 7, 1);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "pace_track=A",
            "pace=3",
            "pace_index=2",
            "unit=8",
            "objective=1",
            "ms_type=MA",
            "ms_date=2023-07-01");

    /**
     * Constructs a new {@code IvtStandardMilestoneRec}.
     */
    TestStandardMilestoneRec() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final StandardMilestoneRec obj = new StandardMilestoneRec(TEST_PACE_TRACK, TEST_PACE, TEST_PACE_INDEX,
                TEST_UNIT, TEST_OBJECTIVE, TEST_MS_TYPE, TEST_MS_DATE);

        assertEquals(TEST_PACE_TRACK, obj.paceTrack, "Invalid pace_track value after constructor");
        assertEquals(TEST_PACE, obj.pace, "Invalid pace value after constructor");
        assertEquals(TEST_PACE_INDEX, obj.paceIndex, "Invalid pace_index value after constructor");
        assertEquals(TEST_UNIT, obj.unit, "Invalid unit value after constructor");
        assertEquals(TEST_OBJECTIVE, obj.objective, "Invalid objective value after constructor");
        assertEquals(TEST_MS_TYPE, obj.msType, "Invalid ms_type value after constructor");
        assertEquals(TEST_MS_DATE, obj.msDate, "Invalid ms_date value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final StandardMilestoneRec obj = new StandardMilestoneRec(TEST_PACE_TRACK, TEST_PACE, TEST_PACE_INDEX,
                TEST_UNIT, TEST_OBJECTIVE, TEST_MS_TYPE, TEST_MS_DATE);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
