package dev.mathops.dbjobs.eos.rollover;

import dev.mathops.commons.EDebugMode;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.cfg.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.SystemData;
import dev.mathops.db.schema.legacy.impl.RawStcuobjectiveLogic;
import dev.mathops.db.schema.legacy.impl.RawStexamLogic;
import dev.mathops.db.schema.legacy.impl.RawSthomeworkLogic;
import dev.mathops.db.schema.legacy.impl.RawSthwqaLogic;
import dev.mathops.db.schema.legacy.impl.RawStqaLogic;
import dev.mathops.db.schema.legacy.rec.RawStcuobjective;
import dev.mathops.db.schema.legacy.rec.RawStexam;
import dev.mathops.db.schema.legacy.rec.RawSthomework;
import dev.mathops.db.schema.legacy.rec.RawSthwqa;
import dev.mathops.db.schema.legacy.rec.RawStqa;
import dev.mathops.db.schema.main.rec.TermRec;
import dev.mathops.db.field.TermKey;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs some cleanup of data after archiving.  This is based on commands in the old "eos_arc" and "eos_load"
 * Informix batches.
 */
public final class PostArchiveCleanup implements Runnable {

    /** Flag to run in "debug" mode which prints changes that would be performed rather than performing any changes. */
    private static final EDebugMode DEBUG_MODE = EDebugMode.NORMAL;
    /** The data cache. */
    private final Cache cache;

    /**
     * Constructs a new {@code PostArchiveCleanup}.
     *
     * @param theCache the data cache
     */
    private PostArchiveCleanup(final Cache theCache) {

        this.cache = theCache;
    }

    /**
     * Runs the process.
     */
    public void run() {

        final DbConnection prodConn = this.cache.checkOutConnection(ESchema.LEGACY);
        try {
            copyActiveTermToNextTerm(prodConn, "cunit");
            deleteOldRecords(prodConn, "cunit", 2);
            copyActiveTermToNextTerm(prodConn, "cuobjective");
            deleteOldRecords(prodConn, "cuobjective", 2);
            copyActiveTermToNextTerm(prodConn, "cusection");
            cleanTable(prodConn, "campus_calendar");
            copyTable(prodConn, "milestone_appeal", "prev_milestone_appeal");
            copyActiveTermToNextTerm(prodConn, "pacing_rules");
            copyActiveTermToNextTerm(prodConn, "pacing_structure");
            cleanTable(prodConn, "semester_calendar");
            copyTable(prodConn, "stmilestone", "prev_stmilestone");
            copyTable(prodConn, "stterm", "prev_stterm");
            final List<String> incompleteKeys = gatherIncompleteKeys(prodConn);
            preserveIncompleteStcuobjectives(prodConn, incompleteKeys);
            preserveIncompleteSthomework(prodConn, incompleteKeys);
            preserveIncompleteStexam(prodConn, incompleteKeys);
            cleanCsection(prodConn);
        } catch (final SQLException ex) {
            Log.warning("Exception performing post-archive cleanup.", ex);
        } finally {
            Cache.checkInConnection(prodConn);
        }
    }

    /**
     * Gathers a list of "keys" for active Incompletes, where a key is the concatenation of the student ID, a ".", and
     * the course ID.
     *
     * @param prodConn the connection to the production database
     * @return the list of keys
     * @throws SQLException if there is an error accessing the database
     */
    private List<String> gatherIncompleteKeys(final DbConnection prodConn) throws SQLException {

        final List<String> keys = new ArrayList<>(20);

        final String sql1 = SimpleBuilder.concat("SELECT stu_id,course FROM stcourse",
                " WHERE (course_grade = 'I' OR i_in_progress = 'Y')");

        try (final Statement statement = prodConn.createStatement();
             final ResultSet rs = statement.executeQuery(sql1)) {
            while (rs.next()) {
                final String stuIdTemp = rs.getString("stu_id");
                final String courseIdTemp = rs.getString("course");

                if (stuIdTemp == null || courseIdTemp == null) {
                    Log.warning("WARNING: An STCOURSE record had null student ID or course ID.");
                } else {
                    final String stuId = stuIdTemp.trim();
                    final String course = courseIdTemp.trim();
                    final String key = stuId + "." + course;
                    keys.add(key);
                }
            }
        }

        return keys;
    }

