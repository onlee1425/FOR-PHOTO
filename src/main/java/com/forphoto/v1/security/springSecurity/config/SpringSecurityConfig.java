package com.forphoto.v1.security.springSecurity.config;

import com.forphoto.v1.security.jwt.JwtAuthenticationEntryPoint;
import com.forphoto.v1.security.jwt.JwtAuthenticationFilter;
import com.forphoto.v1.security.jwt.JwtTokenProvider;
import com.forphoto.v1.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public static final String[] PERMIT_URL_ARRAY = {
            "/error","/js/**", "/css/**", "/image/**", "/dummy/**",
            "/favicon.ico", "/**/favicon.ico",

            //swagger
            "/swagger-ui.html", "/swagger-ui/index.html", "/swagger/**","/swagger-ui/**",
            "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs", "/webjars/**",
    };
    public static final String[] PERMIT_API_ARRAY = {
            "/","/api/user/register","/api/user/login"
    };

    public static final String[] PERMIT_GET_API_ARRAY = {
            "/api/albums/{albumId}/photos","/api/albums/{albumId}/photos/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,jwtTokenUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .antMatchers(PERMIT_API_ARRAY).permitAll()
                .antMatchers(HttpMethod.GET,PERMIT_GET_API_ARRAY).permitAll()

                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;

    }
}
