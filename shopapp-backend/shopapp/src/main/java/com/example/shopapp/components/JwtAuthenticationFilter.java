package com.example.shopapp.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            if(isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            // Endpoints that require authenticate
            final String authHeader = request.getHeader("Authorization");
            // Validate authentication header
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized - No JWT token found in the request headers.\"}");
                return;
            }
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            if(phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // load user
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
                // check if token valid
                if(jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("path", request.getRequestURI());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokenList = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST")
        );

        for(var byPassToken: bypassTokenList) {
            if(request.getServletPath().contains(byPassToken.getFirst())
                    && request.getMethod().equals(byPassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
