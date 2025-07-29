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
    public static FieldDef LEG_COURSE_DEF = new FieldDef("course", EFieldType.STRING,
            "The ID of a course, such as 'STAT 100'.  Legacy Math courses have abbreviated IDs of the form 'M 117',"
            + "but new courses use the full ID: 'MATH 117'.",
            new StringLengthConstraint("C_COURSE_LEN", 1, 10));

    /** 'course' field as a partition key. */
    public static Field LEG_COURSE_PK = new Field(LEG_COURSE_DEF, EFieldRole.PARTITION_KEY);


//    unit smallint not null ,
//    term char(2) not null ,
//    term_yr smallint not null ,
//    unit_exam_wgt decimal(3,2),
//    unit_desc char(50),
//    unit_timelimit smallint,
//    possible_score smallint,
//    nbr_questions smallint,
//    unit_type char(4)
}
