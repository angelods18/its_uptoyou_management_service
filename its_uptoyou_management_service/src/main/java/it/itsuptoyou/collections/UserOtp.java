package it.itsuptoyou.collections;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "rescue-code")
@Data
public class UserOtp extends EntityAbstract{
	
	@Indexed(unique = true)
	private long userId;
	
	private String otp;
	
}
