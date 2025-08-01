package dev.mathops.db.table.criteria;

import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;

/**
 *  A criterion that can be used to match an Integer field.
 */
public class IntegerFieldCriterion extends AbstractFieldCriterion {

    /** The match type. */
    final ENumericMatchType matchType;

    /** The integers to use in the match process. */
    final Integer[] integers;

    /**
     * Constructs a new {@code IntegerFieldCriterion}.
     *
     * @param theField     the field
     * @param theMatchType the type of match to perform
     * @param theIntegers  the integers to match using the match type
     */
    public IntegerFieldCriterion(final Field theField, final ENumericMatchType theMatchType,
                                 final Integer... theIntegers) {

        super(theField);

        final FieldDef def = theField.getDef();
        if (def.getType() != EFieldType.INTEGER) {
            throw new IllegalArgumentException("An Integer field criterion must be used with a Byte field");
        }
        if (theMatchType == null) {
            throw new IllegalArgumentException("The match type may not be null");
        }

        if (theIntegers != null) {
            for (final Integer test : theIntegers) {
                if (test == null) {
                    throw new IllegalArgumentException("Match Integer list may not contain null values");
                }
            }
        }

        if (theMatchType != ENumericMatchType.IS_NULL && theMatchType != ENumericMatchType.IS_NOT_NULL
            && (theIntegers == null || theIntegers.length == 0)) {
            throw new IllegalArgumentException("At least one match integer must be provided.");
        }

        this.matchType = theMatchType;
        this.integers = theIntegers == null || theIntegers.length == 0 ? new Integer[0] : theIntegers.clone();
    }

    /**
     * Gets the match type.
     *
     * @return the match type
     */
    public final ENumericMatchType getMatchType() {

        return this.matchType;
    }

    /**
     * Gets the number of integers in the criterion.
     *
     * @return the number of integers
     */
    public final int getNumIntegers() {

        return this.integers.length;
    }

    /**
     * Gets a specified integer from the list of integers in the criterion.
     *
     * @param index the index (from 0 to one less than the value returned by {@code getNumIntegers})
     * @return the integer
     */
    public final Integer getInteger(final int index) {

        return this.integers[index];
    }
}
