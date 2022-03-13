package it.itsuptoyou.serviceimpls;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.itsuptoyou.collections.RegisteringUser;
import it.itsuptoyou.repositories.RegisteringUserRepository;
import it.itsuptoyou.service.UserService;
import it.itsuptoyou.utils.SecureCodeUtils;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private RegisteringUserRepository registeringUserRepository;
	
	@Autowired
	private SecureCodeUtils secureCodeUtils;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public Map<String, Object> firstStepRegistration(Map<String, Object> registrationRequest) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		
		RegisteringUser newUser = new RegisteringUser();
		String secureCode = secureCodeUtils.generateSecureCode();
		
		newUser.setUsername(registrationRequest.get("username").toString());
		String password = registrationRequest.get("password").toString();
		newUser.setPassword(passwordEncoder.encode(password));
		newUser.setSecureCode(secureCode);
		
		newUser = registeringUserRepository.save(newUser);
		Map<String,Object> response = mapper.convertValue(newUser, Map.class);
		return response;
	}
}
