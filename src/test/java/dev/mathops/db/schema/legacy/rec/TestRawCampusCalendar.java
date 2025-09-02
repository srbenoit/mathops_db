package dev.mathops.db.schema.legacy.rec;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.schema.RecBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code RawCampusCalendar} class.
 */
final class TestRawCampusCalendar {

    /** A test field value. */
    private static final LocalDate TEST_CAMPUS_DT = LocalDate.of(2023, 2, 3);

    /** A test field value. */
    private static final String TEST_DT_DESC = RawCampusCalendar.DT_DESC_BOOKSTORE;

    /** A test field value. */
    private static final String TEST_OPEN_TIME1 = "10:00 AM";

    /** A test field value. */
    private static final String TEST_OPEN_TIME2 = "11:00 AM";

    /** A test field value. */
    private static final String TEST_OPEN_TIME3 = "NOON";

    /** A test field value. */
    private static final String TEST_CLOSE_TIME1 = "8:00 PM";

    /** A test field value. */
    private static final String TEST_CLOSE_TIME2 = "4:00 PM";

    /** A test field value. */
    private static final String TEST_CLOSE_TIME3 = "5:00 PM";

    /** A test field value. */
    private static final String TEST_WEEKDAYS_1 = "Monday - Thursday";

    /** A test field value. */
    private static final String TEST_WEEKDAYS_2 = "Friday";

    /** A test field value. */
    private static final String TEST_WEEKDAYS_3 = "Sunday";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER1 = String.join(RecBase.DIVIDER,
            "campus_dt=2023-02-03",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER2 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            "dt_desc=bookstore",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER3 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "open_time1=10:00 AM",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER4 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "open_time2=11:00 AM",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER4B = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "open_time3=NOON",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER5 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "close_time1=8:00 PM",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER6 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "close_time2=4:00 PM",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER6B = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "close_time3=5:00 PM",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER7 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "weekdays_1=Monday - Thursday",
            CoreConstants.EMPTY,
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER8 = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "weekdays_2=Friday",
            CoreConstants.EMPTY);

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER8B = String.join(RecBase.DIVIDER,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            CoreConstants.EMPTY,
            "weekdays_3=Sunday");

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER9 = String.join(RecBase.DIVIDER,
            "campus_dt=2023-02-03",
            "dt_desc=bookstore",
            "open_time1=10:00 AM",
            "open_time2=11:00 AM",
            "open_time3=NOON",
            "close_time1=8:00 PM",
            "close_time2=4:00 PM",
            "close_time3=5:00 PM",
            "weekdays_1=Monday - Thursday",
            "weekdays_2=Friday",
            "weekdays_3=Sunday");

    /**
     * Constructs a new {@code IvtRawCampusCalendar}.
     */
    TestRawCampusCalendar() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final RawCampusCalendar obj = new RawCampusCalendar(TEST_CAMPUS_DT, TEST_DT_DESC, TEST_OPEN_TIME1,
                TEST_OPEN_TIME2, TEST_OPEN_TIME3, TEST_CLOSE_TIME1, TEST_CLOSE_TIME2, TEST_CLOSE_TIME3, TEST_WEEKDAYS_1,
                TEST_WEEKDAYS_2, TEST_WEEKDAYS_3);

        assertEquals(TEST_CAMPUS_DT, obj.campusDt, "Invalid campus_dt value after constructor");
        assertEquals(TEST_DT_DESC, obj.dtDesc, "Invalid dt_desc value after constructor");
        assertEquals(TEST_OPEN_TIME1, obj.openTime1, "Invalid open_time1 value after constructor");
        assertEquals(TEST_OPEN_TIME2, obj.openTime2, "Invalid open_time2 value after constructor");
        assertEquals(TEST_OPEN_TIME3, obj.openTime3, "Invalid open_time3 value after constructor");
        assertEquals(TEST_CLOSE_TIME1, obj.closeTime1, "Invalid close_time1 value after constructor");
        assertEquals(TEST_CLOSE_TIME2, obj.closeTime2, "Invalid close_time2 value after constructor");
        assertEquals(TEST_CLOSE_TIME3, obj.closeTime3, "Invalid close_time3 value after constructor");
        assertEquals(TEST_WEEKDAYS_1, obj.weekdays1, "Invalid weekdays_1 value after constructor");
        assertEquals(TEST_WEEKDAYS_2, obj.weekdays2, "Invalid weekdays_2 value after constructor");
        assertEquals(TEST_WEEKDAYS_3, obj.weekdays3, "Invalid weekdays_3 value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0002() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(TEST_CAMPUS_DT, null, null, null, null, null, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER1, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0004() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, TEST_DT_DESC, null, null, null, null, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER2, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0006() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, TEST_OPEN_TIME1, null, null, null, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER3, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0008() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, TEST_OPEN_TIME2, null, null, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER4, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0009() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, TEST_OPEN_TIME3, null, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER4B, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0010() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, TEST_CLOSE_TIME1, null, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER5, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0012() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, null, TEST_CLOSE_TIME2, null, null, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER6, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0013() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, null, null, TEST_CLOSE_TIME3, null, null, null);
        final String ser = obj.toString();

        assertEquals(EXPECT_SER6B, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0014() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, null, null, null, TEST_WEEKDAYS_1, null, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER7, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0016() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, null, null, null, null, TEST_WEEKDAYS_2, null);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER8, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0017() {

        final RawCampusCalendar obj =
                new RawCampusCalendar(null, null, null, null, null, null, null, null, null, null, TEST_WEEKDAYS_3);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER8B, ser, "Invalid serialized string");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0018() {

        final RawCampusCalendar obj = new RawCampusCalendar(TEST_CAMPUS_DT, TEST_DT_DESC, TEST_OPEN_TIME1,
                TEST_OPEN_TIME2, TEST_OPEN_TIME3, TEST_CLOSE_TIME1, TEST_CLOSE_TIME2, TEST_CLOSE_TIME3, TEST_WEEKDAYS_1,
                TEST_WEEKDAYS_2, TEST_WEEKDAYS_3);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER9, ser, "Invalid serialized string");
    }
}
