package dev.mathops.db.table;

import dev.mathops.text.builder.SimpleBuilder;

/**
 * An immutable definition of a field appearing with a specific role in a database table.
 *
 * <p>A field definition referenced by this object can be shared by multiple tables, but this class defines the role
 * the field takes on in a particular table.
 *
 * <p>Field objects are suitable for use as map keys, and implement {@code Comparable&lt;Field&gt;} so they can be used
 * in contexts that require a well-defined order (such as keys in a {@code TreeMap}).  Ordering is based on field
 * definition, then field's role.
 *
 * <h2>Field Roles</h2>
 * <p>
 * The roles within their containing table that a field may be assigned include:
 * <dl>
 * <dt>Partition Key</dt>
 *     <dd>The field participates in the primary key. The tuple of all fields that participate in the primary key
 *         together must have a unique value for each record in the table.</dd>
 *     <dd>The field can be used to partition data across multiple servers.  Fields used as partition keys should be
 *         chosen so the majority of queries will select only records with the same value for the partition key.</dd>
 * <dt>Clustering Key</dt>
 *     <dd>The field participates in the primary key.</dd>
 *     <dd>The field can be used to cluster data within a single partition for faster selection of data by queries.</dd>
 * <dt>Not-null</dt>
 *     <dd>The field does NOT participate in the primary key.</dd>
 *     <dd>The field may not have a NULL value.  It must have a specified value in each record.  Note that an empty
 *         string is not considered a NULL value.</dd>
 * <dt>Nullable</dt>
 *     <dd>The field does NOT participate in the primary key.</dd>
 *     <dd>The field may have any value, including NULL.</dd>
 * </dl>
 */
public final class Field implements Comparable<Field> {

    /** The field definition. */
    private final FieldDef def;

    /** The field role in the table. */
    private final EFieldRole role;

    /**
     * Constructs a new {@code Field}.
     *
     * @param theDef  the field definition
     * @param theRole the field role in the table
     */
    public Field(final FieldDef theDef, final EFieldRole theRole) {

        if (theDef == null) {
            throw new IllegalArgumentException("Field name may not be null");
        }
        if (theRole == null) {
            throw new IllegalArgumentException("Field role may not be null");
        }

        this.def = theDef;
        this.role = theRole;
    }

    /**
     * Gets the field definition.
     *
     * @return the field definition
     */
    public FieldDef getDef() {

        return this.def;
    }

    /**
     * Gets the field role.
     *
     * @return the field role
     */
    public EFieldRole getRole() {

        return this.role;
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return this.def.hashCode() + this.role.hashCode();
    }

    /**
     * Tests whether this object is equal to another.  To be equal, the other object must be a {@code Field} with the
     * same values for all fields.
     *
     * @return the hash code
     */
    @Override
    public boolean equals(final Object obj) {

        final boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof final Field objField) {
            equal = this.role == objField.role && this.def.equals(objField.def);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public int compareTo(final Field o) {

        int result = this.def.compareTo(o.def);

        if (result == 0) {
            result = this.role.compareTo(o.role);
        }

        return result;
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return SimpleBuilder.concat("Field{def='", this.def, ", role=", this.role, "}");
    }
}
