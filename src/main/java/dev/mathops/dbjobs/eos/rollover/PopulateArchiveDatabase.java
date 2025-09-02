package dev.mathops.dbjobs.eos.rollover;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.cfg.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.Database;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Facet;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Copies data from the PROD database to the archive database.
 */
public final class PopulateArchiveDatabase implements Runnable {

    /** The name of the target archive database. */
    private static final String ARCHIVE_DATABASE_NAME = "termsm25";

    /** A unique ID for the login object. */
    private static final String ARCHIVE_LOGIN_ID = "_ARCH.P.MATH";

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.DEBUG;

    /** The data cache. */
    private final Cache cache;

    /** A login object that can connect to the archive database. */
    private final Login archiveLogin;

    /**
     * Constructs a new {@code PopulateArchiveDatabase}.
     *
     * @param theCache the data cache
     */
    private PopulateArchiveDatabase(final Cache theCache) {

        this.cache = theCache;

        final Profile profile = theCache.getProfile();
        final Facet facet = profile.getFacet(ESchema.LEGACY);
        final Login login = facet.login;
        final Database database = login.database;

        final Database archiveDatabase = new Database(database.server, ARCHIVE_DATABASE_NAME, database.instance,
                database.dba);
        this.archiveLogin = new Login(archiveDatabase, ARCHIVE_LOGIN_ID, login.user, login.password);
    }

    /**
     * Runs the process.
     */
    public void run() {

        final DbConnection archiveConn = this.archiveLogin.checkOutConnection();

        try {
            if (isArchiveEmpty(archiveConn)) {
                Log.info("Archive database verified to be empty.");
                archiveData(archiveConn);
            } else {
                Log.warning("Archive database is not empty!");
            }
        } catch (final SQLException ex) {
            Log.warning("Exception populating the archive database.", ex);
        } finally {
            this.archiveLogin.checkInConnection(archiveConn);
        }
    }

