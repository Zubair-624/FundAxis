package com.fundaxis.config;

import com.fundaxis.security.CustomAuthenticationSuccessHandler;
import com.fundaxis.security.CustomUserDetailsService;
import com.fundaxis.security.MustChangePasswordFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final MustChangePasswordFilter mustChangePasswordFilter;

    // Constructor injection
    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationSuccessHandler authenticationSuccessHandler,
            MustChangePasswordFilter mustChangePasswordFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.mustChangePasswordFilter = mustChangePasswordFilter;

    }

    ///==================== Password Encoding ====================///

    // PasswordEncoder used to securely hash user passwords, and Plain-text passwords must never be stored in the database
    // BCrypt creates a one-way hash that will be stored in: UserAccount.passwordHash
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    ///==================== Authentication provider used by Spring Security for username/password authentication ====================///

    // It connects: CustomUserDetailsService + PasswordEncoder
    // CustomUserDetailsService loads the user from the FundAxis database
    // PasswordEncoder verifies the submitted password
    // Spring Security uses DaoAuthenticationProvider for username/password authentication. It loads the user through UserDetailsService and checks the submitted password against the stored encoded password using PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Loads the user through CustomUserDetailsService (FundAxis UserAccount records)
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(customUserDetailsService);

        // Compares the submitted password with the stored BCrypt hash
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    ///==================== Security Filter Chains ====================///
    // SecurityFilterChain -> customizing web security, and this defines how HTTP requests are secured.
    // FundAxis now uses TWO filter chains. Spring checks them in @Order sequence,
    // and the first chain whose securityMatcher matches the request handles it:
    //   Order 1 -> /api/**  (REST API, HTTP Basic, stateless)
    //   Order 2 -> everything else (Thymeleaf pages, Form Login, HttpSession)

    ///---------- API SECURITY ----------///
    //
    // Handles:
    // /api/**
    //
    // Used mainly by Postman and REST API clients.
    //
    // Authentication:
    // HTTP Basic
    //
    // CSRF:
    // Disabled because this API chain is stateless.
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                // This chain only applies to requests starting with /api/
                .securityMatcher("/api/**")

                // Register our database authentication provider
                // Spring Security will now authenticate users using FundAxis UserAccount records
                .authenticationProvider(authenticationProvider())

                // CSRF is disabled for the REST API
                // because it is stateless and uses HTTP Basic Authentication
                .csrf(csrf -> csrf.disable())

                // Stateless: no HttpSession is created, credentials are sent with every request
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                ///----- authorizeHttpRequests() -> decides what the authenticated user can access / Define protected endpoints. -----///
                .authorizeHttpRequests(auth -> auth



                        // Allow everyone to access the home page and static resources (CSS, JavaScript, and images) without logging in.
                        .requestMatchers(
                                "/home",        // Home Page
                                "/css/**",      // CSS files
                                "/js/**",       // JavaScript files
                                "/images/**"    // Image files
                        )
                        .permitAll()              // No authentication is required to access these resources

                        ///----- /api/user-accounts/** -> ADMIN or IT_SUPPORT -----///
                        // Only ADMIN or IT_SUPPORT can manage user accounts
                        .requestMatchers("/api/user-accounts/**")
                        .hasAnyRole("ADMIN", "IT_SUPPORT")

                        ///----- /api/roles/** -> ADMIN -----///
                        // Only ADMIN can manage roles
                        .requestMatchers("/api/roles/**")
                        .hasRole("ADMIN")

                        ///----- everything else -> authenticated -----///
                        // All remaining API endpoints require authentication.
                        //
                        // More detailed permissions are enforced using
                        // @PreAuthorize inside the controllers.
                        .anyRequest()
                        .authenticated())

                // Enable HTTP Basic Authentication for protected API requests
                .httpBasic(Customizer.withDefaults())

                // Block users who must change their password (runs after login, so the user is already known)
                // Enforce mustChangePassword for API users
                .addFilterAfter(mustChangePasswordFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    ///---------- THYMELEAF / WEB SECURITY ----------///
    //
    // Handles normal browser pages.
    //
    // Authentication:
    // Form Login + HttpSession
    //
    // CSRF:
    // Enabled.
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                // Register our database authentication provider
                // Spring Security will now authenticate users using FundAxis UserAccount records
                .authenticationProvider(authenticationProvider())

                // Define which requests are public and which require authentication
                .authorizeHttpRequests(auth -> auth

                        // Public home page, login page and static frontend resources
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error")
                        .permitAll()

                        // Password change page is available to
                        // every authenticated account
                        .requestMatchers("/change-password")
                        .authenticated()

                        // General dashboard redirect
                        .requestMatchers("/dashboard")
                        .authenticated()

                        ///----- /admin/** -> ADMIN -----///
                        // ADMIN frontend
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        ///----- /it-support/** -> IT_SUPPORT -----///
                        // IT Support frontend
                        .requestMatchers("/it-support/**")
                        .hasRole("IT_SUPPORT")

                        ///----- /user/** -> USER -----///
                        // Normal employee frontend
                        .requestMatchers("/user/**")
                        .hasRole("USER")

                        ///----- everything else -> authenticated -----///
                        // Every other request requires authentication
                        .anyRequest()
                        .authenticated())

                // Custom FundAxis login page (POST /login is handled by Spring Security, not by our JS)
                .formLogin(form -> form

                        // Page shown to the user when login is required
                        .loginPage("/login")

                        // URL the login form submits to
                        .loginProcessingUrl("/login")

                        // Names of the form fields in the login page
                        .usernameParameter("username")
                        .passwordParameter("password")

                        // After successful login, our handler decides where the user goes (based on role)
                        .successHandler(authenticationSuccessHandler)

                        // Redirect back to the login page when credentials are wrong
                        .failureUrl("/login?error")

                        // Login page and login processing must be public
                        .permitAll())

                // Secure logout
                .logout(logout -> logout

                        // URL that triggers logout
                        .logoutUrl("/logout")

                        // Where the user lands after logging out
                        .logoutSuccessUrl("/login?logout")

                        // Destroy the server-side session
                        .invalidateHttpSession(true)

                        // Remove the stored authentication
                        .clearAuthentication(true)

                        // Delete the session cookie from the browser
                        .deleteCookies("JSESSIONID")

                        .permitAll())

                // Block users who must change their password (runs after login, so the user is already known)
                // Check mustChangePassword after web authentication
                .addFilterAfter(mustChangePasswordFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}