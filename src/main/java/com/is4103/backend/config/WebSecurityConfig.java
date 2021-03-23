package com.is4103.backend.config;

import com.is4103.backend.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UnauthorisedEntryPoint unauthorisedEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS disable CSRF
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/test/**", "/greeting", "/user/register/**", "/organiser/register/**",
                        "/partner/register/**", "/attendee/register/**", "/user/login/**", "/user/reset-password/**",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/user/{\\d+}", "/downloadFile/**",
                        "/organiser/event/{\\d+}", "/partner/followers/{\\d+}", "/partner/following/{\\d+}",
                        "/organiser/attendeeFollowers/{\\d+}", "/organiser/partnerFollowers/{\\d+}","/organiser/event/{\\d+}/**", "/review/eo/{\\d+}", "/review/{\\d+}", "/partner/events/{\\d+}/**", "/event/public/**")
                .permitAll()
                // can also be used to protected routes
                .antMatchers("/user/userping").hasRole("USER").antMatchers("/user/adminping").hasRole("ADMIN")
                .anyRequest().authenticated().and()
                // handle unanthenticated requests
                .exceptionHandling().authenticationEntryPoint(unauthorisedEntryPoint).and()
                // stateless session management
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

}