    /**
     * Performs the archiving process.
     *
     * @throws SQLException if there is an error accessing the database
     */
    private void archiveData(final DbConnection archiveConn) throws SQLException {

        final DbConnection prodConn = this.cache.checkOutConnection(ESchema.LEGACY);
        try {
            archiveAdminHold(prodConn, archiveConn);
            archiveActiveTermRows(prodConn, archiveConn, "bogus_mapping");
            archiveWholeTable(prodConn, archiveConn, "calcs");
            archiveWholeTable(prodConn, archiveConn, "campus_calendar");
            archiveBasedOnDate(prodConn, archiveConn, "challenge_fee", "bill_dt");
            archiveWholeTable(prodConn, archiveConn, "client_pc");
            archiveWholeTable(prodConn, archiveConn, "cohort");
            archiveWholeTable(prodConn, archiveConn, "course");
            archiveActiveTermRows(prodConn, archiveConn, "crsection");
            archiveActiveTermRows(prodConn, archiveConn, "csection");
            archiveActiveTermRows(prodConn, archiveConn, "cunit");
            archiveActiveTermRows(prodConn, archiveConn, "cuobjective");
            archiveActiveTermRows(prodConn, archiveConn, "cusection");
            archiveBasedOnDate(prodConn, archiveConn, "discipline", "dt_incident");
            archiveActiveTermRows(prodConn, archiveConn, "dont_submit");
            archiveWholeTable(prodConn, archiveConn, "etext");
            archiveWholeTable(prodConn, archiveConn, "etext_course");
            archiveWholeTable(prodConn, archiveConn, "etext_key");
            archiveWholeTable(prodConn, archiveConn, "exam");
            archiveWholeTable(prodConn, archiveConn, "examqa");
            archiveWholeTable(prodConn, archiveConn, "except_stu");
            archiveBasedOnDate(prodConn, archiveConn, "ffr_trns", "exam_dt");
            archiveWholeTable(prodConn, archiveConn, "grading_std");
            archiveWholeTable(prodConn, archiveConn, "high_schools");
            archiveWholeTable(prodConn, archiveConn, "hold_type");
            archiveWholeTable(prodConn, archiveConn, "homework");
            archiveBasedOnDate(prodConn, archiveConn, "mdstudent", "create_dt");
            archiveActiveTermRows(prodConn, archiveConn, "milestone");
            archiveActiveTermRows(prodConn, archiveConn, "milestone_appeal");
            archiveWholeTable(prodConn, archiveConn, "mpe");
            archiveBasedOnDate(prodConn, archiveConn, "mpe_credit", "exam_dt");
            archiveBasedOnDate(prodConn, archiveConn, "mpecr_denied", "exam_dt");
            archiveBasedOnDate(prodConn, archiveConn, "mpe_log", "exam_dt");
            archiveActiveTermRows(prodConn, archiveConn, "msg");
            archiveWholeTable(prodConn, archiveConn, "msg_lookup");
            archiveWholeTable(prodConn, archiveConn, "pace_appeals");
            archiveActiveTermRows(prodConn, archiveConn, "pace_track_rule");
            archiveActiveTermRows(prodConn, archiveConn, "pacing_rules");
            archiveActiveTermRows(prodConn, archiveConn, "pacing_structure");
            archiveWholeTable(prodConn, archiveConn, "parameters");
            archiveBasedOnDate(prodConn, archiveConn, "plc_fee", "bill_dt");
            archiveWholeTable(prodConn, archiveConn, "prereq");
            archiveWholeTable(prodConn, archiveConn, "remote_mpe");
            archiveWholeTable(prodConn, archiveConn, "resource");
            archiveWholeTable(prodConn, archiveConn, "semester_calendar");
            archiveWholeTable(prodConn, archiveConn, "special_stus");
            archiveBasedOnDate(prodConn, archiveConn, "stchallenge", "exam_dt");
            archiveWholeTable(prodConn, archiveConn, "stchallengeqa");
            archiveStcourse(prodConn, archiveConn);
            archiveWholeTable(prodConn, archiveConn, "stcuobjective");
            archiveBasedOnDate(prodConn, archiveConn, "stetext", "active_dt");
            archiveBasedOnDate(prodConn, archiveConn, "stexam", "exam_dt");
            archiveBasedOnDate(prodConn, archiveConn, "sthomework", "hw_dt");
            archiveWholeTable(prodConn, archiveConn, "sthwqa");
            archiveWholeTable(prodConn, archiveConn, "stmdscores");
            archiveWholeTable(prodConn, archiveConn, "stmilestone");
            archiveBasedOnDate(prodConn, archiveConn, "stmpe", "exam_dt");
            archiveWholeTable(prodConn, archiveConn, "stmpeqa");
            archiveBasedOnDate(prodConn, archiveConn, "stmsg", "msg_dt");
            archiveWholeTable(prodConn, archiveConn, "stpace_summary");
            archiveBasedOnDate(prodConn, archiveConn, "stqa", "exam_dt");
            archiveBasedOnDate(prodConn, archiveConn, "stresource", "loan_dt");
            archiveWholeTable(prodConn, archiveConn, "stsurveyqa");
            archiveActiveTermRows(prodConn, archiveConn, "stterm");
            archiveStudent(prodConn, archiveConn);
            archiveWholeTable(prodConn, archiveConn, "surveyqa");
            archiveTerm(prodConn, archiveConn);
            archiveWholeTable(prodConn, archiveConn, "testing_centers");
            archiveWholeTable(prodConn, archiveConn, "user_clearance");
            archiveWholeTable(prodConn, archiveConn, "users");
            createIndexData(archiveConn);

        } finally {
            Cache.checkInConnection(prodConn);
        }
    }

