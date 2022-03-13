package it.itsuptoyou.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.itsuptoyou.collections.User;

@Repository
public interface UserRepository extends MongoRepository<User,String>{

}
