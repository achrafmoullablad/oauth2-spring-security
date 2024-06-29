package com.achrafmoullablad.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private JwtEncoder jwtEncoder;
	private AuthenticationManager authenticationManager;
	private JwtDecoder jwtDecoder;
	private UserDetailsService userDetailsService;

	public AuthService(JwtEncoder jwtEncoder, AuthenticationManager authenticationManager, JwtDecoder jwtDecoder,
			UserDetailsService userDetailsService) {
		this.jwtEncoder = jwtEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtDecoder = jwtDecoder;
		this.userDetailsService = userDetailsService;
	}

	public Map<String, String> authenticate(String grantType, String username, String password,
			boolean withRefreshToken, String refreshToken) {

		String subject = null;
		String scope = null;

		if (grantType.equals("password")) {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			subject = authentication.getName();
			scope = authentication.getAuthorities().stream().map(auth -> auth.getAuthority())
					.collect(Collectors.joining(" "));

		} else if (grantType.equals("refreshToken")) {
			Jwt decodeJwt = null;
			try {
				decodeJwt = jwtDecoder.decode(refreshToken);
			} catch (JwtException e) {
				return Map.of("errorMessage", e.getMessage());
			}
			subject = decodeJwt.getSubject();
			UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
			Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
			scope = authorities.stream().map(auth -> auth.getAuthority()).collect(Collectors.joining(" "));
		}

		Map<String, String> idToken = new HashMap<>();
		Instant instant = Instant.now();
		JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder().subject(subject).issuedAt(instant)
				.expiresAt(instant.plus(withRefreshToken ? 1 : 30, ChronoUnit.MINUTES)).issuer("security-service")
				.claim("scope", scope).build();
		String jwtAcessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
		idToken.put("accessToken", jwtAcessToken);

		if (withRefreshToken) {
			JwtClaimsSet jwtClaimsSetRefresh = JwtClaimsSet.builder().subject(subject).issuedAt(instant)
					.expiresAt(instant.plus(30, ChronoUnit.MINUTES)).issuer("security-service").build();
			String jwtRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
			idToken.put("refreshToken", jwtRefreshToken);
		}

		return idToken;
	}

}
