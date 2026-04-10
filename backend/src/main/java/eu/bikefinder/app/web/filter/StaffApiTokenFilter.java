package eu.bikefinder.app.web.filter;

import eu.bikefinder.app.config.StaffProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Optional shared secret for procurement APIs when {@code ebf.staff.api-token} is set.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StaffApiTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Staff-Token";

    private final StaffProperties staffProperties;

    public StaffApiTokenFilter(StaffProperties staffProperties) {
        this.staffProperties = staffProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!staffProperties.isApiProtectionEnabled()) {
            return true;
        }
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/v1/offers")
                || uri.startsWith("/api/v1/price-sense")
                || uri.startsWith("/api/v1/competitor-watch")) {
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String expected = staffProperties.getApiToken().trim();
        String provided = request.getHeader(HEADER);
        if (provided != null && provided.equals(expected)) {
            filterChain.doFilter(request, response);
            return;
        }
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ") && auth.substring(7).trim().equals(expected)) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Staff token required (" + HEADER + " or Authorization: Bearer)");
    }
}
