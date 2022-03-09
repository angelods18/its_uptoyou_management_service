package it.itsuptoyou.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@GetMapping(value="/public/ping")
	public String ping() {
		return "PONG";
	}
	
	@PostMapping(value="/public/register")
	public ResponseEntity<?> registerUser(){
		
		return ResponseEntity.ok("utente creato con successo");
	}
}
