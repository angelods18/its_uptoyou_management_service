package it.itsuptoyou.service;

import java.util.Map;

import it.itsuptoyou.exceptions.NotFoundException;

public interface SocialService {

	Map<String,Object> generateInvitationCode(String username) throws NotFoundException;
}