    /**
     * Deletes all STCUOBJECTIVE records that are not associated with open Incompletes.
     *
     * @param prodConn       the connection to the production database
     * @param incompleteKeys a list of "keys" for all active Incompletes
     * @throws SQLException if there is an error accessing the database
     */
    private void preserveIncompleteStcuobjectives(final DbConnection prodConn, final List<String> incompleteKeys)
            throws SQLException {

        Log.info("> Deleting 'stcuobjective' records that are not associated with tutorials or active Incompletes.");

        final List<RawStcuobjective> toPreserve = new ArrayList<>(300);
        final List<RawStcuobjective> toDelete = new ArrayList<>(10000);

        final List<RawStcuobjective> all = RawStcuobjectiveLogic.queryAll(this.cache);
        for (final RawStcuobjective record : all) {
            final String course = record.course;
            final String key = record.stuId + "." + course;
            if ("M 100T".equals(course) || "M 1170".equals(course) || "M 1180".equals(course)
                || "M 1240".equals(course) || "M 1250".equals(course) || "M 1260".equals(course)
                || incompleteKeys.contains(key)) {
                toPreserve.add(record);
            } else {
                toDelete.add(record);
            }
        }

        final int count = toPreserve.size();
        final String countStr = Integer.toString(count);
        final int count2 = toDelete.size();
        final String toDeleteStr = Integer.toString(count2);
        Log.info("  Found ", countStr, " 'stcuobjective' records to retain, ", toDeleteStr, " to delete.");

        if (DEBUG_MODE == EDebugMode.NORMAL) {
            for (final RawStcuobjective record : toDelete) {
                RawStcuobjectiveLogic.delete(this.cache, record);
            }
        }
    }

    /**
     * Deletes all STHOMEWORK and STHWQA records that are not associated with open Incompletes.
     *
     * @param prodConn       the connection to the production database
     * @param incompleteKeys a list of "keys" for all active Incompletes
     * @throws SQLException if there is an error accessing the database
     */
    private void preserveIncompleteSthomework(final DbConnection prodConn, final List<String> incompleteKeys)
            throws SQLException {

        Log.info("> Deleting 'sthomework' and 'sthwqa' records that are not associated with active Incompletes.");

        final List<RawSthomework> hwToPreserve = new ArrayList<>(300);
        final List<RawSthomework> hwToDelete = new ArrayList<>(10000);
        final List<Long> serials = new ArrayList<>(10000);
        final List<RawSthomework> allHomework = RawSthomeworkLogic.queryAll(this.cache);
        for (final RawSthomework record : allHomework) {
            final String course = record.course;
            final String key = record.stuId + "." + course;
            if ("M 100T".equals(course) || "M 1170".equals(course) || "M 1180".equals(course)
                || "M 1240".equals(course) || "M 1250".equals(course) || "M 1260".equals(course)
                || incompleteKeys.contains(key)) {
                hwToPreserve.add(record);
                serials.add(record.serialNbr);
            } else {
                hwToDelete.add(record);
            }
        }

        final List<RawSthwqa> qaToPreserve = new ArrayList<>(900);
        final List<RawSthwqa> qaToDelete = new ArrayList<>(30000);
        final List<RawSthwqa> allHomeworkQA = RawSthwqaLogic.queryAll(this.cache);
        for (final RawSthwqa record : allHomeworkQA) {
            if (serials.contains(record.serialNbr)) {
                qaToPreserve.add(record);
            } else {
                qaToDelete.add(record);
            }
        }

        final int count1 = hwToPreserve.size();
        final String count1Str = Integer.toString(count1);
        final int count2 = hwToDelete.size();
        final String count2Str = Integer.toString(count2);
        Log.info("  Found ", count1Str, " 'sthomework' records to retain, ", count2Str, " to delete.");

        final int count3 = qaToPreserve.size();
        final String count3Str = Integer.toString(count3);
        final int count4 = qaToDelete.size();
        final String count4Str = Integer.toString(count4);
        Log.info("  Found ", count3Str, " 'sthwqa' records to retain, ", count4Str, " to delete.");

        if (DEBUG_MODE == EDebugMode.NORMAL) {
            final DbConnection conn = this.cache.checkOutConnection(ESchema.LEGACY);

            try {
                for (final RawSthomework record : hwToDelete) {
                    RawSthomeworkLogic.deleteNoCommit(this.cache, conn, record);
                }
                conn.commit();
                for (final RawSthwqa record : qaToDelete) {
                    RawSthwqaLogic.deleteNoCommit(this.cache, conn, record);
                }
                conn.commit();
            } finally {
                Cache.checkInConnection(conn);
            }
        }
    }

