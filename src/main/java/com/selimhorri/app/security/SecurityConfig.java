package com.selimhorri.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.selimhorri.app.business.user.model.RoleBasedAuthority;
import com.selimhorri.app.config.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final JwtRequestFilter jwtRequestFilter;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService)
				.passwordEncoder(this.passwordEncoder);
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.cors().disable()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers("/", "index", "**/css/**", "**/js/**").permitAll()
				.antMatchers("/api/authenticate/**").permitAll()

				// User Resource
				.antMatchers(HttpMethod.POST, "/api/users").permitAll()
				.antMatchers(HttpMethod.GET, "/api/users").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.antMatchers(HttpMethod.GET, "/api/users/username/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/users/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PUT, "/api/users/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/users/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Credentials resource
				.antMatchers(HttpMethod.GET, "/api/credentials").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/credentials/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.GET, "/api/credentials/username/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.POST, "/api/credentials")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PUT, "/api/credentials/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/credentials/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Address resource
				.antMatchers(HttpMethod.GET, "/api/address").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/address/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/address")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PUT, "/api/address/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/address/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// VerificationToken resource
				.antMatchers(HttpMethod.GET, "/api/verificationTokens").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/verificationTokens/*")
				.hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.POST, "/api/verificationTokens")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PUT, "/api/verificationTokens/*")
				.hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/verificationTokens/*")
				.hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				// Product resource
				.antMatchers(HttpMethod.GET, "/api/products").permitAll()
				.antMatchers(HttpMethod.GET, "/api/products/*").permitAll()
				.antMatchers(HttpMethod.POST, "/api/products").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.antMatchers(HttpMethod.PUT, "/api/products/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.antMatchers(HttpMethod.DELETE, "/api/products/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				// Categories resource
				.antMatchers(HttpMethod.GET, "/api/categories").permitAll()
				.antMatchers(HttpMethod.GET, "/api/categories/*").permitAll()
				.antMatchers(HttpMethod.POST, "/api/categories").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.antMatchers(HttpMethod.PUT, "/api/categories/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.antMatchers(HttpMethod.DELETE, "/api/categories/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				// Cart resource
				.antMatchers(HttpMethod.GET, "/api/carts").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/carts/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/carts")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/carts/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Order resource
				.antMatchers(HttpMethod.GET, "/api/orders").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/orders/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/orders")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PATCH, "/api/orders/*/status")
				.hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.PUT, "/api/orders/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/orders/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Favourite resource
				.antMatchers(HttpMethod.GET, "/api/favourites").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/favourites/*/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/favourites")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/favourites/*/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Payment resource
				.antMatchers(HttpMethod.GET, "/api/payments").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/payments/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/payments")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.PUT, "/api/payments/*").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/payments/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Shipping resource
				.antMatchers(HttpMethod.GET, "/api/shippings").hasRole(RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers(HttpMethod.GET, "/api/shippings/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.POST, "/api/shippings")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				.antMatchers(HttpMethod.DELETE, "/api/shippings/*")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole(), RoleBasedAuthority.ROLE_USER.getRole())

				// Default
				.antMatchers("/api/**")
				.hasAnyRole(RoleBasedAuthority.ROLE_USER.getRole(),
						RoleBasedAuthority.ROLE_ADMIN.getRole())

				.antMatchers("/actuator/health/**", "/actuator/info/**")
				.permitAll()
				.antMatchers("/actuator/**")
				.hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
				.anyRequest().authenticated()
				.and()
				.headers()
				.frameOptions()
				.sameOrigin()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(this.jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
