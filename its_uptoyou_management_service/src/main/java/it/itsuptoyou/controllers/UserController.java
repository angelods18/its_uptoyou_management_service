package it.itsuptoyou.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.ValidationFailedException;
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
	public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> registrationRequest) throws NoSuchAlgorithmException, IllegalArgumentException, ValidationFailedException{
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
	public ResponseEntity<?> confirmRegistration(@RequestBody Map<String,Object> confirmRegRequest) throws ValidationFailedException, NotFoundException{
		Map<String,Object> registeredUser = userService.secondStepRegistration(confirmRegRequest);
		return ResponseEntity.ok(registeredUser);
	}
	
	
	/**
	 * update user profile 
	 * @param updateProfileRequest 
	 * @return user with updated profile
	 * @throws NumberFormatException
	 * @throws ClassNotFoundException
	 * @throws ConcurrentModificationException
	 */
	@PatchMapping(value="/protected/update-profile")
	public ResponseEntity<?> updateProfile(@RequestBody Map<String,Object> updateProfileRequest) throws NumberFormatException, NotFoundException, ConcurrentModificationException{
		Map<String,Object> user = userService.updateUserProfile(updateProfileRequest);
		return ResponseEntity.ok(user);
	}
	
	/**
	 * get profile information 
	 * @param request
	 * @return
	 * @throws ClassNotFoundException
	 */
	@GetMapping(value="/protected/profile")
	public ResponseEntity<?> getProfile(HttpServletRequest request) throws NotFoundException{
		log.info(request.getHeader("username"));
		User u = userService.getProfile(request.getHeader("username"));
		return ResponseEntity.ok(u);
	}
	
	/**
	 * 
	 * @param request
	 * @param userId
	 * @return profile of other person by userId
	 * @throws NotFoundException
	 */
	@GetMapping(value="/protected/profile/{userId}")
	public ResponseEntity<?> getProfileOfUser(HttpServletRequest request, @PathVariable("userId") String userId) throws NotFoundException{
		log.info(request.getHeader("username"));
		Map<String,Object> user = userService.getOtherprofile(Long.valueOf(userId));
		return ResponseEntity.ok(user);
	}
	
	/**
	 * start procedure for recovery password
	 * @param request email
	 * @return email send with otp
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping(value="/public/password-forgot")
	public ResponseEntity<?> passwordForget(@RequestBody Map<String,Object> request) throws NotFoundException, NoSuchAlgorithmException{
		if(userService.passwordRecovery(request)) {
			return ResponseEntity.ok("Procedura recupero password avviata");
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
	
	/**
	 * confirm change password after forgot request
	 * @param request email, password (the new one), otp
	 * @return
	 * @throws ClassNotFoundException
	 * @throws ValidationFailedException
	 */
	@PostMapping(value="/public/change-password")
	public ResponseEntity<?> changePassword(@RequestBody Map<String,Object> request) throws NotFoundException, ValidationFailedException{
		if(userService.changePassword(request,false)) {
			return ResponseEntity.ok("Cambio password avvenuto con successo");
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
	
	/**
	 * 
	 * change password for user logged in
	 * 
	 * @param request: email, password (as new one), oldPassword
	 * @return
	 * @throws ClassNotFoundException
	 * @throws ValidationFailedException
	 */
	@PostMapping(value="/protected/change-password")
	public ResponseEntity<?> changePasswordLogged(@RequestBody Map<String,Object> request) throws NotFoundException, ValidationFailedException{
		if(userService.changePassword(request,true)) {
			return ResponseEntity.ok("Cambio password avvenuto con successo");
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
}
