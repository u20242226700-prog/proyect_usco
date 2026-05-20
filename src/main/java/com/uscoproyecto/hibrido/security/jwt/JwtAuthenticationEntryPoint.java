package com.uscoproyecto.hibrido.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.error("Error de autenticación: {}", authException.getMessage());

        String acceptHeader = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");

        if ((acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equals(requestedWith)
                || request.getRequestURI().startsWith("/api/")) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println(
                "{ \"error\": \"No autorizado\", \"message\": \"" + authException.getMessage() + "\" }"
            );
        } else {
            response.sendRedirect("/login");
        }
    }
}