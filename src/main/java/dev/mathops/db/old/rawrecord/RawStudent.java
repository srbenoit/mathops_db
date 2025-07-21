package dev.mathops.db.old.rawrecord;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.rec.RecBase;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * A raw "student" record.
 */
public final class RawStudent extends RecBase implements Comparable<RawStudent> {

    /** The table name. */
    public static final String TABLE_NAME = "student";

    /** A comparator to allow student records to be sorted by CSI UD. */
    public static final CSUIDComparator CSUID_COMPARATOR = new CSUIDComparator();

    /** A field name. */
    private static final String FLD_STU_ID = "stu_id";

    /** A field name. */
    private static final String FLD_PIDM = "pidm";

    /** A field name. */
    private static final String FLD_LAST_NAME = "last_name";

    /** A field name. */
    private static final String FLD_FIRST_NAME = "first_name";

    /** A field name. */
    private static final String FLD_PREF_NAME = "pref_name";

    /** A field name. */
    private static final String FLD_MIDDLE_INITIAL = "middle_initial";

    /** A field name. */
    private static final String FLD_APLN_TERM = "apln_term";

    /** A field name. */
    private static final String FLD_CLASS = "class";

    /** A field name. */
    private static final String FLD_COLLEGE = "college";

    /** A field name. */
    private static final String FLD_DEPT = "dept";

    /** A field name. */
    private static final String FLD_PROGRAM_CODE = "program_code";

    /** A field name. */
    private static final String FLD_MINOR = "minor";

    /** A field name. */
    private static final String FLD_EST_GRADUATION = "est_graduation";

    /** A field name. */
    private static final String FLD_TR_CREDITS = "tr_credits";

    /** A field name. */
    private static final String FLD_HS_CODE = "hs_code";

    /** A field name. */
    private static final String FLD_HS_GPA = "hs_gpa";

    /** A field name. */
    private static final String FLD_HS_CLASS_RANK = "hs_class_rank";

    /** A field name. */
    private static final String FLD_HS_SIZE_CLASS = "hs_size_class";

    /** A field name. */
    private static final String FLD_ACT_SCORE = "act_score";

    /** A field name. */
    private static final String FLD_SAT_SCORE = "sat_score";

    /** A field name. */
    private static final String FLD_AP_SCORE = "ap_score";

    /** A field name. */
    private static final String FLD_RESIDENT = "resident";

    /** A field name. */
    private static final String FLD_BIRTHDATE = "birthdate";

    /** A field name. */
    private static final String FLD_ETHNICITY = "ethnicity";

    /** A field name. */
    private static final String FLD_GENDER = "gender";

    /** A field name. */
    private static final String FLD_DISCIP_HISTORY = "discip_history";

    /** A field name. */
    private static final String FLD_DISCIP_STATUS = "discip_status";

    /** A field name. */
    private static final String FLD_SEV_ADMIN_HOLD = "sev_admin_hold";

    /** A field name. */
    private static final String FLD_TIMELIMIT_FACTOR = "timelimit_factor";

    /** A field name. */
    private static final String FLD_LICENSED = "licensed";

    /** A field name. */
    private static final String FLD_CAMPUS = "campus";

    /** A field name. */
    private static final String FLD_STU_EMAIL = "stu_email";

    /** A field name. */
    private static final String FLD_ADVISER_EMAIL = "adviser_email";

    /** A field name. */
    private static final String FLD_PASSWORD = "password";

    /** A field name. */
    private static final String FLD_ADMIT_TYPE = "admit_type";

    /** A field name. */
    private static final String FLD_ORDER_ENFORCE = "order_enforce";

    /** A field name. */
    private static final String FLD_PACING_STRUCTURE = "pacing_structure";

    /** A field name. */
    private static final String FLD_CREATE_DT = "create_dt";

    /** A field name. */
    private static final String FLD_EXTENSION_DAYS = "extension_days";

    /** A field name. */
    private static final String FLD_CANVAS_ID = "canvas_id";

    /** The ID of a test student. */
    public static final String TEST_STUDENT_ID = "888888888";

    /** The PIDM of a test student. */
    public static final int TEST_STUDENT_INTERNAL_ID = 10567708;

    /** The 'stu_id' field value. */
    public String stuId;

    /** The 'pidm' field value. */
    public Integer pidm;

