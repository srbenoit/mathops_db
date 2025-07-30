package dev.mathops.db.old.rawlogic;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.rec.TermRec;
import dev.mathops.db.type.TermKey;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class to work with "stcourse" records.
 *
 * <pre>
 * Table:  'stcourse'
 *
 * Column name          Type                      Nulls   Key
 * -------------------  ------------------------  ------  -----
 * stu_id               char(9)                   no      PK
 * course               char(6)                   no      PK
 * sect                 char(4)                   no      PK
 * term                 char(2)                   no      PK
 * term_yr              smallint                  no      PK
 * pace_order           smallint                  yes
 * open_status          char(1)                   yes
 * grading_option       char(2)                   yes
 * completed            char(1)                   no
 * score                smallint                  yes
 * course_grade         char(2)                   yes
 * prereq_satis         char(1)                   yes
 * init_class_roll      char(1)                   no
 * stu_provided         char(1)                   yes
 * final_class_roll     char(1)                   no
 * exam_placed          char(1)                   yes
 * zero_unit            smallint                  yes
 * timeout_factor       decimal(3,2)              yes
 * forfeit_i            char(1)                   yes
 * i_in_progress        char(1)                   no
 * i_counted            char(1)                   yes
 * ctrl_test            char(1)                   no
 * deferred_f_dt        date                      yes
 * bypass_timeout       smallint                  no
 * instrn_type          char(2)                   yes
 * registration_stat+   char(2)                   yes
 * last_class_roll_dt   date                      yes      PK
 * i_term               char(2)                   yes
 * i_term_yr            smallint                  yes
 * i_deadline_dt        date                      yes
 * </pre>
 */
public enum RawStcourseLogic {
    ;

    /** A commonly used string. */
    private static final String AND_NOT_OT = " AND instrn_type!='OT'";

    /** A commonly used string. */
    private static final String AND_NOT_DROPPED = " AND (open_status IS NULL OR open_status!='D')";