    /**
     * Deletes all STEXAM and STQA records that are not associated with open Incompletes.
     *
     * @param prodConn       the connection to the production database
     * @param incompleteKeys a list of "keys" for all active Incompletes
     * @throws SQLException if there is an error accessing the database
     */
    private void preserveIncompleteStexam(final DbConnection prodConn, final List<String> incompleteKeys)
            throws SQLException {

        Log.info("> Deleting 'stexam' and 'stqa' records that are not associated with active Incompletes.");

        final List<RawStexam> examsToPreserve = new ArrayList<>(300);
        final List<RawStexam> examsToDelete = new ArrayList<>(50000);
        final List<Long> serials = new ArrayList<>(10000);
        final List<RawStexam> allExams = RawStexamLogic.queryAll(this.cache);
        for (final RawStexam record : allExams) {
            final String course = record.course;
            final String key = record.stuId + "." + course;
            if ("M 100T".equals(course) || "M 1170".equals(course) || "M 1180".equals(course)
                || "M 1240".equals(course) || "M 1250".equals(course) || "M 1260".equals(course)
                || incompleteKeys.contains(key)) {
                examsToPreserve.add(record);
                serials.add(record.serialNbr);
            } else {
                examsToDelete.add(record);
            }
        }

        final List<RawStqa> qaToPreserve = new ArrayList<>(3000);
        final List<RawStqa> qaToDelete = new ArrayList<>(500000);
        final List<RawStqa> allQa = RawStqaLogic.queryAll(this.cache);
        for (final RawStqa record : allQa) {
            if (serials.contains(record.serialNbr)) {
                qaToPreserve.add(record);
            } else {
                qaToDelete.add(record);
            }
        }

        final int count1 = examsToPreserve.size();
        final String count1Str = Integer.toString(count1);
        final int count2 = examsToDelete.size();
        final String count2Str = Integer.toString(count2);
        Log.info("  Found ", count1Str, " 'stexam' records to retain, ", count2Str, " to delete.");

        final int count3 = qaToPreserve.size();
        final String count3Str = Integer.toString(count3);
        final int count4 = qaToDelete.size();
        final String count4Str = Integer.toString(count4);
        Log.info("  Found ", count3Str, " 'stqa' records to retain, ", count4Str, " to delete.");

        if (DEBUG_MODE == EDebugMode.NORMAL) {
            final DbConnection conn = this.cache.checkOutConnection(ESchema.LEGACY);

            try {
                for (final RawStexam record : examsToDelete) {
                    RawStexamLogic.deleteNoCommit(this.cache, conn, record);
                }
                conn.commit();
                for (final RawStqa record : qaToDelete) {
                    RawStqaLogic.deleteNoCommit(this.cache, conn, record);
                }
                conn.commit();
            } finally {
                Cache.checkInConnection(conn);
            }
        }
    }

