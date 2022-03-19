package it.itsuptoyou.configurations;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Component
public class MessagingConfiguration {

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		String fileName = "./config/firebase-messaging.json";
        FileInputStream fis = new FileInputStream(fileName);
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(fis);
		FirebaseOptions firebaseOptions = FirebaseOptions
				.builder()
				.setCredentials(googleCredentials)
				.build();
		try {
			FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "its-app-to-you-gaeta");
			return FirebaseMessaging.getInstance(app);
		}catch(Exception e) {
			return null;
		}
		
	}
}
