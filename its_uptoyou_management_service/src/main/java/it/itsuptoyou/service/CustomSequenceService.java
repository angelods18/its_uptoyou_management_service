package it.itsuptoyou.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import it.itsuptoyou.utils.CustomSequences;

@Service
public class CustomSequenceService {
	
	@Autowired
 	private MongoOperations mongo;

	public long generateSequence(String seqName, String type) {
	    CustomSequences counter = mongo.findAndModify(query(where("_id").is(seqName).and("type").is(type)),
	      new Update().inc("seq",1), options().returnNew(true).upsert(true),
	      CustomSequences.class);
	    return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}
}
