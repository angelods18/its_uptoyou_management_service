package it.itsuptoyou.models;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "invitations")
@Data
public class InvitationCode extends EntityAbstract{
	
	@Indexed(unique=true)
	private long userId;
	private String invitationCode;
}
