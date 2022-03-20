package it.itsuptoyou.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.collections.Team;

public interface TeamRepository extends MongoRepository<Team, String>{

	Optional<Team> findByTeamName(String teamName);
	
	Optional<Team> findByTeamId(long teamId);
}
