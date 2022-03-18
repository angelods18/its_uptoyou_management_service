package it.itsuptoyou.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecureCodeUtils {

	String chars= "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	String numbers = "0123456789";
	
	public String generateSecureCode() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
	    // 36 is the length of the string you want
	    String customTag = secureRandom.ints(36, 0, chars.length()).mapToObj(i -> chars.charAt(i))
	      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	    log.info(customTag);
		return customTag;
	}
	
	public String generateSecureCode(int length) throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
	    String customTag = secureRandom.ints(length, 0, chars.length()).mapToObj(i -> chars.charAt(i))
	      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	    log.info(customTag);
		return customTag;
	}
	
	public String generateInvitationSuffix() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		String code = secureRandom.ints(3,0, numbers.length()).mapToObj(i -> chars.charAt(i))
	      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		return code;
	}
	
	public String generateOtp() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		String otp = secureRandom.ints(6,0, numbers.length()).mapToObj(i -> chars.charAt(i))
	      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		return otp;
	}
}
