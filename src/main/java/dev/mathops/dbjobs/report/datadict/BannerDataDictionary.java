package dev.mathops.dbjobs.report.datadict;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that queries and prints the Banner data dictionary.
 */
final class BannerDataDictionary {

    /** The database profile through which to access the database. */
    private final Profile profile;

    /** The login for the Live schema in the batch profile. */
    private final Login liveLogin;

    /**
     * Constructs a new {@code OdsDataDictionary}.
     */
    private BannerDataDictionary() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        this.profile = config.getCodeProfile(Contexts.BATCH_PATH);

        Login theLogin = null;
        if (this.profile != null) {
            theLogin = this.profile.getLogin(ESchema.LIVE);
        }

        this.liveLogin = theLogin;
    }

    /**
     * Executes the job.
     *
     * @return a report
     */
    private String execute() {

        final Collection<String> report = new ArrayList<>(100);

        if (this.profile == null) {
            report.add("Unable to locate batch profile.");
        } else if (this.liveLogin == null) {
            report.add("Unable to locate LIVE database login.");
        } else {
            execute(report);
        }

        final HtmlBuilder htm = new HtmlBuilder(1000);
        for (final String rep : report) {
            htm.addln(rep);
        }

        return htm.toString();
    }

    /**
     * Executes the query against the ODS and loads data into the primary schema.
     *
     * @param report a list of strings to which to add report output lines
     */
    private void execute(final Collection<? super String> report) {

        final DbConnection odsConn = this.liveLogin.checkOutConnection();

        try {
            queryBannerFields(odsConn, report);
        } catch (final SQLException ex) {
            Log.warning(ex);
            report.add("Unable to perform query: " + ex.getMessage());
        } finally {
            this.liveLogin.checkInConnection(odsConn);
        }
    }

    /**
     * Queries the data dictionary tables and prints the contents.
     *
     * @param conn   the database connection
     * @param report a list to which to add report lines
     * @throws SQLException if there is an error performing the query
     */
    private static void queryBannerFields(final DbConnection conn, final Collection<? super String> report)
            throws SQLException {

        report.add("Querying Banner database metadata...");
        report.add(CoreConstants.EMPTY);

        final DatabaseMetaData metadata = conn.getConnection().getMetaData();

        try (final ResultSet tables = metadata.getTables(null, null, null, null)) {
            while (tables.next()) {
                final String schema = tables.getString("TABLE_SCHEM");
                final String name = tables.getString("TABLE_NAME");

                if (name.indexOf('/') > 0) {
                    Log.warning("Skipping table: ", name);
                    continue;
                }

                report.add(SimpleBuilder.concat("SCHEMA.TABLE: '", schema, ".", name, "'"));

                try (final ResultSet columns = metadata.getColumns(null, schema, name, null)) {
                    while (columns.next()) {
                        final String colName = columns.getString("COLUMN_NAME");
                        final String colType = columns.getString("TYPE_NAME");
                        final String size = columns.getString("COLUMN_SIZE");
                        if (size == null) {
                            report.add(SimpleBuilder.concat("    ", colName, " (", colType, ")"));
                        } else {
                            report.add(SimpleBuilder.concat("    ", colName, " (", colType, "[", size, "])"));
                        }
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

        final BannerDataDictionary job = new BannerDataDictionary();

        final File dir = new File("C:\\opt\\zircon\\data");

        if (dir.exists() || dir.mkdirs()) {
            final File dataDictFile = new File(dir, "banner_data_dict.txt");
            final String fileData = job.execute();

            try (final FileWriter fw = new FileWriter(dataDictFile)) {
                final String dataString = fileData;
                fw.write(dataString);
            } catch (final IOException ex) {
                Log.warning("Failed to write data dictionary JSON file.", ex);
            }
        }
    }
}