    /**
     * Performs a query in the archive database to ensure it is empty.  The main idea here is to prevent someone from
     * running this program without changing the archive database name and polluting an old archive.
     *
     * @param archiveConn the database connection to the archive database
     * @return true if the archive database appears empty; false if not
     * @throws SQLException if there is an error accessing the database
     */
    private static boolean isArchiveEmpty(final DbConnection archiveConn) throws SQLException {

        int total = 0;

        try (final Statement stmt = archiveConn.createStatement()) {
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM campus_calendar")) {
                rs.next();
                total += rs.getInt(1);
            }
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM course")) {
                rs.next();
                total += rs.getInt(1);
            }
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM csection")) {
                rs.next();
                total += rs.getInt(1);
            }
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cusection")) {
                rs.next();
                total += rs.getInt(1);
            }
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM stexam")) {
                rs.next();
                total += rs.getInt(1);
            }
            try (final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM term")) {
                rs.next();
                total += rs.getInt(1);
            }
        }

        return total == 0;
    }

    /**
     * Copies archive data from the 'admin_term' table to the archive database.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveAdminHold(final DbConnection prodConn, final DbConnection archiveConn)
            throws SQLException {

        Log.info("> Archiving the admin_hold table.");

        // FIXME: Should be a field on the "admin_hold" record: "retain across terms" (or in HOLD_TYPE)

        final String select = SimpleBuilder.concat("SELECT * FROM admin_hold",
                " WHERE create_dt >= (SELECT start_dt FROM term WHERE active= 'Y')",
                " AND (hold_id IN ('06','30') OR hold_id MATCHES '4?');");

        copyTable(prodConn, archiveConn, "admin_hold", select);
    }

    /**
     * Copies archive data from the 'stcourse' table to the archive database.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveStcourse(final DbConnection prodConn, final DbConnection archiveConn)
            throws SQLException {

        Log.info("> Archiving the stcourse table.");

        // Gather Incompletes
        final String select1 = SimpleBuilder.concat("SELECT * FROM stcourse",
                " WHERE i_deadline_dt <= (SELECT end_dt FROM term WHERE active = 'Y')",
                " AND i_deadline_dt > (SELECT end_dt FROM term WHERE active = 'P')",
                " AND i_deadline_dt IS NOT NULL;");

        copyTable(prodConn, archiveConn, "stcourse", select1);

        // Gather current-term registrations
        final String select2 = SimpleBuilder.concat("SELECT * FROM stcourse",
                " WHERE term = (SELECT term FROM term WHERE active= 'Y')",
                " AND term_yr = (SELECT term_yr FROM term WHERE active= 'Y')",
                " AND i_in_progress != 'Y'");

        copyTable(prodConn, archiveConn, "stcourse", select2);
    }

    /**
     * Copies archive data from the 'student' table to the archive database.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveStudent(final DbConnection prodConn, final DbConnection archiveConn)
            throws SQLException {

        Log.info("> Archiving the student table.");

        final String select1 = SimpleBuilder.concat("SELECT distinct student.* FROM student",
                " WHERE create_dt >= (SELECT start_dt FROM term WHERE active = 'Y')",
                "    OR stu_id IN (SELECT distinct stu_id FROM admin_hold)",
                "    OR stu_id IN (SELECT distinct stu_id FROM calcs)",
                "    OR stu_id IN (SELECT distinct stu_id FROM discipline",
                "                  WHERE dt_incident >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM except_stu)",
                "    OR stu_id IN (SELECT distinct stu_id FROM ffr_trns",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM mdstudent",
                "                  WHERE create_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM mpe_credit",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM mpecr_denied",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM milestone_appeal)",
                "    OR stu_id IN (SELECT distinct stu_id FROM pace_appeals)",
                "    OR stu_id IN (SELECT distinct stu_id FROM plc_fee",
                "                  WHERE bill_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM special_stus)",
                "    OR stu_id in (SELECT distinct stu_id FROM stcourse",
                "                  WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                "                    AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM stetext",
                "                  WHERE active_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM stexam",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM sthomework",
                "                  WHERE hw_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM stmpe",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM stresource",
                "                  WHERE loan_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM stsurveyqa",
                "                  WHERE exam_dt >= (SELECT start_dt FROM term WHERE active = 'Y'))",
                "    OR stu_id IN (SELECT distinct stu_id FROM users);");

        copyTable(prodConn, archiveConn, "student", select1);
    }

    /**
     * Copies archive data from the 'student' table to the archive database.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveTerm(final DbConnection prodConn, final DbConnection archiveConn) throws SQLException {

        Log.info("> Archiving the term table.");

        final String select1 = SimpleBuilder.concat("SELECT * FROM term WHERE active = 'Y';");

        copyTable(prodConn, archiveConn, "term", select1);
    }

    /**
     * Generates a generic SQL select statement to copy all records in a table to the corresponding archive table.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @param tableName   the table name
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveWholeTable(final DbConnection prodConn, final DbConnection archiveConn,
                                          final String tableName) throws SQLException {

        Log.info("> Archiving the ", tableName, " table.");

        final String select = SimpleBuilder.concat("SELECT * FROM ", tableName, ";");

        copyTable(prodConn, archiveConn, tableName, select);
    }

    /**
     * Generates a generic SQL select statement to copy all records in a table to the corresponding archive table where
     * the 'term' and 'term_yr' fields match those of the ACTIVE term.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @param tableName   the table name
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveActiveTermRows(final DbConnection prodConn, final DbConnection archiveConn,
                                              final String tableName) throws SQLException {

        Log.info("> Archiving the ", tableName, " table.");

        final String select = SimpleBuilder.concat("SELECT * FROM ", tableName,
                " WHERE term = (SELECT term FROM term WHERE active='Y')",
                " AND term_yr = (SELECT term_yr FROM term WHERE active='Y');");

        copyTable(prodConn, archiveConn, tableName, select);
    }

    /**
     * Generates a generic SQL select statement to copy all records in a source table that have some date field whose
     * value is not before the start of the ACTIVE term.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @param tableName   the table name
     * @param dateField   the date field on which to compare
     * @throws SQLException if there is an error accessing the database
     */
    private static void archiveBasedOnDate(final DbConnection prodConn, final DbConnection archiveConn,
                                           final String tableName, final String dateField) throws SQLException {

        Log.info("> Archiving the ", tableName, " table.");

        final String select = SimpleBuilder.concat("SELECT * FROM ", tableName, " WHERE ", dateField,
                " >= (SELECT start_dt FROM term WHERE active = 'Y');");

        copyTable(prodConn, archiveConn, tableName, select);
    }

    /**
     * Performs a copy of all rows returned by a given select statement into a table in the archive database.
     *
     * @param prodConn    the connection to the production database
     * @param archiveConn the connection to the archive database
     * @param tableName   the name of the table in (used to build the insert statement)
     * @param select      the SQL select statement with which to query the production table
     * @throws SQLException if there is an error accessing the database
     */
    private static void copyTable(final DbConnection prodConn, final DbConnection archiveConn,
                                  final String tableName, final String select) throws SQLException {

        try (final Statement statement = prodConn.createStatement();
             final ResultSet rs = statement.executeQuery(select)) {

            final ResultSetMetaData meta = rs.getMetaData();
            final int cols = meta.getColumnCount();
            final int[] types = new int[cols];

            final HtmlBuilder builder = new HtmlBuilder(200);
            builder.add("INSERT INTO ", tableName, " (");
            for (int i = 1; i <= cols; ++i) {
                final String name = meta.getColumnName(i);
                types[i - 1] = meta.getColumnType(i);
                if (i > 1) {
                    builder.add(',');
                }
                builder.add(name);
            }
            builder.add(") VALUES (?");
            for (int i = 2; i <= cols; ++i) {
                builder.add(",?");
            }
            builder.add(")");

            final String insert = builder.toString();
            try (final PreparedStatement prepared = archiveConn.prepareStatement(insert)) {
                int numRows = 0;

                while (rs.next()) {
                    for (int i = 1; i <= cols; ++i) {
                        final Object fieldValue = rs.getObject(i);
                        if (fieldValue == null) {
                            prepared.setNull(i, types[i - 1]);
                        } else {
                            prepared.setObject(i, fieldValue, types[i - 1]);
                        }
                    }

                    ++numRows;
                    if (DEBUG_MODE == EDebugMode.NORMAL) {
                        final int count = prepared.executeUpdate();
                        if (count == 1) {
                            archiveConn.commit();
                        } else {
                            Log.warning("FAILED to insert record into ", tableName, " table");
                        }
                    }
                }

                Log.info("Copied " + numRows + " records into the archive '", tableName, "' table.");
            }
        }
    }

    /**
     * Creates the index data for the archive database.
     *
     * @param archiveConn the connection to the archive database
     * @throws SQLException if there is an error accessing the database
     */
    private static void createIndexData(final DbConnection archiveConn) throws SQLException {

        Log.info("> Creating index specifications");

        // NOTE: Line-feeds keep four arrays aligned - take care if reformatting

        final String[] tables = {"admin_hold", "bogus_mapping", "calcs", "campus_calendar", "challenge_fee",
                "client_pc", "cohort", "course", "crsection", "csection",
                "cunit", "cuobjective", "cusection",
                "discipline", "dont_submit", "etext", "etext_course", "etext_key", "exam", "examqa", "except_stu",
                "ffr_trns", "grade_roll", "grading_std", "high_schools", "hold_type", "homework", "mdstudent",
                "milestone", "milestone_appeal", "mpe", "mpe_credit", "mpe_log", "mpecr_denied", "msg", "msg_lookup",
                "pace_appeals", "pace_track_rule", "pacing_rules", "pacing_structure", "parameters", "plc_fee",
                "prereq", "remote_mpe", "resource", "semester_calendar", "special_stus", "stchallenge", "stchallengeqa",
                "stcourse", "stcuobjective", "stetext",
                "stexam", "sthomework", "sthwqa", "stmdscores",
                "stmilestone", "stmpe", "stmpeqa", "stmsg", "stpace_summary", "stqa", "stresource",
                "stsurveyqa", "stterm", "student",
                "surveyqa", "term", "testing_centers", "user_clearance", "users"};

        final boolean[] hasDesc = {true, false, true, false, true,
                true, true, false, true, true,
                false, true, true,
                true, false, false, false, true, true, true, false,
                true, true, false, true, false, true, true,
                false, true, false, true, true, true, false, true,
                true, false, true, false, false, true,
                false, false, true, false, true, true, true,
                true, true, true,
                true, true, true, true,
                true, true, true, true, true, true, true,
                true, true, true,
                true, false, false, false, true};

        final String[] types = {"unique", null, null, null, "unique",
                "unique", "unique", null, "unique cluster", "unique cluster",
                null, "unique cluster", "unique cluster",
                null, null, null, null, null, "unique", "cluster", null,
                null, null, null, "unique", null, "unique", "unique",
                null, null, null, null, null, null, null, "unique",
                null, null, null, null, null, "unique",
                null, null, "unique", null, null, null, null,
                "cluster", null, null,
                "cluster", null, null, null,
                null, null, null, null, null, null, null,
                null, "unique", "unique",
                "cluster", null, null, null, null};

        final String[] cols = {"(stu_id,hold_id)", null, "(stu_id)", null, "(stu_id)",
                "(computer_id)", "(cohort)", null, "(term,term_yr,course,sect,unit)", "(term,term_yr,course,sect)",
                null, "(term,term_yr,course,unit,objective)", "(term,term_yr,course,sect,unit)",
                "(stu_id)", null, null, null, "(etext_key)", "(version)", "(version,question_nbr)", null,
                "(stu_id,course)", "(stu_id,course,sect)", null, "(hs_code)", null, "(version)", "(stu_id)",
                null, "(stu_id)", null, "(stu_id,course)", "(stu_id)", "(stu_id,course)", null, "(domain,code)",
                "(stu_id)", null, "(pacing_structure,term,term_yr)", null, null, "(stu_id)",
                null, null, "(resource_id)", null, "(stu_id)", "(stu_id)", "(stu_id,question_nbr)",
                "(stu_id,term,term_yr,course,sect)", "(stu_id,course,unit,objective)", "(stu_id)",
                "(stu_id,course,unit,passed)", "(stu_id)", "(stu_id)", "(stu_id,term,term_yr,exam_dt,sts_nbr)",
                "(stu_id)", "(stu_id)", "(stu_id,question_nbr)", "(stu_id)", "(stu_id)", "(stu_id)", "(stu_id)",
                "(stu_id,version,exam_dt,finish_time)", "(stu_id,term,term_yr)", "(stu_id)",
                "(term,term_yr,version,survey_nbr)", null, null, null, "(stu_id,term,term_yr)"};

        final String sql1 = "INSERT INTO index_frequency (on_demand,weekend_indx,tblname) VALUES (?,?,?)";
        final String sql2 = "INSERT INTO index_descriptions (tblname,indxname,indxtype,indxkeys) VALUES (?,?,?,?)";

        if (DEBUG_MODE == EDebugMode.NORMAL) {
            try (final PreparedStatement prepared1 = archiveConn.prepareStatement(sql1)) {
                for (final String table : tables) {
                    prepared1.setString(1, "d");
                    prepared1.setString(2, "Y");
                    prepared1.setString(3, table);

                    prepared1.executeUpdate();
                }
                archiveConn.commit();
            }
            try (final PreparedStatement prepared2 = archiveConn.prepareStatement(sql2)) {
                final int count = tables.length;
                for (int i = 0; i < count; ++i) {
                    if (hasDesc[i]) {
                        final String table = tables[i];
                        final String type = types[i];
                        final String col = cols[i];

                        prepared2.setString(1, table);
                        prepared2.setString(2, "i_" + table);
                        prepared2.setString(3, type);
                        prepared2.setString(4, col);

                        prepared2.executeUpdate();
                    }
                }
                archiveConn.commit();
            }
        }
    }

    /**
     * Main method to execute the batch job.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();

        final DatabaseConfig config = DatabaseConfig.getDefault();
        final Profile profile = config.getCodeProfile(Contexts.BATCH_PATH);
        final Cache cache = new Cache(profile);

        final Runnable obj = new PopulateArchiveDatabase(cache);
        obj.run();
    }
}