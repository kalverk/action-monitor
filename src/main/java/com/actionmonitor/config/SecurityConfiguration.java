package com.actionmonitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static com.actionmonitor.constants.AuthorizationConstants.USER;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // Simple in-memory authentication with three users
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser("user1").password("{noop}user1").roles(USER)
                .and()
                .withUser("user2").password("{noop}user2").roles(USER)
                .and()
                .withUser("user3").password("{noop}user3").roles(USER);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // disable CSRF for simplicity and configure a session registry which will allow us to fetch a list of users
        http.csrf().disable().sessionManagement().maximumSessions(-1).sessionRegistry(sessionRegistry());
    }
}