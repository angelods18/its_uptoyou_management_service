package it.itsuptoyou.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

import it.itsuptoyou.models.NoteFirebase;
import it.itsuptoyou.service.FirebaseMessagingService;

@RestController
public class NotificationController {
	
	@Autowired
	private FirebaseMessagingService firebaseService;

	@PostMapping(value="protected/send-notification")
	public ResponseEntity<?> sendNotification(Map<String,Object> request) throws FirebaseMessagingException {
		String result = firebaseService.sendNotification((NoteFirebase) request.get("note"), request.get("token").toString());
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(value="protected/save-token")
	public ResponseEntity<?> saveToken(HttpServletRequest request) {
		//TODO setup creation of firebaseToken
		String token = "";
		return ResponseEntity.ok(token);
	}
}
