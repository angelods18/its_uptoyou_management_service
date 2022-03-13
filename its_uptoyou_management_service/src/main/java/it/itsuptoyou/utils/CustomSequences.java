package it.itsuptoyou.utils;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "customSequences")
@Data
public class CustomSequences {

	@Id
	private String id;
	private String type;
	private int seq;
}
