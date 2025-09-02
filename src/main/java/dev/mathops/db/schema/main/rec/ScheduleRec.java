package dev.mathops.db.schema.main.rec;

import dev.mathops.db.field.DataDict;
import dev.mathops.db.schema.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

import java.time.LocalDate;

/**
 * An immutable raw "schedule" record.
 *
 * <p>
 * Each record represents a schedule over a period of weeks like a semester.  A schedule is attached to a workplace. A
 * schedule defines a "typical week", which can be overridden with things like closures, training days, or extra days.
 * Each day in the schedule is blocked into shifts, and for each shift the number of workers needed for each position is
 * defined.
 *
 * <p>
 * Once a schedule is created, employees can be assigned to shifts.  This is first done automatically, based on the
 * expressed preferences and availability of employees, then can be adjusted manually.
 *
 * <p>
 * The primary key on the underlying table is the schedule ID.
 */
public final class ScheduleRec extends RecBase implements Comparable<ScheduleRec> {

    /** The table name for serialization of records. */
    public static final String TABLE_NAME = "sch_schedule";

    /** The 'schedule_id' field value. */
    public final String scheduleId;

    /** The 'workplace_id' field value. */
    public final String workplaceId;

    /** The start date. */
    public final LocalDate startDate;

    /** The end date. */
    public final LocalDate endDate;

    /**
     * Constructs a new {@code ScheduleRec}.
     *
     * @param theScheduleId  the schedule ID
     * @param theWorkplaceId the workplace ID
     * @param theStartDate   the start date
     * @param theEndDate     the end date
     */
    public ScheduleRec(final String theScheduleId, final String theWorkplaceId, final LocalDate theStartDate,
                       final LocalDate theEndDate) {

        super();

        if (theScheduleId == null) {
            throw new IllegalArgumentException("Schedule ID may not be null");
        }
        if (theWorkplaceId == null) {
            throw new IllegalArgumentException("Workplace ID may not be null");
        }
        if (theStartDate == null) {
            throw new IllegalArgumentException("Start date may not be null");
        }
        if (theEndDate == null) {
            throw new IllegalArgumentException("End date may not be null");
        }

        this.scheduleId = theScheduleId;
        this.workplaceId = theWorkplaceId;
        this.startDate = theStartDate;
        this.endDate = theEndDate;
    }

    /**
     * Compares two records for order.  Order is based on schedule ID.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public int compareTo(final ScheduleRec o) {

        return this.scheduleId.compareTo(o.scheduleId);
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

        appendField(htm, DataDict.FLD_SCHEDULE_ID, this.scheduleId);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_WORKPLACE_ID, this.workplaceId);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_START_DATE, this.startDate);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_END_DATE, this.endDate);

        return htm.toString();
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return this.scheduleId.hashCode()
               + this.workplaceId.hashCode()
               + this.startDate.hashCode()
               + this.endDate.hashCode();
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
        } else if (obj instanceof final ScheduleRec rec) {
            equal = this.scheduleId.equals(rec.scheduleId)
                    && this.workplaceId.equals(rec.workplaceId)
                    && this.startDate.equals(rec.startDate)
                    && this.endDate.equals(rec.endDate);
        } else {
            equal = false;
        }

        return equal;
    }
}
