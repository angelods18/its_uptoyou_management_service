package it.itsuptoyou.models.requests;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import it.itsuptoyou.models.Profile;
import lombok.Data;

@Data
public class UpdateProfileRequest {
	
	@NotNull
	private Long userId;
	@NotNull
	private Long version;
	@Valid
	@NotNull
	private Profile profile;
}
