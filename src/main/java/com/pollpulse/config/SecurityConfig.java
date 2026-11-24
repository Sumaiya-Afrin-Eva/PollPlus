package com.pollpulse.config;

import java.util.LinkedHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ApiAuthenticationEntryPoint apiAuthenticationEntryPoint,
                                                   ApiAccessDeniedHandler apiAccessDeniedHandler) throws Exception {
        RequestMatcher apiMatcher = new AntPathRequestMatcher("/api/**");
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(apiMatcher, apiAuthenticationEntryPoint);
        DelegatingAuthenticationEntryPoint authenticationEntryPoint =
                new DelegatingAuthenticationEntryPoint(entryPoints);
        authenticationEntryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));

        AccessDeniedHandler browserAccessDeniedHandler = new AccessDeniedHandlerImpl();
        AccessDeniedHandler accessDeniedHandler = (request, response, exception) -> {
            if (apiMatcher.matches(request)) {
                apiAccessDeniedHandler.handle(request, response, exception);
            } else {
                browserAccessDeniedHandler.handle(request, response, exception);
            }
        };

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/login", "/register",
                                "/api/auth/register", "/api/auth/csrf", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
