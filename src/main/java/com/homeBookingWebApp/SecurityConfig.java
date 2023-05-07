package com.homeBookingWebApp;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.homeBookingWebApp.CustomLoginHandler.CustomLoginFailureHandler;
import com.homeBookingWebApp.CustomLoginHandler.CustomLoginSuccessHandler;
import com.homeBookingWebApp.Service.UserSecurityService;
import com.homeBookingWebApp.Utility.SecurityConstant;
import com.homeBookingWebApp.Utility.SecurityUtility;
import com.homeBookingWebApp.Utility.Filter.JwtAccessDeniedHandler;
import com.homeBookingWebApp.Utility.Filter.JwtAuthenticationEntryPoint;
import com.homeBookingWebApp.Utility.Filter.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private BCryptPasswordEncoder bCryptPasswordEncoder() {
        return SecurityUtility.passwordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userSecurityService).passwordEncoder(bCryptPasswordEncoder());
        
        http
		.authorizeHttpRequests(auth -> {
			auth.antMatchers(SecurityConstant.PUBLIC_URLS).permitAll();
			auth.anyRequest().authenticated();
		});
        http
                .csrf(csrf -> csrf.disable())
                .cors().and()
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)).addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/?logout")
                        .permitAll()).rememberMe(me -> me
                .tokenValiditySeconds(3 * 24 * 60 * 60).tokenRepository(persistentTokenRepository())); 
            		


      /* http.authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll().anyRequest().authenticated(); */

       /* http.csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/?logout")
                .permitAll().and().rememberMe()
                .tokenValiditySeconds(3 * 24 * 60 * 60).tokenRepository(persistentTokenRepository()); */
                        		


        return http.build();
    }

    @Bean
    PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }


    @Bean
    AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private CustomLoginFailureHandler loginFailureHandler;

    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

}
