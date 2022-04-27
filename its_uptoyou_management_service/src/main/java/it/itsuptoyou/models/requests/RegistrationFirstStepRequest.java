package it.itsuptoyou.models.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegistrationFirstStepRequest {

	@NotNull
	@Size(min=3, max=20)
	private String username;
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	@Size(min=4, max=16)
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#-_$%^&+=])(?=\\S+$).{4,}$")
	private String password;
	
	private String invitationCode;
}
