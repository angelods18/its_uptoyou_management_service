package it.itsuptoyou.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import it.itsuptoyou.collections.Friendship;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.collections.Friendship.FriendshipStatus;
import it.itsuptoyou.models.FriendshipInfoPerUser;
import it.itsuptoyou.models.FriendshipInfoPerUserReverse;

@Repository
public class SocialDal {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public SocialDal(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public Map<String,Object> getPendingInvitationUser(User user) {
		
		Map<String,Object> resp = new HashMap<>();
		LookupOperation lookupOperationA = LookupOperation.newLookup()
				.from(mongoTemplate.getCollectionName(User.class)).localField("userB")
				.foreignField("userId").as("userB");
		LookupOperation lookupOperationB = LookupOperation.newLookup()
				.from(mongoTemplate.getCollectionName(User.class)).localField("userA")
				.foreignField("userId").as("userA");
		Aggregation aggregationA = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userA").is(user.getUserId()).and("status").is(FriendshipStatus.PENDING.name())),
				lookupOperationA);
		Aggregation aggregationB = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userB").is(user.getUserId()).and("status").is(FriendshipStatus.PENDING.name())),
				lookupOperationB);
		List<FriendshipInfoPerUser> pendingListByMe = mongoTemplate.aggregate(aggregationA, mongoTemplate.getCollectionName(Friendship.class),
				FriendshipInfoPerUser.class).getMappedResults();
		List<FriendshipInfoPerUserReverse> pendingListToMe = mongoTemplate.aggregate(aggregationB, mongoTemplate.getCollectionName(Friendship.class),
				FriendshipInfoPerUserReverse.class).getMappedResults();
		List<User> userInvitedByMe = new ArrayList<>();
		List<User> userInvitingMe = new ArrayList<>();
		pendingListByMe.parallelStream().forEach((fl) -> {
			userInvitedByMe.add(fl.removePrivateInfoFromUserB());
		});
		pendingListToMe.parallelStream().forEach((fl) -> {
			userInvitingMe.add(fl.removePrivateInfoFromUserB());
		});
		resp.put("sendByMeInvitation", userInvitedByMe);
		resp.put("sendToMeInvitation", userInvitingMe);
		
		return resp;
	}
	
	public List<User> getFriendList(User user){
		LookupOperation lookupOperation = LookupOperation.newLookup()
				.from(mongoTemplate.getCollectionName(User.class)).localField("userB")
				.foreignField("userId").as("userB");
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userA").is(user.getUserId()).and("status").is(FriendshipStatus.ACCEPTED.name())),
				lookupOperation);
		List<FriendshipInfoPerUser> friendList = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Friendship.class),
				FriendshipInfoPerUser.class).getMappedResults();
		List<User> friends = new ArrayList<>();
		friendList.parallelStream().forEach((fl) ->{
			friends.add(fl.removePrivateInfoFromUserB());
		});
		return friends;
	}
}
