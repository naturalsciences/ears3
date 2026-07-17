package eu.eurofleets.ears3.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Lets a browser session established via LoginController stand in for the
 * Authorization header that endpoints like OntologyRdfController read directly
 * via @RequestHeader(value = "Authorization", required = false). If a request
 * carries no Authorization header of its own and the session has one stashed
 * (set on successful /login, cleared on /logout or session expiry), this wraps
 * the request so that header appears present - no change needed in the
 * controllers themselves.
 */
@Component
public class SessionAuthorizationHeaderFilter implements Filter {

    public static final String SESSION_ATTR_AUTHORIZATION = "ears.session.authorizationHeader";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getHeader("Authorization") == null) {
            HttpSession session = httpRequest.getSession(false);
            Object stored = session != null ? session.getAttribute(SESSION_ATTR_AUTHORIZATION) : null;
            if (stored instanceof String authHeader) {
                httpRequest = new AuthorizationHeaderRequest(httpRequest, authHeader);
            }
        }
        chain.doFilter(httpRequest, response);
    }

    private static final class AuthorizationHeaderRequest extends HttpServletRequestWrapper {
        private final String authorizationHeader;

        AuthorizationHeaderRequest(HttpServletRequest request, String authorizationHeader) {
            super(request);
            this.authorizationHeader = authorizationHeader;
        }

        @Override
        public String getHeader(String name) {
            return "Authorization".equalsIgnoreCase(name) ? authorizationHeader : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(authorizationHeader));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            if (names.stream().noneMatch(n -> n.equalsIgnoreCase("Authorization"))) {
                names.add("Authorization");
            }
            return Collections.enumeration(names);
        }
    }
}
