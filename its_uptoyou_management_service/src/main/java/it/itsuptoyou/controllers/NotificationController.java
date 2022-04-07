package it.itsuptoyou.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

import it.itsuptoyou.exceptions.NotFoundException;
import it.itsuptoyou.models.NoteFirebase;
import it.itsuptoyou.service.FirebaseMessagingService;

@RestController
public class NotificationController {
	
	@Autowired
	private FirebaseMessagingService firebaseService;

	@PostMapping(value="protected/send-notification")
	public ResponseEntity<?> sendNotification(@RequestBody Map<String,Object> request) throws FirebaseMessagingException {
		String result = firebaseService.sendNotification(request.get("note"), request.get("token").toString());
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(value="protected/save-token")
	public ResponseEntity<?> saveToken(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) throws NotFoundException {
		//TODO setup creation of firebaseToken
		String token = requestBody.get("token").toString();
		firebaseService.saveToken(request.getHeader("username"), requestBody);
		return ResponseEntity.ok(token);
	}
}
