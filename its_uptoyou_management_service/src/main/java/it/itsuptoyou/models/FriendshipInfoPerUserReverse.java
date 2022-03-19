package it.itsuptoyou.models;

import java.util.List;

import it.itsuptoyou.collections.User;
import it.itsuptoyou.collections.Friendship.FriendshipStatus;

public class FriendshipInfoPerUserReverse {

	private long userB;
	private List<User> userA;
	private FriendshipStatus status;
	
	public User removePrivateInfoFromUserB() {
		
		userA.parallelStream().forEach((user) -> {
			user = user.protectPrivateInfo(user);
		});
		return userA.get(0);
	}
}
