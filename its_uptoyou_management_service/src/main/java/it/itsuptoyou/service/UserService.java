package it.itsuptoyou.service;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface UserService {

	Map<String,Object> firstStepRegistration(Map<String,Object> registrationRequest) throws NoSuchAlgorithmException;
}
