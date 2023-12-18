package com.example.redistest.filter;

import java.io.IOException;


import com.example.redistest.service.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.github.bucket4j.Bucket;
import io.micrometer.core.instrument.util.StringUtils;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/v1")) {
            String tenantId = request.getHeader("X-Tenant");
            if(StringUtils.isNotBlank(tenantId)) {
                Bucket bucket = rateLimiter.resolveBucket(tenantId);
                if(bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS.value());
                }
            } else {
                sendErrorResponse(response, HttpStatus.FORBIDDEN.value());
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

    private void sendErrorResponse(HttpServletResponse response, int value) {
        response.setStatus(value);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

}
