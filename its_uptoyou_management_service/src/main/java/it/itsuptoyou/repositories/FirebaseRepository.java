package it.itsuptoyou.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.collections.FirebaseAccount;

public interface FirebaseRepository extends MongoRepository<FirebaseAccount, String>{

	Optional<FirebaseAccount> findByUserId(long userId);
	
	Optional<FirebaseAccount> findByAccessToken(String accessToken);
}
