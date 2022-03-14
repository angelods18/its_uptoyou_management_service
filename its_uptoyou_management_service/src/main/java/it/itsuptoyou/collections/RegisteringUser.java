package it.itsuptoyou.collections;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "registeringUsers")
@Data
public class RegisteringUser extends EntityAbstract{

	@NotNull
	@NotBlank
	private String username;
	
	@Email
	private String email;
		
	@NotBlank
	@NotNull
	private String password;
	
	private String secureCode;
}
