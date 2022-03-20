package it.itsuptoyou.serviceimpls;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.itsuptoyou.collections.Friendship;
import it.itsuptoyou.collections.InvitationCode;
import it.itsuptoyou.collections.Team;
import it.itsuptoyou.collections.Team.Member;
import it.itsuptoyou.collections.Team.TeamRole;
import it.itsuptoyou.collections.Team.TeamStatus;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.collections.Friendship.FriendshipStatus;
import it.itsuptoyou.dal.SocialDal;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.models.FriendshipInfoPerUser;
import it.itsuptoyou.repositories.FriendsRepository;
import it.itsuptoyou.repositories.InvitationRepository;
import it.itsuptoyou.repositories.TeamRepository;
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
	private FriendsRepository friendsRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private SecureCodeUtils secureCodeUtils;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private SocialDal socialDal;
	
	@Autowired
	private CustomSequenceService customSequenceService;
	
	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("YYYY-MM-dd"));
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
	
	@Override
	public Map<String, Object> generateInvitationCode(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		
		
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
		
		
		Map<String,Object> resp = getMapper().convertValue(invitationCode, Map.class);
		return resp;
	}
	
	@Override
	public Map<String, Object> inviteFriend(String username, Map<String, Object> request) throws NotFoundException {
		// TODO Auto-generated method stub
		User userA = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		User userB = userRepository.findByUsername(request.get("username").toString()).orElseThrow(() -> new NotFoundException("user"));
		Friendship friendship = new Friendship();
		friendship.setUserA(userA.getUserId());
		friendship.setUserB(userB.getUserId());
		friendship.setStatus(FriendshipStatus.PENDING);
		friendship.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		friendship.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		friendship = friendsRepository.save(friendship);
		
		//TODO implementare notifiche 
		
		return getMapper().convertValue(friendship, Map.class);
	}
	
	@Override
	public Map<String, Object> answerInvitation(String username, Map<String, Object> request) throws NotFoundException {
		// TODO Auto-generated method stub
		User userB = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
		Friendship friendship = friendsRepository.findByUserAAndUserB( Long.parseLong(request.get("user").toString()), userB.getUserId())
				.orElseThrow(()-> new NotFoundException("user"));
		Friendship reverseFriendship = new Friendship();
		if(request.get("answer").toString().equals(FriendshipStatus.ACCEPTED.name())) {
			friendship.setStatus(FriendshipStatus.ACCEPTED);
			friendship.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			friendship = friendsRepository.save(friendship);
			reverseFriendship.setUserA(userB.getUserId());
			reverseFriendship.setUserB(friendship.getUserA());
			reverseFriendship.setStatus(FriendshipStatus.ACCEPTED);
			reverseFriendship.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			reverseFriendship.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			reverseFriendship = friendsRepository.save(reverseFriendship);
		}
		if(request.get("answer").toString().equals(FriendshipStatus.REFUSED.name())) {
			friendship.setStatus(FriendshipStatus.REFUSED);
		}
		return getMapper().convertValue(reverseFriendship, Map.class);
	}
	
	@Override
	public Map<String, Object> getPendingInvitation(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("user"));

		Map<String,Object> resp = socialDal.getPendingInvitationUser(user);
		return resp;
	}
	
	@Override
	public Map<String, Object> getFriendList(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("user"));
		List<User> friends = socialDal.getFriendList(user);
		//List<User> friends = friendList.removePrivateInfoFromUserB();
		Map<String,Object> resp = new HashMap<>();
		resp.put("friends", friends);
		return resp;
	}
	
	@Override
	public Map<String, Object> createTeam(String username, Map<String, Object> request) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("user"));
		final Team t = new Team();
		t.setMembers(new ArrayList<>());
		t.setCreatorId(user.getUserId());
		Member member = t.new Member();
		member.setUserId(user.getUserId());
		member.getRole().add(TeamRole.FOUNDER);
		member.getRole().add(TeamRole.ADMIN);
		member.setStatus(TeamStatus.ACCEPTED);
		t.getMembers().add(member);
		List<Integer> invited = getMapper().convertValue(request.get("invitedFriends"), List.class);
		invited.parallelStream().forEach((i)-> {
			Long id = Long.parseLong(i.toString());
			if(userRepository.findByUserId(id).isPresent() &&
				friendsRepository.findByUserAAndUserB(id, user.getUserId()).isPresent() &&
				friendsRepository.findByUserAAndUserB(id, user.getUserId()).get().getStatus().equals(FriendshipStatus.ACCEPTED))
			{				
				Member m = t.new Member();
				m.getRole().add(TeamRole.BEGINNER);
				m.setUserId(id);
				t.getMembers().add(m);
			}
		});
		t.setTeamName(request.get("teamName").toString());
		t.setTeamId(customSequenceService.generateSequence("customSequences_team", "team"));
		t.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		t.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		
		return getMapper().convertValue(teamRepository.save(t), Map.class);
	}
	
	@Override
	public Boolean answerTeamInvitationRequest(String username, Map<String,Object> request) throws NotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("user"));
		Team team = teamRepository.findByTeamId(Long.parseLong(request.get("teamId").toString())).orElseThrow(()->new NotFoundException("team"));
		Member member = team.getMembers().stream().filter(m -> m.getUserId()==user.getUserId()).findFirst().orElseThrow(() -> new NotFoundException("invitation"));
		int index = team.getMembers().indexOf(member);
		if(request.get("answer").toString().equals(TeamStatus.ACCEPTED.name())) {
			member.setStatus(TeamStatus.ACCEPTED);
			team.getMembers().set(index, member);
			teamRepository.save(team);
			return true;
		}else {
			team.getMembers().remove(member);
			teamRepository.save(team);
			return false;
		}
	}
}
