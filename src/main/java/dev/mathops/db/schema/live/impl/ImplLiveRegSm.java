package dev.mathops.db.schema.live.impl;

import dev.mathops.db.DbConnection;
import dev.mathops.db.schema.live.rec.LiveReg;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of the {@code ILiveReg} interface for the CSU Banner schema.
 */
public final class ImplLiveRegSm extends AbstractImplLiveReg {

    /** A single instance. */
    public static final ImplLiveRegSm INSTANCE = new ImplLiveRegSm();

    /** The name of the primary table. */
    private static final String TABLE_NAME = "CSUS_MATH_REGISTRATION_SMR";

    /**
     * Constructs a new {@code ImplLiveReg}.
     */
    private ImplLiveRegSm() {

        super();
    }

    /**
     * Gets the name of the primary table.
     *
     * @return the table name
     */
    @Override
    public String getTableName() {

        return TABLE_NAME;
    }

    /**
     * Queries for a particular student.
     *
     * @param conn      the database connection, checked out to this thread
     * @param studentId the student ID
     * @return the list of models that matched the criteria, a zero-length array if none matched
     * @throws SQLException if there is an error performing the query
     */
    public List<LiveReg> query(final DbConnection conn, final String studentId) throws SQLException {

        final String sql = "SELECT * FROM CSUS_MATH_REGISTRATION_SMR WHERE CSU_ID='" + studentId + "'";

        return executeSimpleQuery(conn, sql);
    }
}
