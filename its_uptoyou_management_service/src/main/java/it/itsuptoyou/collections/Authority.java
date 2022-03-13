package it.itsuptoyou.collections;

import org.springframework.data.annotation.Id;
import it.itsuptoyou.enums.AuthorityName;
import lombok.Data;

@Data
public class Authority {

	public  Authority() {}
	
	@Id
	private Long id;
	
	private AuthorityName name;
}