    /** The 'last_name' field value. */
    public String lastName;

    /** The 'first_name' field value. */
    public String firstName;

    /** The 'pref_name' field value. */
    public String prefName;

    /** The 'middle_initial' field value. */
    public String middleInitial;

    /** The 'apln_term' field value. */
    public TermKey aplnTerm;

    /** The 'class' field value. */
    public String clazz;

    /** The 'college' field value. */
    public String college;

    /** The 'dept' field value. */
    public String dept;

    /** The 'program_code' field value. */
    public String programCode;

    /** The 'minor' field value. */
    public String minor;

    /** The 'est_graduation' field value. */
    public TermKey estGraduation;

    /** The 'tr_credits' field value. */
    public String trCredits;

    /** The 'hs_code' field value. */
    public String hsCode;

    /** The 'hs_gpa' field value. */
    public String hsGpa;

    /** The 'hs_class_rank' field value. */
    public Integer hsClassRank;

    /** The 'hs_size_class' field value. */
    public Integer hsSizeClass;

    /** The 'act_score' field value. */
    public Integer actScore;

    /** The 'sat_score' field value. */
    public Integer satScore;

    /** The 'ap_score' field value. */
    public String apScore;

    /** The 'resident' field value. */
    public String resident;

    /** The 'birthdate' field value. */
    public LocalDate birthdate;

    /** The 'ethnicity' field value. */
    public String ethnicity;

    /** The 'gender' field value. */
    public String gender;

    /** The 'discip_history' field value. */
    public String discipHistory;

    /** The 'discip_status' field value. */
    public String discipStatus;

    /** The 'sev_admin_hold' field value. */
    public String sevAdminHold;

    /** The 'timelimit_factor' field value. */
    public Float timelimitFactor;

    /** The 'licensed' field value. */
    public String licensed;

    /** The 'campus' field value. */
    public String campus;

    /** The 'stu_email' field value. */
    public String stuEmail;

    /** The 'adviser_email' field value. */
    public String adviserEmail;

    /** The 'password' field value. */
    public String password;

    /** The 'admit_type' field value. */
    public String admitType;

    /** The 'order_enforce' field value. */
    public String orderEnforce;

    /** The 'pacing_structure' field value. */
    public String pacingStructure;

    /** The 'create_dt' field value. */
    public LocalDate createDt;

    /** The number of extension days the student should be given due to accommodations. */
    public Integer extensionDays;

    /** The student's Canvas ID. */
    public String canvasId;

    /**
     * Constructs a new {@code RawStudent}.
     */
    public RawStudent() {

        super();
    }

