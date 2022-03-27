package it.itsuptoyou.service;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.xml.bind.ValidationException;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.ValidationFailedException;

public interface UserService {

	Map<String,Object> firstStepRegistration(Map<String,Object> registrationRequest) throws NoSuchAlgorithmException,ValidationFailedException, IllegalArgumentException;

	Map<String,Object> secondStepRegistration(Map<String,Object> registrationRequest) throws ValidationFailedException, NotFoundException;

	Map<String, Object> updateUserProfile(Map<String,Object> updateProfileRequest) throws NumberFormatException, NotFoundException, ConcurrentModificationException;

	User getProfile(String username) throws NotFoundException;
	
	Map<String,Object> getOtherprofile(long userId) throws NotFoundException;
	
	Boolean passwordRecovery(Map<String,Object> passwordRecoveryRequest) throws NotFoundException, NoSuchAlgorithmException;

	Boolean changePassword(Map<String, Object> changePasswordRequest, Boolean isLogged) throws NotFoundException, ValidationFailedException;
	
	Boolean sentToSupport(String username, Map<String,Object> request) throws NotFoundException;
	
	String getProfileImage(String username);
}
