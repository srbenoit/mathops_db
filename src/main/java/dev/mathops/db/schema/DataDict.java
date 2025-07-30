package dev.mathops.db.schema;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.constraint.StringLengthConstraint;

/**
 * The collection of all field definitions used in all schemas.
 */
public enum DataDict {
    ;

    /** 'course' field (when used as a partition key) from the Legacy schema. */
    public static final FieldDef LEG_COURSE_DEF = new FieldDef("course", EFieldType.STRING,
            "The ID of a course, such as 'STAT 100'.  Legacy Math courses have abbreviated IDs of the form 'M 117',"
            + "but new courses use the full ID: 'MATH 117'.",
            new StringLengthConstraint("C_COURSE_LEN", 1, 10));

    /** 'course' field as a partition key. */
    public static final Field LEG_COURSE_PK = new Field(LEG_COURSE_DEF, EFieldRole.PARTITION_KEY);


}
