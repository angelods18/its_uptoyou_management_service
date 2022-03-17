package it.itsuptoyou.service;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.xml.bind.ValidationException;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.ValidationFailedException;

public interface UserService {

	Map<String,Object> firstStepRegistration(Map<String,Object> registrationRequest) throws NoSuchAlgorithmException,ValidationFailedException, IllegalArgumentException;

	Map<String,Object> secondStepRegistration(Map<String,Object> registrationRequest) throws ValidationFailedException, ClassNotFoundException;

	Map<String, Object> updateUserProfile(Map<String,Object> updateProfileRequest) throws NumberFormatException, ClassNotFoundException, ConcurrentModificationException;

	User getProfile(String username) throws ClassNotFoundException;
	
	Map<String,Object> getOtherprofile(long userId) throws ClassNotFoundException;
	
	Boolean passwordRecovery(Map<String,Object> passwordRecoveryRequest) throws ClassNotFoundException, NoSuchAlgorithmException;

	Boolean changePassword(Map<String, Object> changePasswordRequest, Boolean isLogged) throws ClassNotFoundException, ValidationFailedException;
	
}
