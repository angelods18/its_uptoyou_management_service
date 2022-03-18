package it.itsuptoyou.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.collections.Friendship;

public interface FriendsRepository extends MongoRepository<Friendship, String>{

	Optional<Friendship> findByUserAAndUserB(long userA, long UserB);
	
	List<Friendship> findByUserA(long userA);
	
	List<Friendship> findByUserB(long userB);
	
	List<Friendship> findByUserAOrUserB(long userA);
	
}
