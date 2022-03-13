package it.itsuptoyou.collections;

import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collation = "registeringUsers")
@Data
public class RegisteringUser extends EntityAbstract{

	private String username;
	private String password;
	private String secureCode;
}
