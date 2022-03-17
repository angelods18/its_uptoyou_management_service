package it.itsuptoyou.serviceimpls;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.itsuptoyou.collections.Authority;
import it.itsuptoyou.collections.RegisteringUser;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.collections.UserOtp;
import it.itsuptoyou.enums.AuthorityName;
import it.itsuptoyou.models.Profile;
import it.itsuptoyou.repositories.RegisteringUserRepository;
import it.itsuptoyou.repositories.UserOtpRepository;
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
	private UserOtpRepository userOtpRepository;
	
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
		
		//TODO encrypting del secureCode
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
		//TODO decrypying del secureCode
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
		u.setUserId(customSequenceService.generateSequence("customSequences", "user"));
		
		u=userRepository.save(u);
		Map<String,Object> resp = mapper.convertValue(u, Map.class);
		return resp;
	}
	
	@Override
	public Map<String, Object> updateUserProfile(Map<String, Object> updateProfileRequest) 
			throws NumberFormatException, ClassNotFoundException, ConcurrentModificationException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		Profile profile = mapper.convertValue(updateProfileRequest.get("profile"), Profile.class);
		User u = userRepository.findByUserId(Long.parseLong(updateProfileRequest.get("userId").toString()))
				.orElseThrow(() -> new ClassNotFoundException("user"));
		if(updateProfileRequest.get("version")!=u.getVersion()) {
			throw new ConcurrentModificationException("version");
		}
		u.setProfile(profile);
		u.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		u = userRepository.save(u);
		
		Map<String,Object> resp = mapper.convertValue(u, Map.class);
		return resp;
	}
	
	@Override
	public User getProfile(String username) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(() -> new ClassNotFoundException("user"));
		return user;
	}
	
@Override
	public Boolean passwordRecovery(Map<String, Object> passwordRecoveryRequest)
			throws ClassNotFoundException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		User u = userRepository.findByEmail(passwordRecoveryRequest.get("email").toString()).orElseThrow(() -> new ClassNotFoundException("user"));
		Optional<UserOtp> userOtp = userOtpRepository.findByUserId(u.getUserId());
		UserOtp userOtpRequest= new UserOtp();
		if(userOtp.isPresent()) {
			userOtpRequest = userOtp.get();
		}else {
			userOtpRequest.setUserId(u.getUserId());
		}
		userOtpRequest.setOtp(secureCodeUtils.generateOtp());
		userOtpRequest=userOtpRepository.save(userOtpRequest);
		String message= "";
		message = message + userOtpRequest.getOtp() ;
		message = message + "";
		
		try {
			emailSender.sendMessage(u.getEmail(), "It's up to you - Recupero password", message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public Boolean changePassword(Map<String, Object> changePasswordRequest) throws ClassNotFoundException, ValidationException {
		// TODO Auto-generated method stub
		String otp = changePasswordRequest.get("otp").toString();
		User user = userRepository.findByEmail(changePasswordRequest.get("email").toString()).orElseThrow(() -> new ClassNotFoundException("user"));;
		UserOtp userOtp = userOtpRepository.findByUserId(user.getUserId()).orElseThrow(() -> new ClassNotFoundException("userOtp"));
		String oldPassword = user.getPassword();
		if(passwordEncoder.matches(changePasswordRequest.get("password").toString(), oldPassword)) {
			log.info("password match");
			throw new ValidationException("password");
		}else {
			user.setPassword(passwordEncoder.encode(changePasswordRequest.get("password").toString()));
			user = userRepository.save(user);
			return true;
		}
	}
}
