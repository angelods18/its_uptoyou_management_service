package it.itsuptoyou.serviceimpls;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.itsuptoyou.collections.Authority;
import it.itsuptoyou.collections.RegisteringUser;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.enums.AuthorityName;
import it.itsuptoyou.repositories.RegisteringUserRepository;
import it.itsuptoyou.repositories.UserRepository;
import it.itsuptoyou.service.CustomSequenceService;
import it.itsuptoyou.service.UserService;
import it.itsuptoyou.utils.MailUtils;
import it.itsuptoyou.utils.SecureCodeUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements UserService{
	
	@Autowired
	private RegisteringUserRepository registeringUserRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomSequenceService customSequenceService;
	
	@Autowired
	private SecureCodeUtils secureCodeUtils;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailUtils emailSender;
	
	@Value(value="${registration.first.step.part1}")
	private String registrationFirstStepPart1;
	
	@Value(value="${registration.first.step.part2}")
	private String registrationFirstStepPart2;
	
	@Value(value="${registration.first.step.part3}")
	private String registrationFirstStepPart3;
	
	@Override
	public Map<String, Object> firstStepRegistration(Map<String, Object> registrationRequest) throws NoSuchAlgorithmException, IllegalArgumentException, ValidationException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		String email="";
		String username="";
		String password="";
		try {
			email = registrationRequest.get("email").toString();
			username = registrationRequest.get("username").toString();
			password = registrationRequest.get("password").toString();
		}catch(NullPointerException e) {
			log.error("null pointer " + e);
			throw new ValidationException("email, username and password cannot be null");
		}
		
		//check if email or username already exist
		List<User> users = userRepository.findByEmailOrUsername(email, username);
		List<RegisteringUser> regUsers = registeringUserRepository.findByEmailOrUsername(email, username);
		
		if(users.size()>0 || regUsers.size()>0) {
			throw new IllegalArgumentException("email or username already in use");
		}
		
		RegisteringUser newUser = new RegisteringUser();
		String secureCode = secureCodeUtils.generateSecureCode();
		
		newUser.setEmail(email);
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(password));
		
		newUser.setSecureCode(secureCode);
		
		newUser = registeringUserRepository.save(newUser);
		String message= registrationFirstStepPart1 + newUser.getUsername() + registrationFirstStepPart2;
		message = message + "<a href="+ secureCode + ">qui</a>";
		message = message + registrationFirstStepPart3;
		
		try {
			emailSender.sendMessage(newUser.getEmail(), "It's up to you - Registrazione", message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,Object> response = mapper.convertValue(newUser, Map.class);
		return response;
	}
	
	@Override
	public Map<String, Object> secondStepRegistration(Map<String, Object> registrationRequest) throws ValidationException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		String secureCode="";
		try {
			 secureCode = registrationRequest.get("secureCode").toString();
		}catch(NullPointerException np) {
			throw new ValidationException("secureCode not found");
		}
		RegisteringUser regUser = registeringUserRepository.findBySecureCode(secureCode).orElseThrow(() -> new ClassNotFoundException("registeringUser"));
		
		User u = new User();
		u.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		u.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		u.setEmail(regUser.getEmail());
		u.setPassword(regUser.getPassword());
		u.setUsername(regUser.getUsername());
		Authority authorityUser=new Authority();
		authorityUser.setName(AuthorityName.ROLE_USER);
		u.setAuthorities( Arrays.asList(new Authority[] {authorityUser}));
		u.setAccountId(customSequenceService.generateSequence("customSequences", "user"));
		
		u=userRepository.save(u);
		Map<String,Object> resp = mapper.convertValue(u, Map.class);
		return resp;
	}
}
