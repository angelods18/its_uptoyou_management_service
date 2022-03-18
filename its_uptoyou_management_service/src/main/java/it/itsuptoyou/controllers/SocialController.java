package it.itsuptoyou.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping(value="/protected/invitation-code")
	public ResponseEntity<?> getInvitationCode(HttpServletRequest request,Map<String,Object> requestBody) throws NotFoundException{
		log.info("request from user: " + request.getHeader("username"));
		Map<String, Object> resp = socialService.generateInvitationCode(request.getHeader("username"));
		return ResponseEntity.ok(resp);
	}
}
