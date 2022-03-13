package it.itsuptoyou.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;	
	
	@GetMapping(value="/public/ping")
	public String ping() {
		return "PONG";
	}
	
	/**
	 * Request: 
	 * { 
	 *   "username": string,
	 *   "password": string
	 * }
	 * Receive basic information for new user
	 * Send email with a generated secure code
	 * @return String Success
	 * @throws NoSuchAlgorithmException 
	 */
	@PostMapping(value="/public/register")
	public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> registrationRequest) throws NoSuchAlgorithmException{
		Map<String,Object> newUser= userService.firstStepRegistration(registrationRequest);
		return ResponseEntity.ok(newUser);
	}
}
