package org.example.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Uncoment if you are testing
                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/index",
                                "/home",
                                "/surveys",
                                "/surveys/*",
                                "/surveys/*/close",
                                "/surveys/*/questions",
                                "/surveys/*/questions/*/answers",
                                "/surveys/*",
                                "/surveys/*/close" ,
                                "/surveys/*/response",
                                "/surveys/*/answer",
                                "/surveys/*/share",
                                "/css/*"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

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
        UserDetails alice =
                User.withDefaultPasswordEncoder()
                        .username("alice")
                        .password("password1")
                        .roles("USER")
                        .build();

        UserDetails bob =
                User.withDefaultPasswordEncoder()
                        .username("bob")
                        .password("password2")
                        .roles("USER")
                        .build();

        UserDetails charlie =
                User.withDefaultPasswordEncoder()
                        .username("charlie")
                        .password("password3")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user, alice, bob, charlie);
    }
}