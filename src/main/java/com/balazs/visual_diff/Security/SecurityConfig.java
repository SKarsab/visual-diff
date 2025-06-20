package com.balazs.visual_diff.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.security.username}")
    private String username;

    @Value("${app.security.password}")
    private String password;

	/**
     * Used to decode the stored hashed password in the incoming format from environment variables
     *
     * @return PasswordEncoder
     */
	@Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

	/**
     * Sets new security defaults for endpoints. "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health"
	 * are permitted by default with zero authentication/authorization. Remaining endpoints require
	 * username adn password to be supplied in header with "Basic Auth"
     *
     * @param HttpSecurity http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((requests) -> requests
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
			.anyRequest().authenticated()
		).httpBasic(httpBasic -> {});

		return http.build();
	}

	/**
     * Override default generated security username/password from Spring's security dependency.
     *
     * @return UserDetailsService
     */
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername(username).password(password).roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}
}
