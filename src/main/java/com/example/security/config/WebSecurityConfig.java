package com.example.security.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.security.ExampleAuthenticationEntryPoint;
import com.example.security.auth.TokenExtractor;
import com.example.security.filter.JwtAuthenticationProcessingFilter;
import com.example.security.filter.JwtAuthenticationProvider;
import com.example.security.filter.ExampleAuthenticationFailureHandler;
import com.example.security.filter.ExampleAuthenticationFilter;
import com.example.security.filter.ExampleAuthenticationProvider;
import com.example.security.filter.ExampleCorsFilter;
import com.example.security.filter.SkipPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
	public static final String AUTHENTICATION_URL = "/api/auth/login";
	public static final String REFRESH_TOKEN_URL = "/api/auth/token/**";
	public static final String RESET_PASSWORD = "/api/auth/resetPassword";
	public static final String FORGOT_PASSWORD = "/api/auth/forgotPassword";
	public static final String API_ROOT_URL = "/api/**";
	public static final String SWAGGER_URL = "/swagger-ui.html";

	@Autowired
	private ExampleAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private AuthenticationSuccessHandler successHandler;

	@Autowired
	private ExampleAuthenticationFailureHandler failureHandler;

	@Autowired
	private ExampleAuthenticationProvider authenticationProvider;

	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;

	@Autowired
	private TokenExtractor tokenExtractor;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private ObjectMapper objectMapper;

	protected ExampleAuthenticationFilter buildExampleAuthenticationFilter(String authenticationEntryPoint)
			throws Exception {
		ExampleAuthenticationFilter filter = new ExampleAuthenticationFilter(authenticationEntryPoint, successHandler,
				failureHandler, objectMapper);
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}

	protected JwtAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(List<String> pathsToSkip,
			String pattern) throws Exception {
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
		JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter(failureHandler, tokenExtractor,
				matcher);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		logger.debug("Configuring auth endpoints");
		auth.authenticationProvider(authenticationProvider);
		auth.authenticationProvider(jwtAuthenticationProvider);
		logger.debug("Completed auth endpoints");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("Configuring security endpoints");
		List<String> permitAllEndpointList = Arrays.asList(AUTHENTICATION_URL, REFRESH_TOKEN_URL, FORGOT_PASSWORD,
				RESET_PASSWORD, SWAGGER_URL, "/console");

		http.csrf().disable().exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint)

				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and().authorizeRequests()
				.antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()])).permitAll().and()
				.authorizeRequests().antMatchers(API_ROOT_URL).authenticated().and()
				.addFilterBefore(new ExampleCorsFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(buildExampleAuthenticationFilter(AUTHENTICATION_URL),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList, API_ROOT_URL),
						UsernamePasswordAuthenticationFilter.class);

		logger.debug("Completed security endpoints");
	}

}
