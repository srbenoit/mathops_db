package dev.mathops.dbjobs.batch.daily;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.cfg.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.legacy.impl.RawFfrTrnsLogic;
import dev.mathops.db.schema.legacy.rec.RawFfrTrns;
import dev.mathops.text.builder.HtmlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that performs an import of past course registrations that can clear Precalculus prerequisites.
 */
public final class ImportOdsPastCourses {

    /** Set to DEBUG to just print rather than updating database; NORMAL to update database. */
    private static final EDebugMode DEBUG = EDebugMode.NORMAL;

    /** The database profile through which to access the database. */
    private final Profile profile;

    /**
     * Constructs a new {@code ImportOdsPastCourses}.
     */
    public ImportOdsPastCourses() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        this.profile = config.getCodeProfile(Contexts.BATCH_PATH);
    }

    /**
     * Executes the job.
     *
     * @return the report
     */
    public String execute() {

        final Collection<String> report = new ArrayList<>(10);

        if (this.profile == null) {
            report.add("Unable to create production profile.");
        } else {
            final Cache cache = new Cache(this.profile);

            final Login odsLogin = this.profile.getLogin(ESchema.ODS);

            final DbConnection odsConn = odsLogin.checkOutConnection();

            try {
                report.add("Processing");
                final List<TransferRecord> list = queryOds(odsConn, report);

                report.add("Found " + list.size() + " rows.");
                processList(cache, list, report);
                report.add("Job completed");

            } catch (final SQLException ex) {
                Log.warning(ex);
                report.add("Unable to perform query");
            } finally {
                odsLogin.checkInConnection(odsConn);
            }
        }

        final HtmlBuilder htm = new HtmlBuilder(1000);
        htm.addln("<pre>");
        for (final String rep : report) {
            htm.addln(rep);
        }
        htm.addln("</pre>");

        return htm.toString();
    }

    /**
     * Queries transfer records from the ODS for the Spring semester.
     *
     * @param conn   the database connection
     * @param report a list to which to add report lines
     * @return a list of transfer records
     * @throws SQLException if there is an error performing the query
     */
    private static List<TransferRecord> queryOds(final DbConnection conn,
                                                 final Collection<? super String> report) throws SQLException {

        final List<TransferRecord> result = new ArrayList<>(1000);

        try (final Statement stmt = conn.createStatement()) {

            final String sql = "SELECT ID, COURSE_IDENTIFICATION, FINAL_GRADE FROM ODSMGR.STUDENT_COURSE "
                               + " WHERE (COURSE_IDENTIFICATION='MATH120' OR COURSE_IDENTIFICATION='MATH127')"
                               + "   AND (FINAL_GRADE = 'A' OR FINAL_GRADE = 'B' OR FINAL_GRADE = 'C' OR"
                               + "        FINAL_GRADE = 'D' OR FINAL_GRADE = 'S')";

            try (final ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    final String stuId = rs.getString("ID");
                    final String course = rs.getString("COURSE_IDENTIFICATION");
                    final String grade = rs.getString("FINAL_GRADE");

                    if (stuId == null) {
                        report.add("ODS record had null student ID");
                    } else if (stuId.length() == 9) {
                        result.add(new TransferRecord(stuId, course, grade));
                    } else {
                        report.add("ODS record had bad student ID: '" + stuId + "'");
                    }
                }
            }
        }

        return result;
    }

    /**
     * Processes a list of transfer records.
     *
     * @param cache  the data cache
     * @param list   the list
     * @param report a list to which to add report lines
     * @throws SQLException if there is an error accessing the database
     */
    private static void processList(final Cache cache, final Iterable<TransferRecord> list,
                                    final Collection<? super String> report) throws SQLException {

        final LocalDate now = LocalDate.now();
        int count = 0;
        int count2 = 0;

        for (final TransferRecord rec : list) {
            final String stu = rec.getStuId();
            final String cid = rec.getCourse().replace("MATH", "M ");

            final List<RawFfrTrns> existings = RawFfrTrnsLogic.queryByStudent(cache, stu);

            RawFfrTrns currentRec = null;
            for (final RawFfrTrns existing : existings) {
                if (existing.course.equals(cid)) {
                    currentRec = existing;
                    break;
                }
            }

            String recGrade = rec.getGrade();
            if (recGrade.startsWith("T")) {
                recGrade = recGrade.substring(1);
                if (recGrade.length() > 2) {
                    recGrade = recGrade.substring(0, 2);
                }
            }

            if (currentRec == null) {
                report.add("Inserting record for " + stu + CoreConstants.SLASH + cid + " - " + now);
                if (DEBUG == EDebugMode.NORMAL) {
                    final RawFfrTrns toInsert = new RawFfrTrns(stu, cid, "T", now, null, recGrade);
                    if (RawFfrTrnsLogic.insert(cache, toInsert)) {
                        ++count;
                    } else {
                        report.add("Insert failed");
                    }
                }
            } else if (!Objects.equals(currentRec.grade, recGrade)) {
                Log.info("Updating grade from ", currentRec.grade, " to ", recGrade, " in ", currentRec.course,
                        " transfer credit for student ", currentRec.stuId);
                if (DEBUG == EDebugMode.NORMAL) {
                    if (RawFfrTrnsLogic.updateGrade(cache, currentRec, recGrade)) {
                        ++count2;
                    } else {
                        report.add("Update failed");
                    }
                }
            }
        }

        report.add("Inserted " + count + " records");
        report.add("Updated " + count2 + " records");
    }

    /**
     * Main method to execute the batch job.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();
        final ImportOdsPastCourses job = new ImportOdsPastCourses();

        Log.fine(job.execute());
    }

    /**
     * A transfer record.
     */
    private static final class TransferRecord {

        /** The student ID. */
        private final String stuId;

        /** The course ID. */
        private final String course;

        /** The course grade. */
        private final String grade;

        /**
         * Constructs a new {@code TransferRecord}.
         *
         * @param theStuId  the student ID
         * @param theCourse the course
         * @param theGrade  the grade
         */
        TransferRecord(final String theStuId, final String theCourse, final String theGrade) {

            this.stuId = theStuId;
            this.course = theCourse;
            this.grade = theGrade;
        }

        /**
         * Gets the student ID.
         *
         * @return the student ID
         */
        String getStuId() {

            return this.stuId;
        }

        /**
         * Gets the course ID.
         *
         * @return the course ID
         */
        String getCourse() {

            return this.course;
        }

        /**
         * Gets the course grade.
         *
         * @return the course grade
         */
        String getGrade() {

            return this.grade;
        }
    }
}
