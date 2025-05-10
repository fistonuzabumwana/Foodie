package com.example.Foodie.config;

import com.example.Foodie.service.UserDetailsServiceImpl; // Your custom UserDetailsService
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    // Constructor injection for UserDetailsServiceImpl
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for strong, salted password hashing
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Set your custom UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder());     // Set the PasswordEncoder
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/",
                                        "/home",
                                        "/css/**",
                                        "/js/**",
                                        "/cart/**",
                                        "/images/**",
                                        "/webjars/**", // Ensure webjars are permitted
                                        "/products", // Assuming you'll have this soon
                                        "/products/view/**", // Assuming you'll have this soon
                                        "/register", // <<-- ADD THIS
                                        "/login"
                                ).permitAll() // These URLs are publicly accessible
                                .requestMatchers("/admin/**").hasRole("ADMIN") // URLs starting with /admin/ require ADMIN role
                                .requestMatchers("/checkout/**", "/orders/**").authenticated() // Secure checkout and future order pages
                                .requestMatchers("/checkout").authenticated() // Require authentication for checkout
                                .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // Specify custom login page URL
                                .loginProcessingUrl("/perform_login") // URL to submit the username and password to
                                .defaultSuccessUrl("/home", true) // Redirect to /home on successful login
                                .failureUrl("/login?error=true") // Redirect to /login?error=true on login failure
                                .permitAll() // Allow everyone to access the login page
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/perform_logout") // Specify logout URL
                                .logoutSuccessUrl("/login?logout=true") // Redirect to /login?logout=true on successful logout
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID") // Delete session cookie
                                .permitAll() // Allow everyone to access the logout functionality
                )
                // .csrf(csrf -> csrf.disable()) // TEMPORARY: Disable CSRF for easier testing with Postman/forms initially.
                // Re-enable and handle CSRF tokens for production!
                .authenticationProvider(authenticationProvider()); // Set the custom authentication provider

        return http.build();
    }
}