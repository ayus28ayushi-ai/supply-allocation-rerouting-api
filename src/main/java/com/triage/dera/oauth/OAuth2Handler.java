package com.triage.dera.oauth;

import com.triage.dera.entity.AuthProvider;
import com.triage.dera.entity.UserPrincipal;
import com.triage.dera.service.JwtService;
import com.triage.dera.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2Handler extends SimpleUrlAuthenticationSuccessHandler {


    private final UserService userService;
    private final JwtService jwtService;

    public OAuth2Handler(@Lazy UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }



    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // 1. Get the provider safely
            AuthProvider provider = AuthProvider.GOOGLE;
            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                String registrationId = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
                try {
                    provider = AuthProvider.valueOf(registrationId);
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown provider {}, defaulting to GOOGLE", registrationId);
                }
            }

            // 2. Extract email
            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                String login = oAuth2User.getAttribute("login");
                email = (login != null) ? login + "@github.user" : "unknown@social.user";
            }

            // 3. Extract name
            String name = oAuth2User.getAttribute("name");
            if (name == null) {
                name = oAuth2User.getAttribute("login");
            }

            // 4. Search or create user in PostgreSQL
            UserPrincipal principal = userService.processOAuthUser(email, name, provider);

            // 5. Generate standard JWT
            String token = jwtService.generateToken(principal);

            // 6. Redirect to Swagger UI with token parameter
            String targetUrl = UriComponentsBuilder.fromUriString("/swagger-ui/index.html")
                    .queryParam("token", token)
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception ex) {
            log.error("Error processing OAuth2 success login: ", ex);
            // On error, redirect back to login page with error param instead of crashing
            getRedirectStrategy().sendRedirect(request, response, "/login.html?error=oauth_processing_failed");
        }
    }
}