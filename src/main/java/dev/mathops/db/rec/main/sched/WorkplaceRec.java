package dev.mathops.db.rec.main.sched;

import dev.mathops.db.DataDict;
import dev.mathops.db.rec.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

/**
 * An immutable raw "workplace" record.
 *
 * <p>
 * Each record represents a workplace in which a schedule can be constructed.  A workplace may define several
 * positions.
 *
 * <p>
 * The primary key on the underlying table is the workplace ID.
 */
public final class WorkplaceRec extends RecBase implements Comparable<WorkplaceRec> {

    /** The table name for serialization of records. */
    public static final String TABLE_NAME = "sch_workplace";

    /** The 'workplace_id' field value. */
    public final String workplaceId;

    /** The 'workplace_name' field value. */
    public final String workplaceName;

    /**
     * Constructs a new {@code WorkplaceRec}.
     *
     * @param theWorkplaceId   the workplace ID
     * @param theWorkplaceName the workplace name
     */
    public WorkplaceRec(final String theWorkplaceId, final String theWorkplaceName) {

        super();

        if (theWorkplaceId == null) {
            throw new IllegalArgumentException("Workplace ID may not be null");
        }
        if (theWorkplaceName == null) {
            throw new IllegalArgumentException("Workplace name may not be null");
        }

        this.workplaceId = theWorkplaceId;
        this.workplaceName = theWorkplaceName;
    }

    /**
     * Compares two records for order.  Order is based only on workplace ID.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public int compareTo(final WorkplaceRec o) {

        return this.workplaceId.compareTo(o.workplaceId);
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

        appendField(htm, DataDict.FLD_WORKPLACE_ID, this.workplaceId);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_WORKPLACE_NAME, this.workplaceName);

        return htm.toString();
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return this.workplaceId.hashCode()
               + this.workplaceName.hashCode();
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
        } else if (obj instanceof final WorkplaceRec rec) {
            equal = this.workplaceId.equals(rec.workplaceId)
                    && this.workplaceName.equals(rec.workplaceName);
        } else {
            equal = false;
        }

        return equal;
    }
}
