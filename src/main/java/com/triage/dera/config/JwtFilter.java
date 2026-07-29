package com.triage.dera.config;

import com.triage.dera.service.AppUserDetailsService;
import com.triage.dera.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext context;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         String authHeader = request.getHeader("Authorization");
         String token = null;
         String username = null;

         //acquire the token by cutting off "Bearer "
         if(authHeader!= null && authHeader.startsWith("Bearer ")){
             token = authHeader.substring(7);
             username=jwtService.extractUsername(token);
         }

         //validating user and checking if they are already authenticated
         if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
             UserDetails userDetails = context            //goes to postgreSQL via AppUserDetailsService and performs checks
                     .getBean(AppUserDetailsService.class)
                     .loadUserByUsername(username);
             if(jwtService.validateToken(token, userDetails)){
               //once the user token is validated
               //--  creates an official Spring Security Authentication object (authenticated)
               //-- give's the user's authority to it and saves it in the SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));   //It reads the raw HttpServletRequest object and extracts information about how and where the request came from
                SecurityContextHolder.getContext().setAuthentication(authToken);  //
             }
         }
         filterChain.doFilter(request, response);
    }
}
