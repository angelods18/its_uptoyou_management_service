package it.itsuptoyou.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecureCodeUtils {

	String chars= "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public String generateSecureCode() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
	    // 9 is the length of the string you want
	    String customTag = secureRandom.ints(36, 0, chars.length()).mapToObj(i -> chars.charAt(i))
	      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	    log.info(customTag);
		return customTag;
	}
}
