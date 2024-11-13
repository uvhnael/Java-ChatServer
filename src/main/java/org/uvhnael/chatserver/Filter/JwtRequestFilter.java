package org.uvhnael.chatserver.Filter;

import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.uvhnael.chatserver.Utility.JwtUtility;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility; // Inject JwtUtility để kiểm tra token

    @Override
    protected void doFilterInternal(final jakarta.servlet.http.HttpServletRequest request, final jakarta.servlet.http.HttpServletResponse response, final jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization"); // Lấy header Authorization

        String username = null;
        String userId = null; // Biến để lưu trữ ID
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtility.extractUsername(jwt);
            userId = jwtUtility.extractId(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtility.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }


        filterChain.doFilter(request, response);
    }
}
