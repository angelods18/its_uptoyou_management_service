package it.itsuptoyou.collections;

import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "friendship-interactions")
@Data
public class Friendship extends EntityAbstract{
	
	private long userA;
	private long userB;
	private FriendshipStatus status;
	
	public enum FriendshipStatus{
		PENDING, ACCEPTED, REFUSED
	}
}
