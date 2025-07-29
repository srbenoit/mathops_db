package dev.mathops.db.table;

import dev.mathops.db.table.constraint.AbstractFieldConstraint;
import dev.mathops.text.builder.SimpleBuilder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * An immutable definition of a field that can be used in one or more database tables.  This class does not define the
 * field's role or nullability, which could change from table to table.
 *
 * <p>Each field defined within a table is represented by an instance of this class, which stores the field name
 * (unique within the table), the field's data type (see below), and zero or more constraints that values for this field
 * must obey.
 *
 * <p>Field objects are suitable for use as map keys, and implement {@code Comparable&lt;Field&gt;} so they can be used
 * in contexts that require a well-defined order (such as keys in a {@code TreeMap}).  Ordering is based on field name
 * then field data type.
 */
public final class FieldDef implements Comparable<FieldDef> {

    /** An empty constraints array. */
    private static final AbstractFieldConstraint<?>[] ZERO_LEN_CONSTRAINTS = new AbstractFieldConstraint<?>[0];

    /** The field name. */
    private final String name;

    /** The field data type. */
    private final EFieldType type;

    /** The field description. */
    private final String description;

    /** Any constraints attached to the field. */
    private final AbstractFieldConstraint<?>[] constraints;

    /**
     * Constructs a new {@code FieldDef}.
     *
     * @param theName        the field name
     * @param theType        the field data type
     * @param theDescription the field description (this should be a short phrase that could be used in a tool-tip
     *                       associated with a field where the user is entering a value for the field)
     * @param theConstraints any constraints attached to the field
     */
    public FieldDef(final String theName, final EFieldType theType, final String theDescription,
                    final AbstractFieldConstraint<?>... theConstraints) {

        if (theName == null) {
            throw new IllegalArgumentException("Field name may not be null");
        }
        if (theType == null) {
            throw new IllegalArgumentException("Field type may not be null");
        }
        if (NameUtils.isInvalidName(theName)) {
            throw new IllegalArgumentException("Field name is not a valid identifier");
        }

        this.name = theName;
        this.type = theType;
        this.description = theDescription;

        if (theConstraints == null) {
            this.constraints = ZERO_LEN_CONSTRAINTS;
        } else {
            final int len = theConstraints.length;
            this.constraints = new AbstractFieldConstraint[len];
            final Collection<String> names = new HashSet<>(len);

            for (int i = 0; i < len; ++i) {
                if (theConstraints[i] == null) {
                    throw new IllegalArgumentException("Field constraints array may not include null values");
                }
                final String constraintName = theConstraints[i].getName();
                if (names.contains(constraintName)) {
                    throw new IllegalArgumentException("All constraints on a field must have unique names");
                }
                names.add(constraintName);
                this.constraints[i] = theConstraints[i];
            }

            // We sort the constraints so two fields that have the same name/type/role/description and the same set of
            // constraints will be considered "equal" regardless of the ordering in which they provide the constraints.
            // This is the reason for having a constraint name - there is no other field shared by all types of
            // constraint that could provide a natural ordering for constraints within a field.
            Arrays.sort(this.constraints);
        }
    }

    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public String getName() {

        return this.name;
    }

    /**
     * Gets the field data type.
     *
     * @return the field type
     */
    public EFieldType getType() {

        return this.type;
    }

    /**
     * Gets the field description.
     *
     * @return the field description
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Gets the constraints array.
     *
     * @return the constraints array
     */
    private AbstractFieldConstraint<?>[] innerGetConstraints() {

        return this.constraints;
    }

    /**
     * Gets the number of field constraints.
     *
     * @return the number of constraints
     */
    public int getNumConstraints() {

        return this.constraints.length;
    }

    /**
     * Gets a specified field constraint.
     *
     * @param index the zero-based constraint index
     * @return the constraint
     */
    public AbstractFieldConstraint<?> getConstraint(final int index) {

        return this.constraints[index];
    }

    /**
     * Tests whether an object is of a type that is valid for this field.  Some types that are promotable to a valid
     * type are considered valid (an Integer is valid for a Long field, for example).
     *
     * @param object the object whose type to test (not {@code null})
     * @return true if the object's type is valid for this field
     */
    public boolean isValidType(final Object object) {

        boolean valid = false;

        switch (this.type) {
            case INTEGER -> valid = object instanceof Byte || object instanceof Integer;
            case LONG -> valid = object instanceof Byte || object instanceof Integer || object instanceof Long;
            case FLOAT -> valid = object instanceof Float;
            case DOUBLE -> valid = object instanceof Float || object instanceof Double;
            case DECIMAL -> valid = object instanceof BigDecimal;
            case STRING -> valid = object instanceof String;
            case BINARY -> valid = object instanceof byte[];
            case BOOLEAN -> valid = object instanceof Boolean;
            case LOCAL_DATE -> valid = object instanceof Date;
            case LOCAL_TIME -> valid = object instanceof Time;
            case LOCAL_DATE_TIME -> valid = object instanceof Timestamp;
        }

        return valid;
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return this.name.hashCode() + this.type.hashCode() + Objects.hashCode(this.description)
               + Arrays.hashCode(this.constraints);
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
        } else if (obj instanceof final FieldDef objField) {
            final AbstractFieldConstraint<?>[] objConstraints = objField.innerGetConstraints();

            equal = this.type == objField.type && this.name.equals(objField.name)
                    && Objects.equals(this.description, objField.description)
                    && Arrays.equals(this.constraints, objConstraints);
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
    public int compareTo(final FieldDef o) {

        int result = this.name.compareTo(o.name);

        if (result == 0) {
            result = this.type.compareTo(o.type);
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

        final String constraintsString = Arrays.toString(this.constraints);

        return SimpleBuilder.concat("FieldDef{name='", this.name, "', type=", this.type, ", constraints=",
                constraintsString, "}");
    }
}