    /**
     * Deletes CSECTION records for 'bogus' sections (except tutorials),
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private static void cleanCsection(final DbConnection prodConn) throws SQLException {

        Log.info("> Deleting 'csection' records for bogus sections.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM csection WHERE bogus = 'Y'",
                        " AND term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y')",
                        " AND course NOT MATCHES 'M 100*'" +
                        " AND course NOT IN ('M 1170','M 1180','M 1240','M 1250','M 1260');");

                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("  Found " + count + " 'csection' records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM csection WHERE bogus = 'Y'",
                        " AND term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y')",
                        " AND course NOT MATCHES 'M 100*'" +
                        " AND course NOT IN ('M 1170','M 1180','M 1240','M 1250','M 1260');");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("  Deleted " + count + " 'csection' records.");
            }
        }

    }

    /**
     * Deletes all records from a table.
     *
     * @param prodConn  the connection to the production database
     * @param tableName the table name
     * @throws SQLException if there is an error accessing the database
     */
    private void cleanTable(final DbConnection prodConn, final String tableName) throws SQLException {

        Log.info("> Deleting all '", tableName, "' records.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", tableName);
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("  Found " + count + " '", tableName, "' records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM ", tableName);
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("  Deleted " + count + " '", tableName, "' records.");
            }
        }
    }

    /**
     * Copies all active-term records in a specified table to next-term records.
     *
     * @param prodConn  the connection to the production database
     * @param tableName the table name
     * @throws SQLException if there is an error accessing the database
     */
    private void copyActiveTermToNextTerm(final DbConnection prodConn, final String tableName) throws SQLException {

        final SystemData systemData = this.cache.getSystemData();
        final TermRec nextTerm = systemData.getNextTerm();

        Log.info("> Copying active-term '", tableName, "' records to next-term records.");

        if (nextTerm == null) {
            Log.warning("  Failed to query 'next' term.");
        } else {
            final TermKey nextKey = nextTerm.term;

            try (final Statement statement = prodConn.createStatement()) {

                if (DEBUG_MODE == EDebugMode.DEBUG) {
                    final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", tableName,
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");
                    try (final ResultSet rs = statement.executeQuery(sql)) {
                        rs.next();
                        final int count = rs.getInt(1);
                        final String countStr = Integer.toString(count);
                        Log.info("  Found ", countStr, " '", tableName, "' records to copy.");
                    }
                } else {
                    final String sql = SimpleBuilder.concat("SELECT * FROM ", tableName,
                            " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                            " AND term_yr = (SELECT term_yr FROM term WHERE active = 'Y');");

                    try (final ResultSet rs = statement.executeQuery(sql);) {
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
                        try (final PreparedStatement prepared = prodConn.prepareStatement(insert)) {
                            int numRows = 0;

                            while (rs.next()) {
                                for (int i = 1; i <= cols; ++i) {
                                    final String name = meta.getColumnName(i);
                                    if ("term".equalsIgnoreCase(name)) {
                                        prepared.setString(i, nextKey.termCode);
                                    } else if ("term_yr".equalsIgnoreCase(name)) {
                                        final int shortYear = nextKey.shortYear.intValue();
                                        prepared.setInt(i, shortYear);
                                    } else {
                                        final Object fieldValue = rs.getObject(i);
                                        if (fieldValue == null) {
                                            prepared.setNull(i, types[i - 1]);
                                        } else {
                                            prepared.setObject(i, fieldValue, types[i - 1]);
                                        }
                                    }
                                }

                                ++numRows;
                                if (DEBUG_MODE == EDebugMode.NORMAL) {
                                    final int count = prepared.executeUpdate();
                                    if (count != 1) {
                                        Log.warning("  FAILED to insert record into '", tableName, "' table");
                                    }
                                }
                            }

                            prodConn.commit();
                            Log.info("  Copied " + numRows + " records within '", tableName, "' table.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Deletes records in a specified table that are some number of years old (or older).
     *
     * @param prodConn the connection to the production database
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteOldRecords(final DbConnection prodConn, final String tableName, final int yearsAgo)
            throws SQLException {

        final String yearsStr = Integer.toString(yearsAgo);
        Log.info("> Deleting '", tableName, "' records that are more than ", yearsStr, " years old.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", tableName,
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    Log.info("  Found " + count + " old '", tableName, "' records to delete.");
                }
            } else {
                final String sql = SimpleBuilder.concat("DELETE FROM ", tableName,
                        " WHERE term = (SELECT term FROM term WHERE active = 'Y')",
                        " AND term_yr <= ((SELECT term_yr FROM term WHERE active = 'Y') - 2)");
                final int count = statement.executeUpdate(sql);
                prodConn.commit();
                Log.info("  Deleted " + count + " old '", tableName, "' records.");
            }
        }
    }

    /**
     * Copies all rows from one table to another.
     *
     * @param prodConn        the connection to the production database
     * @param sourceTableName the source table name
     * @param targetTableName the target table name
     * @throws SQLException if there is an error accessing the database
     */
    private static void copyTable(final DbConnection prodConn, final String sourceTableName,
                                  final String targetTableName) throws SQLException {

        Log.info("> Copying all records from '", sourceTableName, "' to '", targetTableName, "'.");

        try (final Statement statement = prodConn.createStatement()) {
            if (DEBUG_MODE == EDebugMode.DEBUG) {
                final String sql = SimpleBuilder.concat("SELECT COUNT(*) FROM ", sourceTableName);
                try (final ResultSet rs = statement.executeQuery(sql)) {
                    rs.next();
                    final int count = rs.getInt(1);
                    final String countStr = Integer.toString(count);
                    Log.info("  Found ", countStr, " '", sourceTableName, "' records to copy.");
                }
            } else {
                final String sql = SimpleBuilder.concat("SELECT * FROM ", sourceTableName);

                try (final ResultSet rs = statement.executeQuery(sql)) {
                    final ResultSetMetaData meta = rs.getMetaData();
                    final int cols = meta.getColumnCount();
                    final int[] types = new int[cols];

                    final HtmlBuilder builder = new HtmlBuilder(200);
                    builder.add("INSERT INTO ", targetTableName, " (");
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
                    try (final PreparedStatement prepared = prodConn.prepareStatement(insert)) {
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

                            final int count = prepared.executeUpdate();
                            if (count == 1) {
                                ++numRows;
                            } else {
                                Log.warning("  FAILED to insert record into '", targetTableName, "' table");
                            }
                        }

                        prodConn.commit();
                        Log.info("  Copied " + numRows + " records from '", sourceTableName, "' to '",
                                targetTableName,
                                "' table.");
                    }
                }
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

        final Runnable obj = new PostArchiveCleanup(cache);
        obj.run();
    }
}