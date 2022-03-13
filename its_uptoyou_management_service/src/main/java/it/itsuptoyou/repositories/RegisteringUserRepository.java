package it.itsuptoyou.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;

import it.itsuptoyou.collections.RegisteringUser;

@Repository
public interface RegisteringUserRepository extends MongoRepository<RegisteringUser, String>{
	
	Optional<RegisteringUser> findBySecureCode(String secureCode);
}