    /** Empty list to return when there are no records. */
    private static final List<RawStcourse> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>(0));

    /**
     * Gets the qualified table name for a LEGACY table based on the Cache being used.
     *
     * @param cache the data cache
     */
    static String getTableName(final Cache cache) {

        final String schemaPrefix = cache.getSchemaPrefix(ESchema.LEGACY);

        return schemaPrefix == null ? "stcourse" : (schemaPrefix + ".stcourse");
    }

    /**
     * Inserts a new record.
     *
     * @param cache  the data cache
     * @param record the record to insert
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean insert(final Cache cache, final RawStcourse record) throws SQLException {

        if (record.stuId == null) {
            throw new SQLException("Null value in 'stuId' field.");
        }
        if (record.course == null) {
            throw new SQLException("Null value in 'course' field.");
        }
        if (record.sect == null) {
            throw new SQLException("Null value in 'sect' field.");
        }
        if (record.termKey == null) {
            throw new SQLException("Null value in 'termKey' field.");
        }
        if (record.completed == null) {
            throw new SQLException("Null value in 'completed' field.");
        }
        if (record.initClassRoll == null) {
            throw new SQLException("Null value in 'initClassRoll' field.");
        }
        if (record.finalClassRoll == null) {
            throw new SQLException("Null value in 'finalClassRoll' field.");
        }
        if (record.iInProgress == null) {
            throw new SQLException("Null value in 'iInProgress' field.");
        }
        if (record.ctrlTest == null) {
            throw new SQLException("Null value in 'ctrlTest' field.");
        }
        if (record.bypassTimeout == null) {
            throw new SQLException("Null value in 'bypassTimeout' field.");
        }
        if (record.lastClassRollDt == null) {
            throw new SQLException("Null value in 'lastClassRollDt' field.");
        }

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "INSERT INTO ", tableName, " (stu_id,course,sect,term,term_yr,pace_order,open_status,grading_option,",
                "completed,score,course_grade,prereq_satis,init_class_roll,stu_provided,final_class_roll,exam_placed,",
                "zero_unit,timeout_factor,forfeit_i,i_in_progress,i_counted,ctrl_test,deferred_f_dt,bypass_timeout,",
                "instrn_type,registration_status,last_class_roll_dt,i_term,i_term_yr,i_deadline_dt) VALUES (",
                conn.sqlStringValue(record.stuId), ",",
                conn.sqlStringValue(record.course), ",",
                conn.sqlStringValue(record.sect), ",",
                conn.sqlStringValue(record.termKey.termCode), ",",
                conn.sqlIntegerValue(record.termKey.shortYear), ",",
                conn.sqlIntegerValue(record.paceOrder), ",",
                conn.sqlStringValue(record.openStatus), ",",
                conn.sqlStringValue(record.gradingOption), ",",
                conn.sqlStringValue(record.completed), ",",
                conn.sqlIntegerValue(record.score), ",",
                conn.sqlStringValue(record.courseGrade), ",",
                conn.sqlStringValue(record.prereqSatis), ",",
                conn.sqlStringValue(record.initClassRoll), ",",
                conn.sqlStringValue(record.stuProvided), ",",
                conn.sqlStringValue(record.finalClassRoll), ",",
                conn.sqlStringValue(record.examPlaced), ",",
                conn.sqlIntegerValue(record.zeroUnit), ",",
                conn.sqlFloatValue(record.timeoutFactor), ",",
                conn.sqlStringValue(record.forfeitI), ",",
                conn.sqlStringValue(record.iInProgress), ",",
                conn.sqlStringValue(record.iCounted), ",",
                conn.sqlStringValue(record.ctrlTest), ",",
                conn.sqlDateValue(record.deferredFDt), ",",
                conn.sqlIntegerValue(record.bypassTimeout), ",",
                conn.sqlStringValue(record.instrnType), ",",
                conn.sqlStringValue(record.registrationStatus), ",",
                conn.sqlDateValue(record.lastClassRollDt), ",",
                conn.sqlStringValue(record.iTermKey == null ? null : record.iTermKey.termCode), ",",
                conn.sqlIntegerValue(record.iTermKey == null ? null : record.iTermKey.shortYear), ",",
                conn.sqlDateValue(record.iDeadlineDt), ")");

        try (final Statement stmt = conn.createStatement()) {
            final int count = stmt.executeUpdate(sql);
            final boolean result = count == 1;

            if (result) {
                conn.commit();
            } else {
                Log.warning("Insert into '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                conn.rollback();
            }

            return result;
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Deletes a record.
     *
     * @param cache  the data cache
     * @param record the record to delete
     * @return {@code true} if successful; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public static boolean delete(final Cache cache, final RawStcourse record) throws SQLException {

        final boolean result;

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final HtmlBuilder builder = new HtmlBuilder(100);

        builder.add("DELETE FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(record.stuId),
                "   AND course=", conn.sqlStringValue(record.course),
                "   AND sect=", conn.sqlStringValue(record.sect),
                "   AND term=", conn.sqlStringValue(record.termKey.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(record.termKey.shortYear));

        if (record.openStatus == null) {
            builder.add("   AND open_status IS NULL");
        } else {
            builder.add("   AND open_status=", conn.sqlStringValue(record.openStatus));
        }

        if (record.lastClassRollDt == null) {
            builder.add("   AND last_class_roll_dt IS NULL");
        } else {
            builder.add("   AND last_class_roll_dt=", conn.sqlDateValue(record.lastClassRollDt));
        }

        final String sql = builder.toString();

        try (final Statement stmt = conn.createStatement()) {
            final int count = stmt.executeUpdate(sql);
            result = count == 1;

            if (result) {
                conn.commit();
            } else {
                Log.warning("Delete from '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                conn.rollback();
            }
        } catch (final SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Gets all records.
     *
     * @param cache the data cache
     * @return the list of records
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStcourse> queryAll(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all students registered for a particular term. This method does not automatically scan for
     * "provisional" satisfaction of prerequisites, and does not return synthetic test data records.
     *
     * @param cache          the database connection, checked out to this thread
     * @param term           the active term
     * @param includeOT      true to include OT records
     * @param includeDropped true to include dropped records
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryByTerm(final Cache cache, final TermKey term, final boolean includeOT,
                                                final boolean includeDropped) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final HtmlBuilder sql = new HtmlBuilder(100);

        sql.add("SELECT * FROM ", tableName, " WHERE term='", term.termCode, "' AND term_yr=", term.shortYear);

        if (!includeOT) {
            sql.add(AND_NOT_OT);
        }
        if (!includeDropped) {
            sql.add(AND_NOT_DROPPED);
        }

        try {
            return executeQuery(conn, sql.toString());
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all students registered for a particular term. This method does not automatically scan for
     * "provisional" satisfaction of prerequisites, and does not return synthetic test data records.
     *
     * @param cache          the database connection, checked out to this thread
     * @param term           the active term
     * @param course         the course ID
     * @param sect           the section
     * @param includeDropped true to include dropped records
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryByTermCourseSection(final Cache cache, final TermKey term,
                                                             final String course, final String sect,
                                                             final boolean includeDropped) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final HtmlBuilder sql = new HtmlBuilder(100);

        sql.add("SELECT * FROM ", tableName, " WHERE course='", course, "' AND sect='", sect, "' AND term='",
                term.termCode, "' AND term_yr=", term.shortYear);

        if (!includeDropped) {
            sql.add(AND_NOT_DROPPED);
        }

        try {
            return executeQuery(conn, sql.toString());
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Queries for all registration records for in-progress incompletes.
     *
     * @param cache the database connection, checked out to this thread
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryOpenIncompletes(final Cache cache) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        try {
            return executeQuery(conn, "SELECT * FROM " + tableName + " WHERE i_in_progress='Y'");
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all active registrations for the active term. This skips OT, dropped, and forfeit registrations. This
     * method scans for "provisional" satisfaction of prerequisites, but does not return synthetic test data records.
     *
     * @param cache the data cache
     * @return the corresponding list of registrations; {@code null} on any error
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryActiveForActiveTerm(final Cache cache) throws SQLException {

        final SystemData systemData = cache.getSystemData();
        final TermRec activeTerm = systemData.getActiveTerm();

        // The following returns all except "OT" and "Dropped" records

        final List<RawStcourse> result = queryByTerm(cache, activeTerm.term, false, false);

        // Remove "Forfeit" registrations as well
        final Iterator<RawStcourse> iter = result.iterator();
        while (iter.hasNext()) {
            final RawStcourse stc = iter.next();
            if ("G".equals(stc.openStatus)) {
                iter.remove();
            }
            testProvisionalPrereqSatisfied(stc);
        }

        return result;
    }

    /**
     * Students in CSU Online sections (801/809) are allowed to take MATH 117 without having the prerequisite satisfied,
     * in which case they receive the ELM exam as the skills review exam. This method tests for this condition and set a
     * flag to indicate "provisional satisfaction" of prerequisites. This condition does not apply to "resident" student
     * taking 401 sections.
     *
     * @param row the record whose status to test
     * @return true if the record matched the criteria and was set with prerequisite provisionally satisfied
     */
    public static boolean testProvisionalPrereqSatisfied(final RawStcourse row) {

        boolean found = false;

        if (RawRecordConstants.M117.equals(row.course) && ("801".equals(row.sect) || "809".equals(row.sect))) {
            if (row.prereqSatis == null || "N".equals(row.prereqSatis)) {
                row.prereqSatis = "P";
                found = true;
            }
        }

        return found;
    }

    /**
     * Queries for all registration records for a student regardless of term. This method does not automatically scan
     * for "provisional" satisfaction of prerequisites. If a test student ID is provided, it will return synthetic test
     * data records.
     *
     * @param cache          the data cache
     * @param studentId      the ID of the student to query
     * @param includeOT      true to include OT records
     * @param includeDropped true to include dropped records
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryByStudent(final Cache cache, final String studentId,
                                                   final boolean includeOT, final boolean includeDropped)
            throws SQLException {

        final List<RawStcourse> result;

        if (studentId.startsWith("99")) {
            result = queryByStudentTestData(cache, studentId);
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final HtmlBuilder sql = new HtmlBuilder(120);

            sql.add("SELECT * FROM ", tableName, " WHERE stu_id='", studentId, "'");

            if (!includeOT) {
                sql.add(AND_NOT_OT);
            }
            if (!includeDropped) {
                sql.add(AND_NOT_DROPPED);
            }

            try {
                result = executeQuery(conn, sql.toString());
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Queries for all courses the student is registered in a particular term.
     *
     * @param cache          the data cache
     * @param studentId      the ID of the student to query
     * @param termKey        the active term key
     * @param includeOT      true to include OT records
     * @param includeDropped true to include dropped records
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryByStudent(final Cache cache, final String studentId, final TermKey termKey,
                                                   final boolean includeOT, final boolean includeDropped)
            throws SQLException {

        final List<RawStcourse> result;

        if (studentId.startsWith("99")) {
            result = queryByStudentTestData(cache, studentId, termKey);
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final HtmlBuilder sql = new HtmlBuilder(160);

            sql.add("SELECT * FROM ", tableName,
                    " WHERE stu_id='", studentId, "'",
                    " AND term='", termKey.termCode, "'",
                    " AND term_yr=", termKey.shortYear);

            if (!includeOT) {
                sql.add(AND_NOT_OT);
            }
            if (!includeDropped) {
                sql.add(AND_NOT_DROPPED);
            }

            try {
                result = executeQuery(conn, sql.toString());
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Queries for all OT credit records for a student.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> queryCreditByExam(final Cache cache, final String studentId) throws SQLException {

        final String tableName = getTableName(cache);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        final String sql = SimpleBuilder.concat(
                "SELECT * FROM ", tableName, " WHERE instrn_type='OT' ",
                "AND stu_id=", conn.sqlStringValue(studentId),
                " AND (exam_placed='F' OR exam_placed='M')");

        try {
            return executeQuery(conn, sql);
        } finally {
            Cache.checkInConnection(conn);
        }
    }

    /**
     * Retrieves all active registrations for a student. Includes OT, but not dropped for forfeit.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @param termKey   the term key of the term of interest
     * @return the corresponding list of registrations; {@code null} on any error
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> getActiveForStudent(final Cache cache, final String studentId,
                                                        final TermKey termKey) throws SQLException {

        final List<RawStcourse> result = queryByStudent(cache, studentId, termKey, true, false);

        result.removeIf(reg -> "G".equals(reg.openStatus));

        return result;
    }

    /**
     * Retrieves all registrations for a student that contribute to the student's pace.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @return the corresponding list of registrations; {@code null} on any error
     * @throws SQLException if there was an error accessing the database
     */
    public static List<RawStcourse> getPaced(final Cache cache, final String studentId) throws SQLException {

        final TermRec activeTerm = cache.getSystemData().getActiveTerm();

        // Get all current term non-OT credit registrations (may include incompletes)

        final List<RawStcourse> result = queryByStudent(cache, studentId, activeTerm.term, false, false);

        // Remove "ignored" and incompletes that are not counted in pace

        // TODO: We really should check the section or rule set to see if these are "paced" courses,
        //  but for now all courses are "paced".

        final Iterator<RawStcourse> iter = result.iterator();
        while (iter.hasNext()) {
            final RawStcourse reg = iter.next();

            if ("G".equals(reg.openStatus)) {
                iter.remove();
            }
            if ("Y".equals(reg.iInProgress) && "N".equals(reg.iCounted)) {
                iter.remove();
            }
        }

        return result;
    }

    /**
     * Retrieve the registration for a student in a course, if any. This will return OT rows, but not Dropped rows. It
     * scans for provisional satisfaction of prerequisites.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @param courseId  the ID of the course
     * @return the course registration, if one exists; {@code null} if not or if an error occurs
     * @throws SQLException if there is an error performing the query
     */
    public static RawStcourse getRegistration(final Cache cache, final String studentId,
                                              final String courseId) throws SQLException {

        final TermRec activeTerm = cache.getSystemData().getActiveTerm();

        final String tableName = getTableName(cache);

        final HtmlBuilder sql = new HtmlBuilder(160);

        final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

        sql.add("SELECT * FROM ", tableName,
                " WHERE stu_id=", conn.sqlStringValue(studentId),
                "   AND course=", conn.sqlStringValue(courseId),
                "   AND term=", conn.sqlStringValue(activeTerm.term.termCode),
                "   AND term_yr=", conn.sqlIntegerValue(activeTerm.term.shortYear),
                AND_NOT_DROPPED);

        RawStcourse result = null;

        try {
            final List<RawStcourse> list = executeQuery(conn, sql.toString());

        if (!list.isEmpty()) {
            result = list.getFirst();

            if (list.size() > 1) {
                Log.warning("Multiple registrations for ", studentId, " in ", courseId);
            }

            testProvisionalPrereqSatisfied(result);
        }
        } finally {
            Cache.checkInConnection(conn);
        }

        return result;
    }

    /**
     * Retrieves all registrations for a student marked as completed regardless of term.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @return the corresponding list of registrations; {@code null} on any error
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> getAllPriorCompleted(final Cache cache, final String studentId)
            throws SQLException {

        final List<RawStcourse> result = queryByStudent(cache, studentId, true, false);

        result.removeIf(rawStcourse -> !"Y".equals(rawStcourse.completed));

        return result;
    }

    /**
     * Retrieves all registrations for a student from a term other than the active term.
     *
     * @param cache         the data
     * @param studentId     the ID of the student
     * @param theActiveTerm the active term
     * @return the corresponding list of registrations; {@code null} on any error
     * @throws SQLException if there is an error performing the query
     */
    public static List<RawStcourse> getAllPrior(final Cache cache, final String studentId,
                                                final TermKey theActiveTerm) throws SQLException {

        final List<RawStcourse> result = queryByStudent(cache, studentId, true, false);

        result.removeIf(reg -> theActiveTerm.equals(reg.termKey));

        return result;
    }

    /**
     * Retrieves all registrations for a student (including dropped records).
     *
     * @param cache     the data cache
     * @param studentId the ID of the student
     * @return the corresponding list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public static List<RawStcourse> getHistory(final Cache cache, final String studentId)
            throws SQLException {

        final List<RawStcourse> result = queryByStudent(cache, studentId, true, true);

        for (final RawStcourse rawStcourse : result) {
            testProvisionalPrereqSatisfied(rawStcourse);
        }

        return result;
    }

    /**
     * Updates the pace order field in a student course.
     *
     * @param cache        the data cache
     * @param stuId        the student ID of the record to be updated
     * @param course       the course of the record to be updated
     * @param sect         the section number of the record to be updated
     * @param termKey      the term key of the record to be updated
     * @param newPaceOrder the new pace order
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updatePaceOrder(final Cache cache, final String stuId, final String course,
                                          final String sect, final TermKey termKey, final Integer newPaceOrder)
            throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of pace order of RawStcourse for test student");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET pace_order=", conn.sqlIntegerValue(newPaceOrder),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the open status field, the "on final class roll" status, and the last class roll date in a student
     * course.
     *
     * @param cache              the data cache
     * @param stuId              the student ID of the record to be updated
     * @param course             the course of the record to be updated
     * @param sect               the section number of the record to be updated
     * @param termKey            the term key of the record to be updated
     * @param newOpenStatus      the new open status
     * @param newFinalClassRoll  the new "on final class roll" status
     * @param newLastClassRollDt the new "last class roll date" field value
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateOpenStatusAndFinalClassRoll(final Cache cache, final String stuId, final String course,
                                                            final String sect, final TermKey termKey,
                                                            final String newOpenStatus, final String newFinalClassRoll,
                                                            final LocalDate newLastClassRollDt) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of open status order of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET open_status=", conn.sqlStringValue(newOpenStatus), ",",
                    "     final_class_roll=", conn.sqlStringValue(newFinalClassRoll), ",",
                    "     last_class_roll_dt=", conn.sqlDateValue(newLastClassRollDt),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the prerequisite satisfied field in a student course.
     *
     * @param cache              the data cache
     * @param stuId              the student ID of the record to be updated
     * @param course             the course of the record to be updated
     * @param sect               the section number of the record to be updated
     * @param termKey            the term key of the record to be updated
     * @param newPrereqSatisfied the new prerequisite satisfied value
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updatePrereqSatisfied(final Cache cache, final String stuId,
                                                final String course, final String sect, final TermKey termKey,
                                                final String newPrereqSatisfied) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of prereq satisfied of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET prereq_satis=", conn.sqlStringValue(newPrereqSatisfied),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the completed and score fields in a student course.
     *
     * @param cache        the data cache
     * @param stuId        the student ID of the record to be updated
     * @param course       the course of the record to be updated
     * @param sect         the section number of the record to be updated
     * @param termKey      the term key of the record to be updated
     * @param newCompleted the new "is Completed" flag
     * @param newScore     the new course score
     * @param newGrade     the new course grade
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateCompletedScoreGrade(final Cache cache, final String stuId, final String course,
                                                    final String sect, final TermKey termKey, final String newCompleted,
                                                    final Integer newScore, final String newGrade) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of completed score of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET completed=", conn.sqlStringValue(newCompleted), ",",
                    "     score=", conn.sqlIntegerValue(newScore), ",",
                    "     course_grade=", conn.sqlStringValue(newGrade),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the completed and score fields in a student course.
     *
     * @param cache         the data cache
     * @param stuId         the student ID of the record to be updated
     * @param course        the course of the record to be updated
     * @param sect          the section number of the record to be updated
     * @param termKey       the term key of the record to be updated
     * @param newExamPlaced the new "exam placed" flag
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateExamPlaced(final Cache cache, final String stuId, final String course,
                                           final String sect, final TermKey termKey,
                                           final String newExamPlaced) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of completed score of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET exam_placed=", conn.sqlStringValue(newExamPlaced),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the grade field in a dropped course row.
     *
     * @param cache      the data cache
     * @param stuId      the student ID of the record to be updated
     * @param course     the course of the record to be updated
     * @param sect       the section number of the record to be updated
     * @param termKey    the term key of the record to be updated
     * @param lastRollDt the last roll date of the record to update
     * @param newGrade   the new course grade
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateDroppedGrade(final Cache cache, final String stuId,
                                             final String course, final String sect, final TermKey termKey,
                                             final LocalDate lastRollDt,
                                             final String newGrade) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of completed score of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET course_grade=", conn.sqlStringValue(newGrade),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND last_class_roll_dt=", conn.sqlDateValue(lastRollDt),
                    "   AND final_class_roll='Y'",
                    "   AND open_status='D'");

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the instruction type field in a student course.
     *
     * @param cache         the data cache
     * @param stuId         the student ID of the record to be updated
     * @param course        the course of the record to be updated
     * @param sect          the section number of the record to be updated
     * @param termKey       the term key of the record to be updated
     * @param newInstrnType the new instruction type
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateInstructionType(final Cache cache, final String stuId,
                                                final String course, final String sect, final TermKey termKey,
                                                final String newInstrnType) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of instrn_type of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET instrn_type=", conn.sqlStringValue(newInstrnType),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the grading option field in a student course.
     *
     * @param cache            the data cache
     * @param stuId            the student ID of the record to be updated
     * @param course           the course of the record to be updated
     * @param sect             the section number of the record to be updated
     * @param termKey          the term key of the record to be updated
     * @param newGradingOption the new grading option
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateGradingOption(final Cache cache, final String stuId,
                                              final String course, final String sect, final TermKey termKey,
                                              final String newGradingOption) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of grading_option of RawStcourse for test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET grading_option=", conn.sqlStringValue(newGradingOption),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Updates the registration status field in a student course.
     *
     * @param cache        the data cache
     * @param stuId        the student ID of the record to be updated
     * @param course       the course of the record to be updated
     * @param sect         the section number of the record to be updated
     * @param termKey      the term key of the record to be updated
     * @param newRegStatus the new registration status
     * @return true if successful; false if not
     * @throws SQLException if there is an error performing the update
     */
    public static boolean updateRegistrationStatus(final Cache cache, final String stuId, final String course,
                                                   final String sect, final TermKey termKey,
                                                   final String newRegStatus) throws SQLException {

        final boolean result;

        if (stuId.startsWith("99")) {
            Log.info("Skipping update of grading_option of RawStcourse for ",
                    "test student:");
            Log.info("stuId: ", stuId);
            result = true;
        } else {
            final String tableName = getTableName(cache);

            final DbConnection conn = cache.checkOutConnection(ESchema.LEGACY);

            final String sql = SimpleBuilder.concat("UPDATE ", tableName,
                    " SET registration_status=", conn.sqlStringValue(newRegStatus),
                    " WHERE stu_id=", conn.sqlStringValue(stuId),
                    "   AND course=", conn.sqlStringValue(course),
                    "   AND sect=", conn.sqlStringValue(sect),
                    "   AND term=", conn.sqlStringValue(termKey.termCode),
                    "   AND term_yr=", conn.sqlIntegerValue(termKey.shortYear),
                    "   AND final_class_roll='Y'",
                    AND_NOT_DROPPED);

            try (final Statement stmt = conn.createStatement()) {
                final int count = stmt.executeUpdate(sql);
                result = count == 1;

                if (result) {
                    conn.commit();
                } else {
                    Log.warning("Update of '", tableName, "' returned " + count + " rather than 1 (", sql, ").");
                    conn.rollback();
                }
            } catch (final SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                Cache.checkInConnection(conn);
            }
        }

        return result;
    }

    /**
     * Executes a query that returns a list of records.
     *
     * @param conn the database connection
     * @param sql  the SQL to execute
     * @return the list of matching records
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStcourse> executeQuery(final DbConnection conn, final String sql) throws SQLException {

        final List<RawStcourse> result = new ArrayList<>(40);

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(RawStcourse.fromResultSet(rs));
            }
        }

        return result;
    }

    /**
     * Queries test data for a student.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStcourse> queryByStudentTestData(final Cache cache,
                                                            final String studentId) throws SQLException {

        final List<RawStcourse> result;

        if (studentId != null && studentId.length() == 9 && studentId.startsWith("99")) {

            final char ch3 = studentId.charAt(2);
            final char ch4 = studentId.charAt(3);

            if (ch3 == 'C' && ch4 == 'I') {
                result = queryByStudentCheckin(cache, studentId, null);
            } else {
                result = EMPTY_LIST;
            }
        } else {
            Log.warning("Invalid test student ID: " + studentId);
            result = EMPTY_LIST;
        }

        return result;
    }

    /**
     * Queries test data for a student within a term.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @param term      the term
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStcourse> queryByStudentTestData(final Cache cache, final String studentId,
                                                            final TermKey term) throws SQLException {

        final List<RawStcourse> result;

        if (studentId != null && studentId.length() == 9 && studentId.startsWith("99")) {

            final char ch3 = studentId.charAt(2);
            final char ch4 = studentId.charAt(3);

            if (ch3 == 'C' && ch4 == 'I') {
                result = queryByStudentCheckin(cache, studentId, term);
            } else {
                result = EMPTY_LIST;
            }
        } else {
            Log.warning("Invalid test student ID: " + studentId);
            result = EMPTY_LIST;
        }

        return result;
    }

    /**
     * Queries test data for a student for Checkin tests.
     *
     * @param cache     the data cache
     * @param studentId the ID of the student to query
     * @param term      the term; null to include all terms
     * @return the list of records that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error accessing the database
     */
    private static List<RawStcourse> queryByStudentCheckin(final Cache cache, final String studentId,
                                                           final TermKey term) throws SQLException {

        final TermRec activeTerm = cache.getSystemData().getActiveTerm();

        final boolean doCurrent = term == null || term.equals(activeTerm.term);
        final boolean doPast = term == null;

        List<RawStcourse> result = new ArrayList<>(1);

        final char ch5 = studentId.charAt(4);
        final char ch6 = studentId.charAt(5);

        if (ch5 == 'C' && ch6 == 'H') {

            final String id = studentId.substring(6);

            if ("017".equals(id) || "022".equals(id)) {
                if (doPast) {
                    result.add(makeCompleted(activeTerm, studentId, RawRecordConstants.M117));
                }
            } else if ("018".equals(id)) {
                if (doCurrent) {
                    result.add(makeCurrent(activeTerm, studentId, RawRecordConstants.M117));
                }
            } else if ("024".equals(id) || "032".equals(id)
                       || "042".equals(id)) {
                if (doPast) {
                    result.add(makeCompleted(activeTerm, studentId, RawRecordConstants.M118));
                }
            } else if ("025".equals(id)) {
                if (doCurrent) {
                    result.add(makeCurrent(activeTerm, studentId, RawRecordConstants.M118));
                }
            } else if ("034".equals(id)) {
                if (doPast) {
                    result.add(makeCompleted(activeTerm, studentId, RawRecordConstants.M124));
                }
            } else if ("035".equals(id)) {
                if (doCurrent) {
                    result.add(makeCurrent(activeTerm, studentId, RawRecordConstants.M124));
                }
            } else if ("044".equals(id) || "052".equals(id)) {
                if (doPast) {
                    result.add(makeCompleted(activeTerm, studentId, RawRecordConstants.M125));
                }
            } else if ("045".equals(id)) {
                if (doCurrent) {
                    result.add(makeCurrent(activeTerm, studentId, RawRecordConstants.M125));
                }
            } else if ("054".equals(id)) {
                if (doPast) {
                    result.add(makeCompleted(activeTerm, studentId, RawRecordConstants.M126));
                }
            } else if ("055".equals(id) && doCurrent) {
                result.add(makeCurrent(activeTerm, studentId, RawRecordConstants.M126));
            }
        } else {
            result = EMPTY_LIST;
        }

        return result;
    }

    /**
     * Creates a record of a completed (passed) course.
     *
     * @param activeTerm the active term
     * @param studentId  the student ID
     * @param course     the course ID
     * @return the generated student course record
     */
    private static RawStcourse makeCompleted(final TermRec activeTerm, final String studentId, final String course) {

        final int termYear = activeTerm.term.year.intValue() - 1;
        final LocalDate end = activeTerm.endDate;

        // Make it one year before active term
        return new RawStcourse(new TermKey(activeTerm.term.name, termYear), studentId, course, "001",
                Integer.valueOf(1), "N", "I", "Y", Integer.valueOf(64), "B", "Y", "Y", "N", "Y", null, null, null, null,
                "N", null, null, null, null, "RI", "RW", LocalDate.of(termYear, end.getMonth(), end.getDayOfMonth()),
                null, null);
    }

    /**
     * Creates a record of a current course.
     *
     * @param activeTerm the active term
     * @param studentId  the student ID
     * @param course     the course ID
     * @return the generated student course record
     */
    private static RawStcourse makeCurrent(final TermRec activeTerm, final String studentId, final String course) {

        return new RawStcourse(activeTerm.term, studentId, course, "001", Integer.valueOf(1), "N", "I", "Y",
                Integer.valueOf(64), "B", "Y", "Y", "N", "Y", null, null, null, null, "N", null, null, null, null,
                "RI", "RW", activeTerm.startDate, null, null);
    }
}
