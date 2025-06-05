package dev.mathops.db.rec.main;

import dev.mathops.db.rec.RecBase;
import dev.mathops.db.rec.main.LtiRegistrationRec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code LtiRegistrationRec} class.
 */
final class TestLtiRegistrationRec {

    /** A field name. */
    private static final String TEST_CLIENT_ID = "1234CLIENT1234";

    /** A field name. */
    private static final String TEST_ISSUER = "domino.math.colostate.edu";

    /** A field name. */
    private static final String TEST_REDIRECT_URI = "https://coursedev.math.colostate.edu/lti/lti13_callback";

    /** A field name. */
    private static final String TEST_ISSUER_PORT = ":12345";

    /** A field name. */
    private static final String TEST_AUTH_ENDPOINT =
            "https://domino.math.colostate.edu:20443/api/lti/authorize_redirect";

    /** A field name. */
    private static final String TEST_TOKEN_ENDPOINT = "https://domino.math.colostate.edu:20443/login/oauth2/token";

    /** A field name. */
    private static final String TEST_REG_ENDPOINT = "https://domino.math.colostate.edu:20443/api/lti/registrations";

    /** A field name. */
    private static final String TEST_JWKS_URI = "https://domino.math.colostate.edu:20443/api/lti/security/jwks";

    /** The expected String serialization of a test record. */
    private static final String EXPECT_SER99 = String.join(RecBase.DIVIDER,
            "client_id=1234CLIENT1234",
            "issuer=domino.math.colostate.edu",
            "issuer_port=:12345",
            "redirect_uri=https://coursedev.math.colostate.edu/lti/lti13_callback",
            "auth_endpoint=https://domino.math.colostate.edu:20443/api/lti/authorize_redirect",
            "token_endpoint=https://domino.math.colostate.edu:20443/login/oauth2/token",
            "reg_endpoint=https://domino.math.colostate.edu:20443/api/lti/registrations",
            "jwks_uri=https://domino.math.colostate.edu:20443/api/lti/security/jwks");

    /**
     * Constructs a new {@code TestLtiRegistrationRec}.
     */
    TestLtiRegistrationRec() {

        // No action
    }

    /** Test case. */
    @Test
    @DisplayName("Constructor")
    void test0001() {

        final LtiRegistrationRec obj = new LtiRegistrationRec(TEST_CLIENT_ID, TEST_ISSUER, TEST_ISSUER_PORT,
                TEST_REDIRECT_URI, TEST_AUTH_ENDPOINT, TEST_TOKEN_ENDPOINT, TEST_REG_ENDPOINT, TEST_JWKS_URI);

        assertEquals(TEST_CLIENT_ID, obj.clientId, "Invalid client ID value after constructor");
        assertEquals(TEST_ISSUER, obj.issuer, "Invalid issuer value after constructor");
        assertEquals(TEST_ISSUER_PORT, obj.issuerPort, "Invalid issuer port value after constructor");
        assertEquals(TEST_REDIRECT_URI, obj.redirectUri, "Invalid redirect URI value after constructor");
        assertEquals(TEST_AUTH_ENDPOINT, obj.authEndpoint, "Invalid auth endpoint value after constructor");
        assertEquals(TEST_TOKEN_ENDPOINT, obj.tokenEndpoint, "Invalid token endpoint value after constructor");
        assertEquals(TEST_REG_ENDPOINT, obj.regEndpoint, "Invalid reg endpoint value after constructor");
        assertEquals(TEST_JWKS_URI, obj.jwksUri, "Invalid JWKS URI value after constructor");
    }

    /** Test case. */
    @Test
    @DisplayName("string serialization")
    void test0098() {

        final LtiRegistrationRec obj = new LtiRegistrationRec(TEST_CLIENT_ID, TEST_ISSUER, TEST_ISSUER_PORT,
                TEST_REDIRECT_URI, TEST_AUTH_ENDPOINT, TEST_TOKEN_ENDPOINT, TEST_REG_ENDPOINT, TEST_JWKS_URI);

        final String ser = obj.toString();

        assertEquals(EXPECT_SER99, ser, "Invalid serialized string");
    }
}
