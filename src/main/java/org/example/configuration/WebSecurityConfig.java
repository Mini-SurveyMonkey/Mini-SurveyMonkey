package org.example.configuration;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/register", "/processing-registration").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"))
                .headers(headers ->
                        headers.frameOptions((frameOptions) -> frameOptions.disable())
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl( "/logout")
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler((request, response, auth) -> {
                            for (Cookie cookie : request.getCookies()) {
                                String cookieName = cookie.getName();
                                Cookie cookieToDelete = new Cookie(cookieName, null);
                                cookieToDelete.setMaxAge(0);
                                response.addCookie(cookieToDelete);
                            }
                        })
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}