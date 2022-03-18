package it.itsuptoyou.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.exceptions.NotFoundException;
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
	public ResponseEntity<?> inviteFriend(HttpServletRequest request, Map<String,Object> requestBody) throws NotFoundException{
		Map<String,Object> resp = new HashMap<>();
		return ResponseEntity.ok(resp);
	}
}