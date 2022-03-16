package it.itsuptoyou.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.collections.UserOtp;

public interface UserOtpRepository extends MongoRepository<UserOtp, String>{
	
	Optional<UserOtp> findByUserId(long userId);
	
	Optional<UserOtp> findByOtp(String otp);
}
