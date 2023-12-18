package com.example.redistest.jwt;


import com.example.redistest.exception.UserNotFoundException;
import com.example.redistest.model.User;
import com.example.redistest.repository.UserRepository;
import com.example.redistest.service.RateLimiter;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.Claims;
import io.micrometer.core.instrument.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RateLimiter rateLimiter;

    public JwtTokenFilter(JwtService jwtUtil, UserRepository userRepository, RateLimiter rateLimiter) {
        this.jwtService = jwtUtil;
        this.userRepository = userRepository;
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String tenantId = request.getParameter("username"); //todo
        String encodedString = Base64.getEncoder().encodeToString(tenantId.getBytes());
        if (StringUtils.isNotBlank(encodedString)) {
            Bucket bucket = rateLimiter.resolveBucket(encodedString);
            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS.value());
            }
        } else {
            sendErrorResponse(response, HttpStatus.FORBIDDEN.value());
        }

        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        if (!jwtService.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(token, request);


    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")) {
            return false;
        }

        return true;
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String token) {
        User userDetails;
        Claims claims = jwtService.parseClaims(token);
        String subject = (String) claims.get(Claims.SUBJECT);
        userDetails = userRepository.findByUsername(subject).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userDetails;
    }

    private void sendErrorResponse(HttpServletResponse response, int value) {
        response.setStatus(value);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
