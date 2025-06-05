package dev.mathops.db.rec.term;

import dev.mathops.db.rec.RecBase;
import dev.mathops.db.rec.term.LtiContextCourseSectionRec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code LtiContextCourseSectionRec} class.
 */
final class TestLtiContextCourseSectionRec {

    /** A field name. */
    private static final String TEST_CLIENT_ID = "1234CLIENT1234";

    /** A field name. */
    private static final String TEST_ISSUER = "domino.math.colostate.edu";

    /** A field name. */
    private static final String TEST_DEPLOYMENT_ID = "slkoejtopwi3u40poncwfen235098fao21";

    /** A field name. */
    private static final String TEST_CONTEXT_ID = "0ck3woiruj20ijmc012qwop3imr2903rnk0";

    /** A field name. */
    private static final String TEST_COURSE_ID = "M 117";

    /** A field name. */
    private static final String TEST_SECTION_NBR = "801";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "client_id=1234CLIENT1234",
            "issuer=domino.math.colostate.edu",
            "deployment_id=slkoejtopwi3u40poncwfen235098fao21",
            "context_id=0ck3woiruj20ijmc012qwop3imr2903rnk0",
            "course_id=M 117",
            "section_nbr=801");

    /**
     * Constructs a new {@code TestLtiCourseRec}.
     */
    TestLtiContextCourseSectionRec() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final LtiContextCourseSectionRec obj = new LtiContextCourseSectionRec(TEST_CLIENT_ID, TEST_ISSUER,
                TEST_DEPLOYMENT_ID, TEST_CONTEXT_ID, TEST_COURSE_ID, TEST_SECTION_NBR);

        assertEquals(TEST_CLIENT_ID, obj.clientId, "Invalid client ID value after constructor");
        assertEquals(TEST_ISSUER, obj.issuer, "Invalid issuer value after constructor");
        assertEquals(TEST_DEPLOYMENT_ID, obj.deploymentId, "Invalid deployment ID value after constructor");
        assertEquals(TEST_CONTEXT_ID, obj.contextId, "Invalid context ID value after constructor");
        assertEquals(TEST_COURSE_ID, obj.courseId, "Invalid course ID value after constructor");
        assertEquals(TEST_SECTION_NBR, obj.sectionNbr, "Invalid section number value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final LtiContextCourseSectionRec obj = new LtiContextCourseSectionRec(TEST_CLIENT_ID, TEST_ISSUER,
                TEST_DEPLOYMENT_ID, TEST_CONTEXT_ID, TEST_COURSE_ID, TEST_SECTION_NBR);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
