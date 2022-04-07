package it.itsuptoyou.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.itsuptoyou.collections.RegisteringUser;

@Repository
public interface RegisteringUserRepository extends MongoRepository<RegisteringUser, String>{
	
	Optional<RegisteringUser> findBySecureCode(String secureCode);
	
	List<RegisteringUser> findByEmailOrUsername(String email, String username);
}
