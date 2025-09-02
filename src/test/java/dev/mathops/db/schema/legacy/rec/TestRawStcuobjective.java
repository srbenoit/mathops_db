package dev.mathops.db.schema.legacy.rec;

import dev.mathops.db.schema.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawStcuobjective} class.
 */
final class TestRawStcuobjective {

    /** A field name. */
    private static final String TEST_COURSE = "M 117";

    /** A field name. */
    private static final Integer TEST_UNIT = Integer.valueOf(4);

    /** A field name. */
    private static final Integer TEST_OBJECTIVE = Integer.valueOf(5);

    /** A field name. */
    private static final LocalDate TEST_LECTURE_VIEWED_DT = LocalDate.of(2023, 1, 2);

    /** A field name. */
    private static final Integer TEST_SEED = Integer.valueOf(6);

    /** A field name. */
    private static final Integer TEST_LAST_COMPONENT_FINISHED = Integer.valueOf(7);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "stu_id=888888888",
            "course=M 117",
            "unit=4",
            "objective=5",
            "lecture_viewed_dt=2023-01-02",
            "seed=6",
            "last_component_finished=7");

    /**
     * Constructs a new {@code IvtRawStcuobjective}.
     */
    TestRawStcuobjective() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawStcuobjective obj = new RawStcuobjective(RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_UNIT,
                TEST_OBJECTIVE, TEST_LECTURE_VIEWED_DT, TEST_SEED, TEST_LAST_COMPONENT_FINISHED);

        assertEquals(RawStudent.TEST_STUDENT_ID, obj.stuId, "Invalid term value after constructor");
        assertEquals(TEST_COURSE, obj.course, "Invalid course value after constructor");
        assertEquals(TEST_UNIT, obj.unit, "Invalid version value after constructor");
        assertEquals(TEST_OBJECTIVE, obj.objective, "Invalid academic_yr value after constructor");
        assertEquals(TEST_LECTURE_VIEWED_DT, obj.lectureViewedDt, "Invalid exam_dt value after constructor");
        assertEquals(TEST_SEED, obj.seed, "Invalid start_time value after constructor");
        assertEquals(TEST_LAST_COMPONENT_FINISHED, obj.lastComponentFinished,
                "Invalid finish_time value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final RawStcuobjective obj = new RawStcuobjective(RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_UNIT,
                TEST_OBJECTIVE, TEST_LECTURE_VIEWED_DT, TEST_SEED, TEST_LAST_COMPONENT_FINISHED);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
