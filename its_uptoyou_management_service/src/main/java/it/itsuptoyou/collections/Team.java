package it.itsuptoyou.collections;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection="teams")
@Data
public class Team extends EntityAbstract{
	
	@Indexed(unique=true)
	private long teamId;
	private String teamName;
	private long creatorId;
	private List<Member> members;
	private List<Member> pendingMembers;
	
	@Data
	public class Member{
		private long userId;
		private LocalDateTime joinDate;
		private List<TeamRole> role;
		private double points;
		private TeamStatus status;
		
		public Member() {
			joinDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
			points = 0;
			role= new ArrayList<>();
			status=TeamStatus.PENDING;
		}
	}
	
	public enum TeamRole{
		ADMIN, FOUNDER, SENTINEL, CADET, BEGINNER
	}
	
	public enum TeamStatus{
		PENDING, ACCEPTED
	}
}
