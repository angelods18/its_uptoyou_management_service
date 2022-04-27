package it.itsuptoyou.serviceimpls;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.imagekit.sdk.ImageKit;
import it.itsuptoyou.collections.Authority;
import it.itsuptoyou.collections.Friendship;
import it.itsuptoyou.collections.InvitationCode;
import it.itsuptoyou.collections.Friendship.FriendshipStatus;
import it.itsuptoyou.collections.RegisteringUser;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.collections.UserOtp;
import it.itsuptoyou.enums.AuthorityName;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.exceptions.ValidationFailedException;
import it.itsuptoyou.models.Profile;
import it.itsuptoyou.models.requests.RegistrationFirstStepRequest;
import it.itsuptoyou.repositories.FriendsRepository;
import it.itsuptoyou.repositories.InvitationRepository;
import it.itsuptoyou.repositories.RegisteringUserRepository;
import it.itsuptoyou.repositories.UserOtpRepository;
import it.itsuptoyou.repositories.UserRepository;
import it.itsuptoyou.service.CustomSequenceService;
import it.itsuptoyou.service.UserService;
import it.itsuptoyou.utils.ImageUtils;
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
	private InvitationRepository invitationRepository;
	
	@Autowired
	private FriendsRepository friendsRepository;
	
	@Autowired
	private CustomSequenceService customSequenceService;
	
	@Autowired
	private SecureCodeUtils secureCodeUtils;
	
	@Autowired
	private ImageUtils imageUtils;
	
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
	public Map<String, Object> firstStepRegistration(RegistrationFirstStepRequest registrationRequest) throws NoSuchAlgorithmException, ValidationFailedException, PreconditionFailedException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		String email="";
		String username="";
		String password="";
		try {
			email = registrationRequest.getEmail();
			username = registrationRequest.getUsername();
			password = registrationRequest.getPassword();
		}catch(NullPointerException e) {
			log.error("null pointer " + e);
			throw new ValidationFailedException("user","email and username and password cannot be null");
		}
		
		//check if email or username already exist
		List<User> users = userRepository.findByEmailOrUsername(email, username);
		List<RegisteringUser> regUsers = registeringUserRepository.findByEmailOrUsername(email, username);
		
		if(users.size()>0 || regUsers.size()>0) {
			throw new PreconditionFailedException("user","email or username already in use");
		}
		
		RegisteringUser newUser = new RegisteringUser();
		String secureCode = secureCodeUtils.generateSecureCode();
		
		newUser.setEmail(email);
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(password));
		
		newUser.setSecureCode(secureCode);
		if(registrationRequest.getInvitationCode()!=null &&
				!registrationRequest.getInvitationCode().isBlank()) {
			newUser.setInvitationCode(registrationRequest.getInvitationCode());
		}
		newUser = registeringUserRepository.save(newUser);
		
		//TODO encrypting del secureCode dentro l'URL
		String message= registrationFirstStepPart1 + newUser.getUsername() + registrationFirstStepPart2;
		message = message + "<a href="+ secureCode + ">qui</a>";
		message = message + registrationFirstStepPart3;
		
		try {
			emailSender.sendMessage(newUser.getEmail(), "It's up to you - Registrazione", message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			log.error("Error in sending registration email");
		}
		
		Map<String,Object> response = mapper.convertValue(newUser, Map.class);
		return response;
	}
	
	@Override
	public Map<String, Object> secondStepRegistration(Map<String, Object> registrationRequest) throws NotFoundException, PreconditionFailedException, ValidationFailedException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		//mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-ddTHH:mm:ss"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		String secureCode="";
		try {
			 secureCode = registrationRequest.get("secureCode").toString();
		}catch(NullPointerException np) {
			throw new ValidationFailedException("secureUser","secureCode not found");
		}
		RegisteringUser regUser = registeringUserRepository.findBySecureCode(secureCode).orElseThrow(() -> new NotFoundException("registeringUser"));
		
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
		
		if(regUser.getInvitationCode()!=null) {
			
			Optional<InvitationCode> invCode = invitationRepository.findByInvitationCode(regUser.getInvitationCode());
			if(invCode.isPresent()) {
				Optional<User> invitingUser = userRepository.findByUserId(invCode.get().getUserId());
				if(invitingUser.isPresent()) {
					//amicizia reciproca più eventuali bonus all'utente che lo ha invitato
					saveFriendship(u.getUserId(), invitingUser.get().getUserId());
				}
			}
		}
		return resp;
	}
	
	@Override
	public Map<String, Object> updateUserProfile(Map<String, Object> updateProfileRequest) 
			throws NumberFormatException, NotFoundException, ConcurrentModificationException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		Profile profile = mapper.convertValue(updateProfileRequest.get("profile"), Profile.class);
		User u = userRepository.findByUserId(Long.parseLong(updateProfileRequest.get("userId").toString()))
				.orElseThrow(() -> new NotFoundException("user"));
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
	public Boolean updateProfileImage(String username, MultipartFile file) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		try {
			imageUtils.uploadImage(file, user);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Error in uploading image" + e);
			return false;
		}
	}
	
	@Override
	public User getProfile(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		user.setPassword(null);
		return user;
	}
	
	@Override
	public String getProfileImage(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		
		return imageUtils.getImage(user);
	}
	
	@Override
	public Map<String, Object> getOtherprofile(long userId) throws NotFoundException{
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		User user = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("user"));
		Map<String,Object> userMap = mapper.convertValue(user, Map.class);
		userMap.remove("id");
		userMap.remove("version");
		userMap.remove("password");
		userMap.remove("email");
		userMap.remove("authorities");
		return userMap;
	}
	
	@Override
	public Boolean passwordRecovery(Map<String, Object> passwordRecoveryRequest)
			throws NotFoundException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		User u = userRepository.findByEmail(passwordRecoveryRequest.get("email").toString()).orElseThrow(() -> new NotFoundException("user"));
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
	public Boolean changePassword(Map<String, Object> changePasswordRequest, Boolean isLogged) throws NotFoundException, ValidationFailedException {
		// TODO Auto-generated method stub
		if(!isLogged) {
			String otp = changePasswordRequest.get("otp").toString();
			User user = userRepository.findByEmail(changePasswordRequest.get("email").toString()).orElseThrow(() -> new NotFoundException("user"));
			UserOtp userOtp = userOtpRepository.findByUserId(user.getUserId()).orElseThrow(() -> new NotFoundException("userOtp"));
			String oldPassword = user.getPassword();
			if(passwordEncoder.matches(changePasswordRequest.get("password").toString(), oldPassword)) {
				log.info("password match");
				throw new ValidationFailedException("password","must be different");
			}else {
				if(userOtp.getOtp().equals(otp)) {
					user.setPassword(passwordEncoder.encode(changePasswordRequest.get("password").toString()));
					user.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
					user = userRepository.save(user);
					return true;
				}else {
					throw new ValidationFailedException("otp","wrong code");
				}
				
			}
		}else {
			User user = userRepository.findByEmail(changePasswordRequest.get("email").toString()).orElseThrow(() -> new NotFoundException("user"));
			String oldPassword = user.getPassword();
			if(passwordEncoder.matches(changePasswordRequest.get("password").toString(), oldPassword) || 
					!passwordEncoder.matches(changePasswordRequest.get("oldPassword").toString(), oldPassword)) {
				throw new ValidationFailedException("password","mismatch");
			}else {
				user.setPassword(passwordEncoder.encode(changePasswordRequest.get("password").toString()));
				user.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
				user = userRepository.save(user);
				return true;
			}
		}
		
	}
	
	private Friendship saveFriendship(long userA, long userB) {
		Friendship friendshipAtoB = new Friendship();
		Friendship friendshipBtoA = new Friendship();
		friendshipAtoB.setUserA(userA);
		friendshipAtoB.setUserB(userB);
		friendshipAtoB.setStatus(FriendshipStatus.ACCEPTED);
		friendshipAtoB.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		friendshipAtoB.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		friendshipBtoA.setUserA(userB);
		friendshipBtoA.setUserB(userA);
		friendshipBtoA.setStatus(FriendshipStatus.ACCEPTED);
		friendshipBtoA.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		friendshipBtoA.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		log.info("saving friendship between user :" + userA + " and user: " + userB);
		friendsRepository.save(friendshipBtoA);
		return friendsRepository.save(friendshipAtoB);
	}
	
	@Override
	public Boolean sentToSupport(String username, Map<String, Object> request) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("user"));
		try {
			emailSender.sendMessageToSupport(user.getEmail(), request.get("subject").toString(), request.get("message").toString());
			return true;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
