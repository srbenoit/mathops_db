package dev.mathops.db.schema.main.rec;

import dev.mathops.db.field.DataDict;
import dev.mathops.db.schema.RecBase;
import dev.mathops.text.builder.HtmlBuilder;

/**
 * An immutable raw "position" record.
 *
 * <p>
 * Each record represents a position for which an employee can be eligible to take on shifts.
 *
 * <p>
 * The primary key on the underlying table is the workplace ID and position ID.
 */
public final class PositionRec extends RecBase implements Comparable<PositionRec> {

    /** The table name for serialization of records. */
    public static final String TABLE_NAME = "sch_position";

    /** The 'workplace_id' field value. */
    public final String workplaceId;

    /** The 'position_id' field value. */
    public final String positionId;

    /** The 'position_name' field value. */
    public final String positionName;

    /**
     * Constructs a new {@code PositionRec}.
     *
     * @param theWorkplaceId  the workplace ID
     * @param thePositionId   the position ID
     * @param thePositionName the position name
     */
    public PositionRec(final String theWorkplaceId, final String thePositionId, final String thePositionName) {

        super();

        if (theWorkplaceId == null) {
            throw new IllegalArgumentException("Workplace ID may not be null");
        }
        if (thePositionId == null) {
            throw new IllegalArgumentException("Position ID may not be null");
        }
        if (thePositionName == null) {
            throw new IllegalArgumentException("Workplace name may not be null");
        }

        this.workplaceId = theWorkplaceId;
        this.positionId = thePositionId;
        this.positionName = thePositionName;
    }

    /**
     * Compares two records for order.  Order is based on workplace ID and position ID.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public int compareTo(final PositionRec o) {

        int result = this.workplaceId.compareTo(o.workplaceId);

        if (result == 0) {
            result = this.positionId.compareTo(o.positionId);
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

        appendField(htm, DataDict.FLD_WORKPLACE_ID, this.workplaceId);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_POSITION_ID, this.positionId);
        htm.add(DIVIDER);
        appendField(htm, DataDict.FLD_WORKPLACE_NAME, this.positionName);

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
               + this.positionId.hashCode()
               + this.positionName.hashCode();
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
        } else if (obj instanceof final PositionRec rec) {
            equal = this.workplaceId.equals(rec.workplaceId)
                    && this.positionId.equals(rec.positionId)
                    && this.positionName.equals(rec.positionName);
        } else {
            equal = false;
        }

        return equal;
    }
}
