package dev.mathops.db.schema.legacy;

import dev.mathops.db.rec.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A raw "admin_hold" record.
 */
public final class RawAdminHold extends RecBase implements Comparable<RawAdminHold> {

    /** The table name. */
    public static final String TABLE_NAME = "admin_hold";

    /** A field name. */
    public static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    public static final String FLD_HOLD_ID = "hold_id";

    /** A field name. */
    public static final String FLD_SEV_ADMIN_HOLD = "sev_admin_hold";

    /** A field name. */
    public static final String FLD_TIMES_DISPLAY = "times_display";

    /** A field name. */
    public static final String FLD_CREATE_DT = "create_dt";

    /** The 'stu_id' field value. */
    public String stuId;

    /** The 'hold_id' field value. */
    public String holdId;

    /** The 'sev_admin_hold' field value. */
    public String sevAdminHold;

    /** The 'times_display' field value. */
    public Integer timesDisplay;

    /** The 'create_dt' field value. */
    public LocalDate createDt;

    /**
     * Constructs a new {@code RawAdminHold}.
     */
    public RawAdminHold() {

        super();
    }

    /**
     * Constructs a new {@code RawAdminHold}.
     *
     * @param theStuId        the student ID
     * @param theHoldId       the hold ID
     * @param theSevAdminHold the hold severity ("F" for fatal, "N" for nonfatal)
     * @param theTimesDisplay the number of times this hold has been applied for a resource
     * @param theCreateDt     the creation date
     */
    public RawAdminHold(final String theStuId, final String theHoldId, final String theSevAdminHold,
                        final Integer theTimesDisplay, final LocalDate theCreateDt) {

        super();

        this.stuId = theStuId;
        this.holdId = theHoldId;
        this.sevAdminHold = theSevAdminHold;
        this.timesDisplay = theTimesDisplay;
        this.createDt = theCreateDt;
    }

    /**
     * Extracts an "admin_hold" record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    public static RawAdminHold fromResultSet(final ResultSet rs) throws SQLException {

        final RawAdminHold result = new RawAdminHold();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.holdId = getStringField(rs, FLD_HOLD_ID);
        result.sevAdminHold = getStringField(rs, FLD_SEV_ADMIN_HOLD);
        result.timesDisplay = getIntegerField(rs, FLD_TIMES_DISPLAY);
        result.createDt = getDateField(rs, FLD_CREATE_DT);

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
    public int compareTo(final RawAdminHold o) {

        int result = compareAllowingNull(this.stuId, o.stuId);

        if (result == 0) {
            result = compareAllowingNull(this.holdId, o.holdId);
            if (result == 0) {
                result = compareAllowingNull(this.createDt, o.createDt);
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

        final HtmlBuilder htm = new HtmlBuilder(80);

        appendField(htm, FLD_STU_ID, this.stuId);
        htm.add(DIVIDER);
        appendField(htm, FLD_HOLD_ID, this.holdId);
        htm.add(DIVIDER);
        appendField(htm, FLD_SEV_ADMIN_HOLD, this.sevAdminHold);
        htm.add(DIVIDER);
        appendField(htm, FLD_TIMES_DISPLAY, this.timesDisplay);
        htm.add(DIVIDER);
        appendField(htm, FLD_CREATE_DT, this.createDt);

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
                + Objects.hashCode(this.holdId)
                + Objects.hashCode(this.sevAdminHold)
                + Objects.hashCode(this.timesDisplay)
                + Objects.hashCode(this.createDt);
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
        } else if (obj instanceof final RawAdminHold rec) {
            equal = Objects.equals(this.stuId, rec.stuId)
                    && Objects.equals(this.holdId, rec.holdId)
                    && Objects.equals(this.sevAdminHold, rec.sevAdminHold)
                    && Objects.equals(this.timesDisplay, rec.timesDisplay)
                    && Objects.equals(this.createDt, rec.createDt);
        } else {
            equal = false;
        }

        return equal;
    }
}
