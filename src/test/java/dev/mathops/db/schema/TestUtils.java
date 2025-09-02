package dev.mathops.db.schema;

import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.cfg.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.schema.legacy.impl.RawWhichDbLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Constants used across RawLogic tests.
 */
public enum TestUtils {
    ;

    /** The database context. */
    public static final String CONTEXT = Contexts.POSTGRES_TEST_PATH;
//    public static final String CONTEXT = Contexts.INFORMIX_TEST_PATH;

    /**
     * Establishes a database connection and verifies that it is connected to the TEST database.
     *
     * @return the data cache for the new connection
     * @throws IllegalArgumentException if the context (configured in this class) does not provide a connection to the
     *                                  TEST database
     */
    public static Cache ensureConnectedToTest() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        final Profile profile = config.getCodeProfile(CONTEXT);
        if (profile == null) {
            final String msg = TestRes.get(TestRes.ERR_NO_TEST_PROFILE);
            throw new IllegalArgumentException(msg);
        }

        final Login login = profile.getLogin(ESchema.LEGACY);
        final DbConnection conn = login.checkOutConnection();
        final Cache cache = new Cache(profile);

        final String whichDbName = RawWhichDbLogic.getTableName(cache);
        final String sql = "SELECT descr FROM " + whichDbName;

        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                final String which = rs.getString(1);
                if (which != null) {
                    final String trimmed = which.trim();
                    if (!"TEST".equals(trimmed)) {
                        final String msg = TestRes.fmt(TestRes.ERR_NOT_CONNECTED_TO_TEST, which);
                        throw new IllegalArgumentException(msg);
                    }
                }
            } else {
                final String msg = TestRes.get(TestRes.ERR_CANT_QUERY_WHICH_DB);
                throw new IllegalArgumentException(msg);
            }
        } catch (final SQLException ex) {
            Log.warning(ex);
            final String msg = ex.getMessage();
            fail("Exception while confirming connection to TEST database: " + msg);
        } finally {
            login.checkInConnection(conn);
        }

        return cache;
    }
}
