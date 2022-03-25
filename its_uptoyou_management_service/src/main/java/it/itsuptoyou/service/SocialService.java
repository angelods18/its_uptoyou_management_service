package it.itsuptoyou.service;

import java.util.Map;

import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.exceptions.PreconditionFailedException;

public interface SocialService {

	Map<String,Object> generateInvitationCode(String username) throws NotFoundException;
	
	Map<String, Object> inviteFriend(String username, Map<String, Object> request) throws NotFoundException;
	
	Map<String, Object> answerInvitation(String username, Map<String,Object> request) throws NotFoundException;
	
	Map<String, Object> getPendingInvitation(String username) throws NotFoundException;
	
	Map<String, Object> getFriendList(String username) throws NotFoundException;
	
	Map<String, Object> createTeam(String username, Map<String,Object> request) throws NotFoundException;
	
	Boolean requestJoiningTeam(String username, Map<String,Object> request) throws NotFoundException;
	
	Boolean answerTeamInvitationRequest(String username, Map<String,Object> request) throws NotFoundException;
	
	Boolean answerTeamJoinRequest(String username, Map<String,Object> request) throws NotFoundException, PreconditionFailedException;
}
