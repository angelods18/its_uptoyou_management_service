package it.itsuptoyou.collections;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.models.Profile;
import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Data
@Document(collection="users")
public class User extends EntityAbstract{
	
	@Indexed(unique=true)
	private Long userId;
	
	private boolean enabled = true;
	
	@Indexed(unique=true)
	private String username;
	private String password;
	
	private String email;
	
	private List<Authority> authorities;
	
	private Profile profile;
	
	public User protectPrivateInfo(User u) {
		u.setPassword(null);
		u.setAuthorities(new ArrayList<>());
		u.setEmail(null);
		return u;
	}
}