    /**
     * Constructs a new {@code RawStudent}.
     *
     * @param theStuId           the 'stu_id' field value
     * @param thePidm            the 'pidm' field value
     * @param theLastName        the 'last_name' field value
     * @param theFirstName       the 'first_name' field value
     * @param thePrefName        the 'pref_name' field value
     * @param theMiddleInitial   the 'middle_initial' field value
     * @param theAplnTerm        the 'apln_term' field value
     * @param theClass           the 'class' field value
     * @param theCollege         the 'college' field value
     * @param theDept            the 'dept' field value
     * @param theProgramCode     the 'program_code ' field value
     * @param theMinor           the 'minor' field value
     * @param theEstGraduation   the 'est_graduation' field value
     * @param theTrCredits       the 'tr_credits ' field value
     * @param theHsCode          the 'hs_code' field value
     * @param theHsGpa           the 'hs_gpa' field value
     * @param theHsClassRank     the 'hs_class_rank ' field value
     * @param theHsSizeClass     the 'hs_size_class' field value
     * @param theActScore        the 'act_score' field value
     * @param theSatScore        the 'sat_score ' field value
     * @param theApScore         the 'ap_score' field value
     * @param theResident        the 'resident' field value
     * @param theBirthdate       the 'birthdate ' field value
     * @param theEthnicity       the 'ethnicity' field value
     * @param theGender          the 'gender' field value
     * @param theDiscipHistory   the 'discip_history' field value
     * @param theDiscipStatus    the 'discip_status' field value
     * @param theSevAdminHold    the 'sev_admin_hold' field value
     * @param theTimelimitFactor the 'timelimit_factor' field value
     * @param theLicensed        the 'licensed' field value
     * @param theCampus          the 'campus' field value
     * @param theStuEmail        the 'stu_email' field value
     * @param theAdviserEmail    the 'adviser_email' field value
     * @param thePassword        the 'password' field value
     * @param theAdmitType       the 'admit_type' field value
     * @param theOrderEnforce    the 'order_enforce' field value
     * @param thePacingStructure the 'pacing_structure' field value
     * @param theCreateDt        the 'create_dt' field value
     * @param theExtensionDays   the 'extension_days' field value
     * @param theCanvasId        the 'canvas_id' field value
     */
    public RawStudent(final String theStuId, final Integer thePidm, final String theLastName,
                      final String theFirstName, final String thePrefName, final String theMiddleInitial,
                      final TermKey theAplnTerm, final String theClass, final String theCollege,
                      final String theDept, final String theProgramCode, final String theMinor,
                      final TermKey theEstGraduation, final String theTrCredits, final String theHsCode,
                      final String theHsGpa, final Integer theHsClassRank, final Integer theHsSizeClass,
                      final Integer theActScore, final Integer theSatScore, final String theApScore,
                      final String theResident, final LocalDate theBirthdate, final String theEthnicity,
                      final String theGender, final String theDiscipHistory, final String theDiscipStatus,
                      final String theSevAdminHold, final Float theTimelimitFactor, final String theLicensed,
                      final String theCampus, final String theStuEmail, final String theAdviserEmail,
                      final String thePassword, final String theAdmitType, final String theOrderEnforce,
                      final String thePacingStructure, final LocalDate theCreateDt,
                      final Integer theExtensionDays, final String theCanvasId) {

        super();

        this.stuId = theStuId;
        this.pidm = thePidm;
        this.lastName = theLastName;
        this.firstName = theFirstName;
        this.prefName = thePrefName;
        this.middleInitial = theMiddleInitial;
        this.aplnTerm = theAplnTerm;
        this.clazz = theClass;
        this.college = theCollege;
        this.dept = theDept;
        this.programCode = theProgramCode;
        this.minor = theMinor;
        this.estGraduation = theEstGraduation;
        this.trCredits = theTrCredits;
        this.hsCode = theHsCode;
        this.hsGpa = theHsGpa;
        this.hsClassRank = theHsClassRank;
        this.hsSizeClass = theHsSizeClass;
        this.actScore = theActScore;
        this.satScore = theSatScore;
        this.apScore = theApScore;
        this.resident = theResident;
        this.birthdate = theBirthdate;
        this.ethnicity = theEthnicity;
        this.gender = theGender;
        this.discipHistory = theDiscipHistory;
        this.discipStatus = theDiscipStatus;
        this.sevAdminHold = theSevAdminHold;
        this.timelimitFactor = theTimelimitFactor;
        this.licensed = theLicensed;
        this.campus = theCampus;
        this.stuEmail = theStuEmail;
        this.adviserEmail = theAdviserEmail;
        this.password = thePassword;
        this.admitType = theAdmitType;
        this.orderEnforce = theOrderEnforce;
        this.pacingStructure = thePacingStructure;
        this.createDt = theCreateDt;
        this.extensionDays = theExtensionDays;
        this.canvasId = theCanvasId;
    }

    /**
     * Gets the "screen name" of the student, which is the preferred or first name, then a space, then the last name.
     *
     * @return the screen name
     */
    public String getScreenName() {

        final String useFirst = this.prefName == null ? this.firstName : this.prefName;

        return useFirst + CoreConstants.SPC + this.lastName;
    }

