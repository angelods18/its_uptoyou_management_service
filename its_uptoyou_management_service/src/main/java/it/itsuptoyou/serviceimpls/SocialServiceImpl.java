package it.itsuptoyou.serviceimpls;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.models.InvitationCode;
import it.itsuptoyou.repositories.InvitationRepository;
import it.itsuptoyou.repositories.UserRepository;
import it.itsuptoyou.service.CustomSequenceService;
import it.itsuptoyou.service.SocialService;
import it.itsuptoyou.utils.SecureCodeUtils;

@Service
public class SocialServiceImpl implements SocialService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private InvitationRepository invitationRepository;
	
	@Autowired
	private SecureCodeUtils secureCodeUtils;
	
	@Override
	public Map<String, Object> generateInvitationCode(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
		User u = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		
		InvitationCode invitationCode = new InvitationCode();
		invitationCode = invitationRepository.findByUserId(u.getUserId()).orElse(new InvitationCode());
		if(invitationCode.getCreatedDate()==null) {
			invitationCode.setUserId(u.getUserId());
			try {
				invitationCode.setInvitationCode("UPTO"+u.getUsername()+secureCodeUtils.generateInvitationSuffix());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				invitationCode.setInvitationCode("UPTO"+u.getUsername()+u.getId().substring(5,8));

			}
			invitationCode.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			invitationCode.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			invitationCode = invitationRepository.save(invitationCode);
		}
		
		
		Map<String,Object> resp = mapper.convertValue(invitationCode, Map.class);
		return resp;
	}
}
