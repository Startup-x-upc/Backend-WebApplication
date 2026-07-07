package org.example.backendwebapplication.iam.infrastructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Spring Security filter that intercepts every request and validates
 * the JWT access token from the {@code Authorization: Bearer <token>} header.
 * <p>On success, populates {@link SecurityContextHolder} with the
 * authenticated user's identity and role.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());

            try {
                var claims = jwtService.parseAccessToken(token);
                var payload = claims.getPayload();

                UUID userId = UUID.fromString(payload.getSubject());
                String role = payload.get("role", String.class);
                String email = payload.get("email", String.class);

                var authorities = List.of(
                        new SimpleGrantedAuthority(role));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);
                // Attach email as details for convenience
                authentication.setDetails(email);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // Token is invalid — leave the context unauthenticated.
                // The security config will reject the request if the endpoint requires auth.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
