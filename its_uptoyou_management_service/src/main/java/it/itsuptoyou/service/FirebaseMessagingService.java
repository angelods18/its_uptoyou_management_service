package it.itsuptoyou.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import it.itsuptoyou.collections.FirebaseAccount;
import it.itsuptoyou.collections.User;
import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.models.NoteFirebase;
import it.itsuptoyou.repositories.FirebaseRepository;
import it.itsuptoyou.repositories.UserRepository;

@Service
public class FirebaseMessagingService {

	@Autowired
	private final FirebaseMessaging firebaseMessaging;
	
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final FirebaseRepository firebaseRepository;
	
	public FirebaseMessagingService(FirebaseMessaging firebaseMessaging,
			UserRepository userRepository,
			FirebaseRepository firebaseRepository) {
		this.firebaseMessaging=firebaseMessaging;
		this.userRepository = userRepository;
		this.firebaseRepository=firebaseRepository;
	}
	
    public String sendNotification(Object noteMap, String token) throws FirebaseMessagingException {

    	ObjectMapper mapper = new ObjectMapper();
    	NoteFirebase note = mapper.convertValue(noteMap, NoteFirebase.class);
        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();

        return firebaseMessaging.send(message);
    }
    
    public void saveToken(String username, Map<String, Object> request) throws NotFoundException {
    	
    	User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("user"));
    	Optional<FirebaseAccount> fireOpt = firebaseRepository.findByUserId(user.getUserId());
    	FirebaseAccount fa = new FirebaseAccount();
    	fa.setFirebaseToken(new ArrayList<>());
    	if(fireOpt.isPresent()) {
    		fa=fireOpt.get();
    	}
    	fa.getFirebaseToken().add(request.get("token").toString());
    	firebaseRepository.save(fa);
    }
}
