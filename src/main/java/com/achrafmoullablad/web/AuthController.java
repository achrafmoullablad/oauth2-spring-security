package com.achrafmoullablad.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achrafmoullablad.service.AuthService;

@RestController
public class AuthController {

	private AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/token")
	public ResponseEntity<Map<String, String>> jwtToken(String grantType, String username, String password,
			boolean withRefreshToken, String refreshToken) {

		if (grantType.equals("refreshToken")) {
			if (refreshToken == null)
				return new ResponseEntity<>(Map.of("errorMessage", "Refresh Token is required"),
						HttpStatus.UNAUTHORIZED);
		}
		Map<String, String> idToken = authService.authenticate(grantType, username, password, withRefreshToken,
				refreshToken);

		return new ResponseEntity<>(idToken, HttpStatus.OK);
	}

}