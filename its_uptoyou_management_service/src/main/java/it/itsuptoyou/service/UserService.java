package it.itsuptoyou.service;

import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.xml.bind.ValidationException;

import it.itsuptoyou.collections.User;

public interface UserService {

	Map<String,Object> firstStepRegistration(Map<String,Object> registrationRequest) throws NoSuchAlgorithmException,ValidationException, IllegalArgumentException;

	Map<String,Object> secondStepRegistration(Map<String,Object> registrationRequest) throws ValidationException, ClassNotFoundException;

	Map<String, Object> updateUserProfile(Map<String,Object> updateProfileRequest) throws NumberFormatException, ClassNotFoundException, ConcurrentModificationException;

	User getProfile(String username) throws ClassNotFoundException;
}
