package it.itsuptoyou.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.models.InvitationCode;

public interface InvitationRepository extends MongoRepository<InvitationCode,String>{

	Optional<InvitationCode> findByUserId(long userId);
	
	Optional<InvitationCode> findByInvitationCode(String invitationCode);
}
