package it.itsuptoyou.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.exceptions.ValidationFailedException;
import it.itsuptoyou.models.requests.RegistrationFirstStepRequest;
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
	 *   "email": string,
	 *   "invitationCode": string
	 * }
	 * Receive basic information for new user
	 * Send email with a generated secure code
	 * 
	 * Rif: Registrazione base
	 *  RF_ID 1 
	 *  SF_1_1
	 * @return String Success
	 * @throws NoSuchAlgorithmException 
	 * @throws PreconditionFailedException
	 * @throws ValidationException 
	 */
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="newUser"),
		@ApiResponse(responseCode = "400", description="validation.user.emailAndUsernameAndPasswordCannotBeNull"),
		@ApiResponse(responseCode = "422", description = "precondition.user.emailOrUsernameAlreadyInUse")
	})
	@PostMapping(value="/public/register")
	public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationFirstStepRequest registrationRequest) throws NoSuchAlgorithmException, PreconditionFailedException, ValidationFailedException{
		
		
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
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "validation.secureCodeNotFound.secureUser")
	})
	@PostMapping(value="/public/confirm-registration")
	public ResponseEntity<?> confirmRegistration(@RequestBody Map<String,Object> confirmRegRequest) throws PreconditionFailedException, ValidationFailedException, NotFoundException{
		Map<String,Object> registeredUser = userService.secondStepRegistration(confirmRegRequest);
		return ResponseEntity.ok(registeredUser);
	}
	
	
	/**
	 * update user profile 
	 * @param updateProfileRequest 
	 * {
		    "userId":5,
		    "version":0,
		    "profile":{
		        "name":"Angelo",
		        "surname":"De Santis",
		        "birthdate": "1995-05-18",
		        "address":{
		            "formattedAddress":"Viale America, 24, Gaeta, 04024, LT, Lazio, Italia",
		            "city":"Gaeta",
		            "geoPoint":{
		                "type": "Point",
		                "coordinates":[
		                    13.13,
		                    41.15
		                ]
		            }
		        },
		        "gender":"M",
		        "school":"",
		        "job":"software developer"
		    }
		}
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
	
	@PutMapping(value="/protected/profile/image")
	public ResponseEntity<?> updateImageProfile(HttpServletRequest request ,@RequestPart MultipartFile image) throws NotFoundException{
		Boolean resp = userService.updateProfileImage(request.getHeader("username"), image);
		return ResponseEntity.ok(resp);
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
	
	@GetMapping(value="/protected/profile/image")
	public ResponseEntity<?> getProfileImage(HttpServletRequest request) throws NotFoundException {
		String urlImageProfile= userService.getProfileImage(request.getHeader("username"));
		return ResponseEntity.ok(urlImageProfile);
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
	
	/**
	 * send an email to the admin
	 * @param request: username from gateway
	 * @param requestBody: info about the support request: Title - Subject - Body
	 * @return
	 * @throws NotFoundException 
	 */
	@PostMapping(value="/protected/contact-support")
	public ResponseEntity<?> contactSupport(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException {
		if(userService.sentToSupport(request.getHeader("username"),requestBody)) {
			return ResponseEntity.ok("Email inviata con successo");
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
	
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
