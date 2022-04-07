package it.itsuptoyou.models;

import java.util.List;

import it.itsuptoyou.collections.Friendship.FriendshipStatus;
import it.itsuptoyou.collections.User;
import lombok.Data;

@Data
public class FriendshipInfoPerUser {
	
	private long userA;
	private List<User> userB;
	private FriendshipStatus status;
	
	public User removePrivateInfoFromUserB() {
		
		userB.parallelStream().forEach((user) -> {
			user = user.protectPrivateInfo(user);
		});
		return userB.get(0);
	}
}
