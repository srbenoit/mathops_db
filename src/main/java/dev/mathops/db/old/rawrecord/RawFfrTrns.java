package dev.mathops.db.old.rawrecord;

import dev.mathops.db.rec.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A raw "ffr_trns" record.
 */
public final class RawFfrTrns extends RecBase implements Comparable<RawFfrTrns> {

    /** The table name. */
    public static final String TABLE_NAME = "ffr_trns";

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_COURSE = "course";

    /** A field name. */
    private static final String FLD_EXAM_PLACED = "exam_placed";

    /** A field name. */
    private static final String FLD_EXAM_DT = "exam_dt";

    /** A field name. */
    private static final String FLD_DT_CR_REFUSED = "dt_cr_refused";

    /** A field name. */
    private static final String FLD_GRADE = "grade";

    /** The 'stu_id' field value. */
    public String stuId;

    /** The 'course' field value. */
    public String course;

    /** The 'exam_placed' field value. */
    public String examPlaced;

    /** The 'exam_dt' field value. */
    public LocalDate examDt;

    /** The 'dt_cr_refused' field value. */
    public LocalDate dtCrRefused;

    /** The 'grade' field value. */
    public String grade;

    /**
     * Constructs a new {@code RawFfrTrns}.
     */
    private RawFfrTrns() {

        super();
    }

    /**
     * Constructs a new {@code RawFfrTrns}.
     *
     * @param theStuId       the student ID
     * @param theCourse      the course
     * @param theExamPlaced  "C" if placed by challenge exam; "N" if not, "T" for transfer credit
     * @param theExamDt      the date the record was created
     * @param theDtCrRefused the date credit was refused
     */
    public RawFfrTrns(final String theStuId, final String theCourse, final String theExamPlaced,
                      final LocalDate theExamDt, final LocalDate theDtCrRefused, final String theGrade) {
        super();

        this.stuId = theStuId;
        this.course = theCourse;
        this.examPlaced = theExamPlaced;
        this.examDt = theExamDt;
        this.dtCrRefused = theDtCrRefused;
        this.grade = theGrade;
    }

    /**
     * Extracts a "ffr_trns" record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    public static RawFfrTrns fromResultSet(final ResultSet rs) throws SQLException {

        final RawFfrTrns result = new RawFfrTrns();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.course = getStringField(rs, FLD_COURSE);
        result.examPlaced = getStringField(rs, FLD_EXAM_PLACED);
        result.examDt = getDateField(rs, FLD_EXAM_DT);
        result.dtCrRefused = getDateField(rs, FLD_DT_CR_REFUSED);
        result.grade = getStringField(rs, FLD_GRADE);

        return result;
    }

    /**
     * Compares two records for order.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public int compareTo(final RawFfrTrns o) {

        int result = compareAllowingNull(this.stuId, o.stuId);

        if (result == 0) {
            result = compareAllowingNull(this.course, o.course);
            if (result == 0) {
                result = compareAllowingNull(this.examDt, o.examDt);
            }
        }

        return result;
    }

    /**
     * Generates a string serialization of the record. Each concrete subclass should have a constructor that accepts a
     * single {@code String} to reconstruct the object from this string.
     *
     * @return the string
     */
    @Override
    public String toString() {

        final HtmlBuilder htm = new HtmlBuilder(40);

        appendField(htm, FLD_STU_ID, this.stuId);
        htm.add(DIVIDER);
        appendField(htm, FLD_COURSE, this.course);
        htm.add(DIVIDER);
        appendField(htm, FLD_EXAM_PLACED, this.examPlaced);
        htm.add(DIVIDER);
        appendField(htm, FLD_EXAM_DT, this.examDt);
        htm.add(DIVIDER);
        appendField(htm, FLD_DT_CR_REFUSED, this.dtCrRefused);
        htm.add(DIVIDER);
        appendField(htm, FLD_GRADE, this.grade);

        return htm.toString();
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return Objects.hashCode(this.stuId)
               + Objects.hashCode(this.course)
               + Objects.hashCode(this.examPlaced)
               + Objects.hashCode(this.examDt)
               + Objects.hashCode(this.dtCrRefused)
               + Objects.hashCode(this.grade);
    }

    /**
     * Tests whether this object is equal to another.
     *
     * @param obj the other object
     * @return true if equal; false if not
     */
    @Override
    public boolean equals(final Object obj) {

        final boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof final RawFfrTrns rec) {
            equal = Objects.equals(this.stuId, rec.stuId)
                    && Objects.equals(this.course, rec.course)
                    && Objects.equals(this.examPlaced, rec.examPlaced)
                    && Objects.equals(this.examDt, rec.examDt)
                    && Objects.equals(this.dtCrRefused, rec.dtCrRefused)
                    && Objects.equals(this.grade, rec.grade);
        } else {
            equal = false;
        }

        return equal;
    }
}
