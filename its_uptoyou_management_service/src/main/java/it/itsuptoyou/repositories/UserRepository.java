package it.itsuptoyou.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.itsuptoyou.collections.User;

@Repository
public interface UserRepository extends MongoRepository<User,String>{

	List<User> findByEmailOrUsername(String email, String username);
	
	Optional<User> findByUserId(long userId);
}
