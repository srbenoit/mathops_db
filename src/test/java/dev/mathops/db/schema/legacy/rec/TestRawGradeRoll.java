package dev.mathops.db.schema.legacy.rec;

import dev.mathops.db.field.ETermName;
import dev.mathops.db.schema.RecBase;
import dev.mathops.db.field.TermKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawGradeRoll} class.
 */
final class TestRawGradeRoll {

    /** A field name. */
    private static final TermKey TEST_TERM = new TermKey(ETermName.FALL, 2022);

    /** A field name. */
    private static final String TEST_COURSE = "M 117";

    /** A field name. */
    private static final String TEST_SECT = "001";

    /** A field name. */
    private static final String TEST_FULLNAME = "Steve Benoit";

    /** A field name. */
    private static final String TEST_GRADE_OPT = "A";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "term=Fall, 2022",
            "stu_id=888888888",
            "course=M 117",
            "sect=001",
            "fullname=Steve Benoit",
            "grade_opt=A");

    /**
     * Constructs a new {@code IvtRawGradeRoll}.
     */
    TestRawGradeRoll() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawGradeRoll obj = new RawGradeRoll(TEST_TERM, RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_SECT,
                TEST_FULLNAME, TEST_GRADE_OPT);

        assertEquals(TEST_TERM, obj.termKey, "Invalid term_key value after constructor");
        assertEquals(RawStudent.TEST_STUDENT_ID, obj.stuId, "Invalid stu_id value after constructor");
        assertEquals(TEST_COURSE, obj.course, "Invalid course value after constructor");
        assertEquals(TEST_SECT, obj.sect, "Invalid sect value after constructor");
        assertEquals(TEST_FULLNAME, obj.fullname, "Invalid fullname value after constructor");
        assertEquals(TEST_GRADE_OPT, obj.gradeOpt, "Invalid grade_opt value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final RawGradeRoll obj = new RawGradeRoll(TEST_TERM, RawStudent.TEST_STUDENT_ID, TEST_COURSE, TEST_SECT,
                TEST_FULLNAME, TEST_GRADE_OPT);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
