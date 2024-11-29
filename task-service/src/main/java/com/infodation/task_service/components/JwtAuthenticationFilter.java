package com.infodation.task_service.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate;

    @Value("${auth.service.baseUrl}")
    private String authServiceUrl;

    public JwtAuthenticationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ResponseEntity<String> validationResponse = null;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String message = null;
        int status = HttpServletResponse.SC_OK;
        try {
            String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (jwt == null || !jwt.startsWith("Bearer ")) {
                status = HttpServletResponse.SC_UNAUTHORIZED;
                message = "Missing or invalid Authorization header";
                throw new ServletException(message);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, jwt);
            
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);

            validationResponse = restTemplate.exchange(
                     authServiceUrl + "/api/validate",
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            if (validationResponse.getStatusCode() == HttpStatus.OK) {
                String userDetails = validationResponse.getBody();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

        } catch (HttpClientErrorException.Forbidden e) {
            status = HttpServletResponse.SC_FORBIDDEN;
            message = "Token validation failed";
            log.error(message);
        } catch (ServletException e) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            message = "Servlet exception: " + e.getMessage();
            log.error(message);
        } catch (Exception e) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred during token validation";
            log.error(message);
        }

        response.setStatus(status);
        String jsonResponse = String.format("{\"status\": %d, \"message\": \"%s\"}", status, message);
        response.getWriter().write(jsonResponse);
    }

}
