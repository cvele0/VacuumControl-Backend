package rs.raf.demo.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.raf.demo.model.UserPermission;
import rs.raf.demo.services.UserService;
import rs.raf.demo.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        String requestURI = httpServletRequest.getRequestURI();
        String requestMethod = httpServletRequest.getMethod();

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            String username2 = userDetails.getUsername();

            int permissions = this.userService.getPermissionsByEmail(username2);

            if (requestMethod.equalsIgnoreCase("get") &&
                    requestURI.endsWith("api/users/all") && ((permissions & UserPermission.CAN_READ_USERS) == 0)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            if (requestMethod.equalsIgnoreCase("post") &&
                    requestURI.endsWith("api/users") && ((permissions & UserPermission.CAN_CREATE_USERS) == 0)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            if (requestMethod.equalsIgnoreCase("put") &&
                    requestURI.endsWith("api/users") && ((permissions & UserPermission.CAN_UPDATE_USERS) == 0)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            if (requestMethod.equalsIgnoreCase("delete") &&
                    requestURI.contains("api/users") && ((permissions & UserPermission.CAN_DELETE_USERS) == 0)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