    /**
     * Extracts a "student" record from a result set.
     *
     * @param rs the result set from which to retrieve the record
     * @return the record
     * @throws SQLException if there is an error accessing the database
     */
    public static RawStudent fromResultSet(final ResultSet rs) throws SQLException {

        final RawStudent result = new RawStudent();

        result.stuId = getStringField(rs, FLD_STU_ID);
        result.pidm = getIntegerField(rs, FLD_PIDM);
        result.lastName = getStringField(rs, FLD_LAST_NAME);
        result.firstName = getStringField(rs, FLD_FIRST_NAME);
        result.prefName = getStringField(rs, FLD_PREF_NAME);
        result.middleInitial = getStringField(rs, FLD_MIDDLE_INITIAL);
        result.aplnTerm = getShortTermStringField(rs, FLD_APLN_TERM);
        result.clazz = getStringField(rs, FLD_CLASS);
        result.college = getStringField(rs, FLD_COLLEGE);
        result.dept = getStringField(rs, FLD_DEPT);
        result.programCode = getStringField(rs, FLD_PROGRAM_CODE);
        result.minor = getStringField(rs, FLD_MINOR);
        result.estGraduation = getShortTermStringField(rs, FLD_EST_GRADUATION);
        result.trCredits = getStringField(rs, FLD_TR_CREDITS);
        result.hsCode = getStringField(rs, FLD_HS_CODE);
        result.hsGpa = getStringField(rs, FLD_HS_GPA);
        result.hsClassRank = getIntegerField(rs, FLD_HS_CLASS_RANK);
        result.hsSizeClass = getIntegerField(rs, FLD_HS_SIZE_CLASS);
        result.actScore = getIntegerField(rs, FLD_ACT_SCORE);
        result.satScore = getIntegerField(rs, FLD_SAT_SCORE);
        result.apScore = getStringField(rs, FLD_AP_SCORE);
        result.resident = getStringField(rs, FLD_RESIDENT);
        result.birthdate = getDateField(rs, FLD_BIRTHDATE);
        result.ethnicity = getStringField(rs, FLD_ETHNICITY);
        result.gender = getStringField(rs, FLD_GENDER);
        result.discipHistory = getStringField(rs, FLD_DISCIP_HISTORY);
        result.discipStatus = getStringField(rs, FLD_DISCIP_STATUS);
        result.sevAdminHold = getStringField(rs, FLD_SEV_ADMIN_HOLD);
        result.timelimitFactor = getFloatField(rs, FLD_TIMELIMIT_FACTOR);
        result.licensed = getStringField(rs, FLD_LICENSED);
        result.campus = getStringField(rs, FLD_CAMPUS);
        result.stuEmail = getStringField(rs, FLD_STU_EMAIL);
        result.adviserEmail = getStringField(rs, FLD_ADVISER_EMAIL);
        result.password = getStringField(rs, FLD_PASSWORD);
        result.admitType = getStringField(rs, FLD_ADMIT_TYPE);
        result.orderEnforce = getStringField(rs, FLD_ORDER_ENFORCE);
        result.pacingStructure = getStringField(rs, FLD_PACING_STRUCTURE);
        result.createDt = getDateField(rs, FLD_CREATE_DT);

        // Temporary catch blocks until tables are altered to match
        try {
            result.extensionDays = getIntegerField(rs, FLD_EXTENSION_DAYS);
        } catch (final SQLException ex) {
            result.extensionDays = null;
        }
        try {
            result.canvasId = getStringField(rs, FLD_CANVAS_ID);
        } catch (final SQLException ex) {
            result.canvasId = null;
        }

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
    public int compareTo(final RawStudent o) {

        int result = compareAllowingNull(this.lastName.toUpperCase(Locale.ROOT), o.lastName.toUpperCase(Locale.ROOT));

        if (result == 0) {
            result = compareAllowingNull(this.firstName.toUpperCase(Locale.ROOT), o.firstName.toUpperCase(Locale.ROOT));
            if (result == 0) {
                result = compareAllowingNull(this.middleInitial, o.middleInitial);
                if (result == 0) {
                    result = compareAllowingNull(this.prefName, o.prefName);
                    if (result == 0) {
                        result = compareAllowingNull(this.stuId, o.stuId);
                    }
                }
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

        final HtmlBuilder htm = new HtmlBuilder(40);

        appendField(htm, FLD_STU_ID, this.stuId);
        htm.add(DIVIDER);
        appendField(htm, FLD_PIDM, this.pidm);
        htm.add(DIVIDER);
        appendField(htm, FLD_LAST_NAME, this.lastName);
        htm.add(DIVIDER);
        appendField(htm, FLD_FIRST_NAME, this.firstName);
        htm.add(DIVIDER);
        appendField(htm, FLD_PREF_NAME, this.prefName);
        htm.add(DIVIDER);
        appendField(htm, FLD_MIDDLE_INITIAL, this.middleInitial);
        htm.add(DIVIDER);
        appendField(htm, FLD_APLN_TERM, this.aplnTerm);
        htm.add(DIVIDER);
        appendField(htm, FLD_CLASS, this.clazz);
        htm.add(DIVIDER);
        appendField(htm, FLD_COLLEGE, this.college);
        htm.add(DIVIDER);
        appendField(htm, FLD_DEPT, this.dept);
        htm.add(DIVIDER);
        appendField(htm, FLD_PROGRAM_CODE, this.programCode);
        htm.add(DIVIDER);
        appendField(htm, FLD_MINOR, this.minor);
        htm.add(DIVIDER);
        appendField(htm, FLD_EST_GRADUATION, this.estGraduation);
        htm.add(DIVIDER);
        appendField(htm, FLD_TR_CREDITS, this.trCredits);
        htm.add(DIVIDER);
        appendField(htm, FLD_HS_CODE, this.hsCode);
        htm.add(DIVIDER);
        appendField(htm, FLD_HS_GPA, this.hsGpa);
        htm.add(DIVIDER);
        appendField(htm, FLD_HS_CLASS_RANK, this.hsClassRank);
        htm.add(DIVIDER);
        appendField(htm, FLD_HS_SIZE_CLASS, this.hsSizeClass);
        htm.add(DIVIDER);
        appendField(htm, FLD_ACT_SCORE, this.actScore);
        htm.add(DIVIDER);
        appendField(htm, FLD_SAT_SCORE, this.satScore);
        htm.add(DIVIDER);
        appendField(htm, FLD_AP_SCORE, this.apScore);
        htm.add(DIVIDER);
        appendField(htm, FLD_RESIDENT, this.resident);
        htm.add(DIVIDER);
        appendField(htm, FLD_BIRTHDATE, this.birthdate);
        htm.add(DIVIDER);
        appendField(htm, FLD_ETHNICITY, this.ethnicity);
        htm.add(DIVIDER);
        appendField(htm, FLD_GENDER, this.gender);
        htm.add(DIVIDER);
        appendField(htm, FLD_DISCIP_HISTORY, this.discipHistory);
        htm.add(DIVIDER);
        appendField(htm, FLD_DISCIP_STATUS, this.discipStatus);
        htm.add(DIVIDER);
        appendField(htm, FLD_SEV_ADMIN_HOLD, this.sevAdminHold);
        htm.add(DIVIDER);
        appendField(htm, FLD_TIMELIMIT_FACTOR, this.timelimitFactor);
        htm.add(DIVIDER);
        appendField(htm, FLD_LICENSED, this.licensed);
        htm.add(DIVIDER);
        appendField(htm, FLD_CAMPUS, this.campus);
        htm.add(DIVIDER);
        appendField(htm, FLD_STU_EMAIL, this.stuEmail);
        htm.add(DIVIDER);
        appendField(htm, FLD_ADVISER_EMAIL, this.adviserEmail);
        htm.add(DIVIDER);
        appendField(htm, FLD_PASSWORD, this.password);
        htm.add(DIVIDER);
        appendField(htm, FLD_ADMIT_TYPE, this.admitType);
        htm.add(DIVIDER);
        appendField(htm, FLD_ORDER_ENFORCE, this.orderEnforce);
        htm.add(DIVIDER);
        appendField(htm, FLD_PACING_STRUCTURE, this.pacingStructure);
        htm.add(DIVIDER);
        appendField(htm, FLD_CREATE_DT, this.createDt);
        htm.add(DIVIDER);
        appendField(htm, FLD_EXTENSION_DAYS, this.extensionDays);
        htm.add(DIVIDER);
        appendField(htm, FLD_CANVAS_ID, this.canvasId);

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
               + Objects.hashCode(this.pidm)
               + Objects.hashCode(this.lastName)
               + Objects.hashCode(this.firstName)
               + Objects.hashCode(this.prefName)
               + Objects.hashCode(this.middleInitial)
               + Objects.hashCode(this.aplnTerm)
               + Objects.hashCode(this.clazz)
               + Objects.hashCode(this.college)
               + Objects.hashCode(this.dept)
               + Objects.hashCode(this.programCode)
               + Objects.hashCode(this.minor)
               + Objects.hashCode(this.estGraduation)
               + Objects.hashCode(this.trCredits)
               + Objects.hashCode(this.hsCode)
               + Objects.hashCode(this.hsGpa)
               + Objects.hashCode(this.hsClassRank)
               + Objects.hashCode(this.hsSizeClass)
               + Objects.hashCode(this.actScore)
               + Objects.hashCode(this.satScore)
               + Objects.hashCode(this.apScore)
               + Objects.hashCode(this.resident)
               + Objects.hashCode(this.birthdate)
               + Objects.hashCode(this.ethnicity)
               + Objects.hashCode(this.gender)
               + Objects.hashCode(this.discipHistory)
               + Objects.hashCode(this.discipStatus)
               + Objects.hashCode(this.sevAdminHold)
               + Objects.hashCode(this.timelimitFactor)
               + Objects.hashCode(this.licensed)
               + Objects.hashCode(this.campus)
               + Objects.hashCode(this.stuEmail)
               + Objects.hashCode(this.adviserEmail)
               + Objects.hashCode(this.password)
               + Objects.hashCode(this.admitType)
               + Objects.hashCode(this.orderEnforce)
               + Objects.hashCode(this.pacingStructure)
               + Objects.hashCode(this.createDt)
               + Objects.hashCode(this.extensionDays)
               + Objects.hashCode(this.canvasId);
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
        } else if (obj instanceof final RawStudent rec) {
            equal = Objects.equals(this.stuId, rec.stuId)
                    && Objects.equals(this.pidm, rec.pidm)
                    && Objects.equals(this.lastName, rec.lastName)
                    && Objects.equals(this.firstName, rec.firstName)
                    && Objects.equals(this.prefName, rec.prefName)
                    && Objects.equals(this.middleInitial, rec.middleInitial)
                    && Objects.equals(this.aplnTerm, rec.aplnTerm)
                    && Objects.equals(this.clazz, rec.clazz)
                    && Objects.equals(this.college, rec.college)
                    && Objects.equals(this.dept, rec.dept)
                    && Objects.equals(this.programCode, rec.programCode)
                    && Objects.equals(this.minor, rec.minor)
                    && Objects.equals(this.estGraduation, rec.estGraduation)
                    && Objects.equals(this.trCredits, rec.trCredits)
                    && Objects.equals(this.hsCode, rec.hsCode)
                    && Objects.equals(this.hsGpa, rec.hsGpa)
                    && Objects.equals(this.hsClassRank, rec.hsClassRank)
                    && Objects.equals(this.hsSizeClass, rec.hsSizeClass)
                    && Objects.equals(this.actScore, rec.actScore)
                    && Objects.equals(this.satScore, rec.satScore)
                    && Objects.equals(this.apScore, rec.apScore)
                    && Objects.equals(this.resident, rec.resident)
                    && Objects.equals(this.birthdate, rec.birthdate)
                    && Objects.equals(this.ethnicity, rec.ethnicity)
                    && Objects.equals(this.gender, rec.gender)
                    && Objects.equals(this.discipHistory, rec.discipHistory)
                    && Objects.equals(this.discipStatus, rec.discipStatus)
                    && Objects.equals(this.sevAdminHold, rec.sevAdminHold)
                    && Objects.equals(this.timelimitFactor, rec.timelimitFactor)
                    && Objects.equals(this.licensed, rec.licensed)
                    && Objects.equals(this.campus, rec.campus)
                    && Objects.equals(this.stuEmail, rec.stuEmail)
                    && Objects.equals(this.adviserEmail, rec.adviserEmail)
                    && Objects.equals(this.password, rec.password)
                    && Objects.equals(this.admitType, rec.admitType)
                    && Objects.equals(this.orderEnforce, rec.orderEnforce)
                    && Objects.equals(this.pacingStructure, rec.pacingStructure)
                    && Objects.equals(this.createDt, rec.createDt)
                    && Objects.equals(this.extensionDays, rec.extensionDays)
                    && Objects.equals(this.canvasId, rec.canvasId);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * A comparator to allow students to be sorted by CSU ID.
     */
    public static final class CSUIDComparator implements Comparator<RawStudent> {

        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first
         * argument is less than, equal to, or greater than the second.
         *
         * @param o1 the first object
         * @param o2 the second object
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
         *         greater than the second
         */
        public int compare(final RawStudent o1, final RawStudent o2) {

            final int result;

            if (o1.stuId == null) {
                result = o2.stuId == null ? 0 : -1;
            } else if (o2.stuId == null) {
                result = 1;
            } else {
                result = o1.stuId.compareTo(o2.stuId);
            }

            return result;
        }
    }
}
