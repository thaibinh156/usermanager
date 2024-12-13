package com.infodation.task_service.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    @Value("${auth.service.baseUrl}")
    private String authServiceUrl;

    final String BEAR_TOKEN_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
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

            if (jwt == null || !jwt.startsWith(BEAR_TOKEN_PREFIX)) {
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
                String responseBody = validationResponse.getBody();
                JsonNode jsonNode = mapper.readTree(responseBody);

                String userId = jsonNode.get("data").get("userId").asText();
                List<String> roles = new ArrayList<>();
                JsonNode roleJson = jsonNode.get("data").get("roles");

                roleJson.forEach(x -> {
                    roles.add(x.get("name").asText());
                });

                if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_USER")) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    status = HttpServletResponse.SC_FORBIDDEN;
                    message = "User does not have the required role";
                    throw new ServletException(message);
                }
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
