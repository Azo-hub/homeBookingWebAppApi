package com.bookingWebAppApi.Utility.Filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bookingWebAppApi.Utility.JWTTokenProvider;
import com.bookingWebAppApi.Utility.SecurityConstant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTokenProvider jWTTokenProvider;

    private static List<String> skipFilterUrls = Arrays.asList("/login","/signUp","/newUser",
    		"/forgetPassword","/allPropertyByCategory","/eachPropertyById","/reviewByProperty","/getUserByUsername",
    		"/allPropertyByOwner","/contactPropertyOwner","/support","/uploadPropertyImageTest");
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // TODO Auto-generated method stub

        if (request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)) {

            response.setStatus(HttpStatus.OK.value());
        } else {

            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null ||
                    !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {

                filterChain.doFilter(request, response);

                return;
            }

            String token = authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length()).trim();

            String username = jWTTokenProvider.getSubject(token);

            if(jWTTokenProvider.isTokenValid(username, token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                List<GrantedAuthority> authorities = jWTTokenProvider.getAuthorities(token);

                Authentication authentication =
                        jWTTokenProvider.getAuthentication(username, authorities, request);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {

                SecurityContextHolder.clearContext();
            }

        }

        filterChain.doFilter(request, response);



    }
    
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

      return skipFilterUrls.stream().anyMatch(url -> new AntPathRequestMatcher(url).matches(request));
    }


}
