package it.itsuptoyou.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.service.UserService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
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
	 *   "password": string,
	 *   "email": string
	 * }
	 * Receive basic information for new user
	 * Send email with a generated secure code
	 * @return String Success
	 * @throws NoSuchAlgorithmException 
	 * @throws ValidationException 
	 */
	@PostMapping(value="/public/register")
	public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> registrationRequest) throws NoSuchAlgorithmException, IllegalArgumentException, ValidationException{
		Map<String,Object> newUser= userService.firstStepRegistration(registrationRequest);
		return ResponseEntity.ok(newUser);
	}
	
	/**
	 * Request: 
	 * { 
	 *   "secureCode": string
	 * }
	 * Receive secureCode to complete registration
	 * @return String Success
	 * @throws ValidationException 
	 * @throws ClassNotFoundException 
	 */
	@PostMapping(value="/public/confirm-registration")
	public ResponseEntity<?> confirmRegistration(@RequestBody Map<String,Object> confirmRegRequest) throws ValidationException, ClassNotFoundException{
		Map<String,Object> registeredUser = userService.secondStepRegistration(confirmRegRequest);
		return ResponseEntity.ok(registeredUser);
	}
	
	@PatchMapping(value="/protected/update-profile")
	public ResponseEntity<?> updateProfile(@RequestBody Map<String,Object> updateProfileRequest) throws NumberFormatException, ClassNotFoundException, ConcurrentModificationException{
		Map<String,Object> user = userService.updateUserProfile(updateProfileRequest);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping(value="/protected/profile")
	public ResponseEntity<?> getProfile(HttpServletRequest request) throws ClassNotFoundException{
		log.info(request.getHeader("username"));
		User u = userService.getProfile(request.getHeader("username"));
		return ResponseEntity.ok(u);
	}
	
	@PostMapping(value="/public/password-forgot")
	public ResponseEntity<?> passwordForget(@RequestBody Map<String,Object> request) throws ClassNotFoundException, NoSuchAlgorithmException{
		if(userService.passwordRecovery(request)) {
			return ResponseEntity.ok("Procedura recupero password avviata");
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
}
