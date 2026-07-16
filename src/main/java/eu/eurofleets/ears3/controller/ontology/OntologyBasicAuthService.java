package eu.eurofleets.ears3.controller.ontology;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Basic Auth check for ontology publish/download operations, ported from the
 * legacy OntologyController's authenticate()/decodeBase64() logic.
 *
 * Reuses the same property names (ears.ontology.username / ears.ontology.password)
 * so existing .env / application.properties configuration keeps working unchanged.
 *
 * Uses java.util.Base64 (JDK built-in) instead of the legacy code's
 * org.apache.commons.codec.binary.Base64, avoiding an extra dependency for
 * something the JDK already provides directly.
 */
@Component
public class OntologyBasicAuthService {

    private final Environment env;

    public OntologyBasicAuthService(Environment env) {
        this.env = env;
    }

    private String getUsername() {
        return env.getProperty("ears.ontology.username");
    }

    private String getPassword() {
        return env.getProperty("ears.ontology.password");
    }

    public boolean matches(String username, String password) {
        String expectedUser = getUsername();
        String expectedPass = getPassword();
        return expectedUser != null && expectedPass != null
                && expectedUser.equals(username) && expectedPass.equals(password);
    }

    public boolean isAuthorized(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return false;
        }
        Map<String, String> decoded = decodeBase64(authorizationHeader.substring("Basic ".length()));
        return matches(decoded.get("username"), decoded.get("password"));
    }

    /** Throws 401 if the Authorization header is missing or the credentials don't match. */
    public void assertAuthorized(String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid or missing credentials for this ontology operation.");
        }
    }

    private static Map<String, String> decodeBase64(String encoded) {
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        String pair = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] parts = pair.split(":", 2);
        Map<String, String> map = new HashMap<>();
        if (parts.length == 2) {
            map.put("username", parts[0]);
            map.put("password", parts[1]);
        }
        return map;
    }
}
