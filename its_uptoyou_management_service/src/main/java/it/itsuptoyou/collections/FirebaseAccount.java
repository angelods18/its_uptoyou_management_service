package it.itsuptoyou.collections;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "firebaseAccount")
@Data
public class FirebaseAccount extends EntityAbstract{

	private long userId;
	private String accessToken;
	private List<String> firebaseToken;
}
