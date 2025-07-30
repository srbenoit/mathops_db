package dev.mathops.db.table.criteria;

import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;

/**
 *  A criterion that can be used to match a Boolean field.
 */
public class BooleanFieldCriterion extends AbstractFieldCriterion {

    /** The match type. */
    final EBooleanMatchType matchType;

    /**
     * Constructs a new {@code BooleanFieldCriterion}.
     *
     * @param theField the field
     * @param theMatchType the type of match to perform
     */
    public BooleanFieldCriterion(final Field theField, final EBooleanMatchType theMatchType) {

        super(theField);

        final FieldDef def = theField.getDef();
        if (def.getType() != EFieldType.BOOLEAN) {
            throw new IllegalArgumentException("A Boolean field criterion must be used with a Boolean field");
        }
        if (theMatchType == null) {
            throw new IllegalArgumentException("The match type may not be null");
        }

        this.matchType = theMatchType;
    }
}
