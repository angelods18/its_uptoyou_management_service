package it.itsuptoyou.service;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.springframework.web.multipart.MultipartFile;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.exceptions.ValidationFailedException;
import it.itsuptoyou.models.requests.RegistrationFirstStepRequest;
import it.itsuptoyou.models.requests.UpdateProfileRequest;

public interface UserService {

	Map<String,Object> firstStepRegistration(RegistrationFirstStepRequest registrationRequest) throws NoSuchAlgorithmException,ValidationFailedException, PreconditionFailedException;

	Map<String,Object> secondStepRegistration(Map<String,Object> registrationRequest) throws PreconditionFailedException,ValidationFailedException, NotFoundException;

	Map<String, Object> updateUserProfile(UpdateProfileRequest updateProfileRequest) throws NumberFormatException, NotFoundException, ConcurrentModificationException;

	Boolean updateProfileImage(String username, MultipartFile file) throws NotFoundException;
	
	User getProfile(String username) throws NotFoundException;
	
	String getProfileImage(String username) throws NotFoundException;
	
	Map<String,Object> getOtherprofile(long userId) throws NotFoundException;
	
	Boolean passwordRecovery(Map<String,Object> passwordRecoveryRequest) throws NotFoundException, NoSuchAlgorithmException;

	Boolean changePassword(Map<String, Object> changePasswordRequest, Boolean isLogged) throws NotFoundException, ValidationFailedException;
	
	Boolean sentToSupport(String username, Map<String,Object> request) throws NotFoundException;
	
	;
}
