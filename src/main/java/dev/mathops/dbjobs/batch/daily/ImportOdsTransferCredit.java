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
import dev.mathops.db.field.ETermName;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.schema.legacy.impl.RawFfrTrnsLogic;
import dev.mathops.db.schema.legacy.rec.RawFfrTrns;
import dev.mathops.db.schema.main.rec.TermRec;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that performs an import of transfer credit data from the ODS.
 */
public final class ImportOdsTransferCredit {

    /** Set to DEBUG to just print rather than updating database; NORMAL to update database. */
    private static final EDebugMode DEBUG = EDebugMode.NORMAL;

    /** The database profile through which to access the database. */
    private final Profile profile;

    /**
     * Constructs a new {@code ImportOdsTransferCredit}.
     */
    public ImportOdsTransferCredit() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        this.profile = config.getCodeProfile(Contexts.BATCH_PATH);
    }

    /**
     * Executes the job.
     */
    public void execute() {

        if (this.profile == null) {
            Log.warning("Unable to create production profile.");
        } else {
            final Cache cache = new Cache(this.profile);
            final SystemData systemData = cache.getSystemData();

            try {
                final TermRec active = systemData.getActiveTerm();
                if (active == null) {
                    Log.warning("Failed to query the active term.");
                } else {
                    final Login odsLogin = this.profile.getLogin(ESchema.ODS);
                    final DbConnection odsConn = odsLogin.checkOutConnection();

                    try {
                        Map<String, Map<String, String>> map = null;

                        if (active.term.name == ETermName.SPRING) {
                            Log.info("Processing under the SPRING term");
                            map = queryOds(odsConn, "CSUS_SECTION_INFO_SPR");
                        } else if (active.term.name == ETermName.SUMMER) {
                            Log.info("Processing under the SUMMER term");
                            map = queryOds(odsConn, "CSUS_SECTION_INFO_SMR");
                        } else if (active.term.name == ETermName.FALL) {
                            Log.info("Processing under the FALL term");
                            map = queryOds(odsConn, "CSUS_SECTION_INFO_FAL");
                        } else {
                            Log.warning("Active term has invalid term name:" + active.term.name);
                        }

                        if (map != null) {
                            final int numStudents = map.size();
                            Log.info("Found transfer credit data for " + numStudents + " students.");
                            process(cache, map);
                            Log.info("Job completed");
                        }

                    } catch (final SQLException ex) {
                        Log.warning("Unable to perform query", ex);
                    } finally {
                        odsLogin.checkInConnection(odsConn);
                    }
                }
            } catch (final SQLException ex) {
                Log.warning("Unable to obtain connection to ODS database", ex);
            }
        }
    }

    /**
     * Queries transfer records from the ODS for the Spring semester.
     *
     * @param conn      the database connection
     * @param tableName the term-specific table name for the section information table
     * @return a map from student ID to a map from course ID to the best transfer grade for that student/course
     * @throws SQLException if there is an error performing the query
     */
    private static Map<String, Map<String, String>> queryOds(final DbConnection conn, final String tableName)
            throws SQLException {

        final Map<String, Map<String, String>> result = new HashMap<>(5000);

        final String sql = "SELECT A.ID x, B.COURSE_IDENTIFICATION y, B.FINAL_GRADE z "
                           + "FROM CSUBAN." + tableName + " A, ODSMGR.STUDENT_COURSE B "
                           + "WHERE A.PERSON_UID = B.PERSON_UID "
                           + " AND ((B.COURSE_IDENTIFICATION='MATH002'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH117'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH118'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH120'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH124'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH125'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH126'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH127'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH141'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH155'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH156'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH157'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH160'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH161'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH229'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH255'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH1++'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH1++1B'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH2++'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH2++1B'"
                           + "    OR B.COURSE_IDENTIFICATION='FIN200'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH101'"
                           + "    OR B.COURSE_IDENTIFICATION='MATH105'"
                           + "    OR B.COURSE_IDENTIFICATION='STAT100'"
                           + "    OR B.COURSE_IDENTIFICATION='STAT201'"
                           + "    OR B.COURSE_IDENTIFICATION='STAT204')"
                           + "  AND (B.TRANSFER_COURSE_IND='Y'))";

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                final String stuId = rs.getString("x");
                final String course = rs.getString("y");

                if (stuId == null) {
                    Log.warning("ODS record had null student ID");
                } else if (course == null) {
                    Log.warning("ODS record had null course");
                } else if (stuId.length() == 9) {
                    String grade = rs.getString("z");
                    if (grade.startsWith("T")) {
                        grade = grade.substring(1);
                        if (grade.length() > 2) {
                            grade = grade.substring(0, 2);
                        }
                    }

                    final Map<String, String> studentMap = result.computeIfAbsent(stuId, id -> new HashMap<>(5));

                    final String existing = studentMap.get(course);
                    if (existing == null || gradeIsHigher(existing, grade)) {
                        studentMap.put(course, grade);
                    }
                } else {
                    Log.warning("ODS record had bad student ID: '" + stuId + "'");
                }
            }
        }

        return result;
    }

    /**
     * Given two grades, tests whether a "new" grade is higher than an "original" grade. We treat "S" as higher than "C"
     * since it can fulfil a prerequisite that needs a "B-" ior higher.
     *
     * @param origGrade the original grade
     * @param newGrade  the new grade
     * @return true if the new grade is higher
     */
    private static boolean gradeIsHigher(final String origGrade, final String newGrade) {

        final boolean higher;

        if (origGrade == null) {
            higher = newGrade != null;
        } else if ("A+".equals(origGrade)) {
            higher = false;
        } else if ("A".equals(origGrade)) {
            higher = "A+".equals(newGrade);
        } else if ("A-".equals(origGrade)) {
            higher = "A+".equals(newGrade) || "A".equals(newGrade);
        } else if ("B+".equals(origGrade)) {
            higher = newGrade.startsWith("A");
        } else if ("B".equals(origGrade)) {
            higher = newGrade.startsWith("A") || "B+".equals(newGrade);
        } else if ("B-".equals(origGrade)) {
            higher = newGrade.startsWith("A") || "B+".equals(newGrade) || "B".equals(newGrade);
        } else if ("S".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B");
        } else if ("C+".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade);
        } else if ("C".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade)
                     || "C+".equals(newGrade);
        } else if ("C-".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade)
                     || "C+".equals(newGrade) || "C".equals(newGrade);
        } else if ("D+".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade)
                     || newGrade.startsWith("C");
        } else if ("D".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade)
                     || newGrade.startsWith("C") || "D+".equals(newGrade);
        } else if ("D-".equals(origGrade)) {
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || "S".equals(newGrade)
                     || newGrade.startsWith("C") || "D".equals(newGrade);
        } else {
            // Original is some unrecognized code - treat any recognized code that is not "F" or "U" as "higher"
            higher = newGrade.startsWith("A") || newGrade.startsWith("B") || newGrade.startsWith("C")
                     || newGrade.startsWith("D") || newGrade.startsWith("S");
        }

        return higher;
    }

    /**
     * Processes a list of transfer records.
     *
     * @param cache the data cache
     * @param map   a map from student ID to a map from course ID to the "best" transfer grade for that course
     * @throws SQLException if there is an error accessing the database
     */
    private static void process(final Cache cache, final Map<String, Map<String, String>> map)
            throws SQLException {

        final LocalDate now = LocalDate.now();
        int count = 0;
        int count2 = 0;

        for (final Map.Entry<String, Map<String, String>> stuEntry : map.entrySet()) {

            final String stuId = stuEntry.getKey();
            final List<RawFfrTrns> existingRows = RawFfrTrnsLogic.queryByStudent(cache, stuId);
            final Map<String, String> stuMap = stuEntry.getValue();

            if (DEBUG == EDebugMode.DEBUG) {
                // Scan for existing FFR_TRNS records that have no corresponding ODS record and log, but do not log
                // "M 120" and "M 127" rows, since this is where we (currently) keep the record of completion of those
                // courses
                for (final RawFfrTrns row : existingRows) {
                    if ("M 120".equals(row.course) || "M 127".equals(row.course)) {
                        continue;
                    }
                    boolean searching = true;
                    for (final Map.Entry<String, String> courseEntry : stuMap.entrySet()) {
                        final String courseId = courseEntry.getKey();
                        final String cid = courseId.replace("MATH", "M ");
                        if (cid.equals(row.course)) {
                            searching = false;
                            break;
                        }
                    }

                    if (searching) {
                        Log.warning("FFR_TRNS has record for ", row.course, " for ", row.stuId, " but ODS does not.");
                    }
                }
            }

            for (final Map.Entry<String, String> courseEntry : stuMap.entrySet()) {
                final String courseId = courseEntry.getKey();
                final String cid = courseId.replace("MATH", "M ");
                final String grade = courseEntry.getValue();

                RawFfrTrns currentRec = null;
                for (final RawFfrTrns existing : existingRows) {
                    if (existing.course.equals(cid)) {
                        currentRec = existing;
                        break;
                    }
                }

                if (currentRec == null) {
                    Log.info("Inserting record for " + stuId + CoreConstants.SLASH + cid + " - " + now);
                    if (DEBUG == EDebugMode.NORMAL) {
                        final RawFfrTrns toInsert = new RawFfrTrns(stuId, cid, "T", now, null, grade);
                        if (RawFfrTrnsLogic.insert(cache, toInsert)) {
                            ++count;
                        } else {
                            Log.warning("Insert failed");
                        }
                    }
                } else if (!Objects.equals(currentRec.grade, grade)) {
                    Log.info("Updating grade from ", currentRec.grade, " to ", grade, " in ", currentRec.course,
                            " transfer credit for student ", stuId);
                    if (DEBUG == EDebugMode.NORMAL) {
                        if (RawFfrTrnsLogic.updateGrade(cache, currentRec, grade)) {
                            ++count2;
                        } else {
                            Log.warning("Update failed");
                        }
                    }
                }
            }
        }

        Log.info("Inserted " + count + " records");
        Log.info("Updated " + count2 + " records");
    }

    /**
     * Main method to execute the batch job.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        final ImportOdsTransferCredit job = new ImportOdsTransferCredit();

        job.execute();
    }
}
