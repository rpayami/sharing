package com.sharing.config;

import com.sharing.account.AccountSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final AccountSecurityService accountSecurityService;

    SecurityConfig(AccountSecurityService accountSecurityService) {
        this.accountSecurityService = accountSecurityService;
    }

    @Bean
    public PasswordEncoder buildPasswordEncoder() {
       return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/accounts").permitAll();
                    auth.anyRequest().authenticated();
                 })
                .csrf().disable()
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(accountSecurityService);
        return provider;
    }

}
