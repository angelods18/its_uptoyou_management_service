package it.itsuptoyou.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.service.SocialService;
import it.itsuptoyou.service.UserService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class SocialController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SocialService socialService;
	
	/**
	 * 
	 * 
	 * @param request from gateway with header username
	 * @param requestBody null
	 * @return the invitation code saved in repository
	 * @throws NotFoundException
	 */
	@GetMapping(value="/protected/invitation-code")
	public ResponseEntity<?> getInvitationCode(HttpServletRequest request,Map<String,Object> requestBody) throws NotFoundException{
		log.info("request from user: " + request.getHeader("username"));
		Map<String, Object> resp = socialService.generateInvitationCode(request.getHeader("username"));
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * invite a user to be your friend
	 * @param request from gateway with header username
	 * @param requestBody the user that you want to invite
	 * @return friendship invitation pending
	 * @throws NotFoundException
	 */
	@PostMapping(value="/protected/invite-friend")
	public ResponseEntity<?> inviteFriend(HttpServletRequest request,@RequestBody Map<String,Object> requestBody) throws NotFoundException{
		Map<String,Object> resp = socialService.inviteFriend(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * answer a friend invitation ACCEPTED or REFUSED
	 * @param request
	 * @param requestBody username and answer ACCEPTED or REFUSED
	 * @return
	 * @throws NotFoundException
	 */
	@PostMapping(value="/protected/answer-invitation")
	public ResponseEntity<?> answerInvitation(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException{
		Map<String,Object> resp = socialService.answerInvitation(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * get the list of pending invitation both to me and by me
	 * @param request from gateway with header username
	 * @return list of pendingToMe and pendingByMe
	 * @throws NotFoundException
	 */
	@GetMapping(value="/protected/pending-invitation")
	public ResponseEntity<?> getPendingInvitation(HttpServletRequest request) throws NotFoundException{
		Map<String,Object> resp = socialService.getPendingInvitation(request.getHeader("username"));
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * get the list of all your confirmed friends
	 * @param request
	 * @return
	 * @throws NotFoundException
	 */
	@GetMapping(value="/protected/friends")
	public ResponseEntity<?> getFriendList(HttpServletRequest request) throws NotFoundException{
		Map<String,Object> resp = socialService.getFriendList(request.getHeader("username"));
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * create a team of which you are the founder -> invited friends go to pendingMembers
	 * @param request username from gateway header
	 * @param requestBody teamname and starting user invitation
	 * @return
	 * @throws NotFoundException
	 */
	@PostMapping(value="/protected/create-team")
	public ResponseEntity<?> createTeam(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException {
		Map<String,Object> resp = socialService.createTeam(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * ask the founder and the admin to be part of the team -> added to pendingMembers
	 * @param request
	 * @param requestBody
	 * @return
	 * @throws NotFoundException 
	 */
	@PatchMapping(value="/protected/team-join-request")
	public ResponseEntity<?> teamRequest(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException {
		//TODO when notify have been setup
		Boolean resp = socialService.requestJoiningTeam(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	/**
	 * answer invitation in a team -> if accepted from pendingMembers to members
	 * @param request
	 * @param requestBody
	 * @return
	 * @throws NotFoundException 
	 */
	@PatchMapping(value="/protected/answer-team-invitation")
	public ResponseEntity<?> answerTeamInvitationRequest(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException {
		Boolean resp = socialService.answerTeamInvitationRequest(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	@PatchMapping(value="/protected/answer-team-join-request")
	public ResponseEntity<?> answerTeamJoinRequest(HttpServletRequest request, @RequestBody Map<String,Object> requestBody) throws NotFoundException, PreconditionFailedException {
		Boolean resp = socialService.answerTeamJoinRequest(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(resp);
	}
	
	
}
