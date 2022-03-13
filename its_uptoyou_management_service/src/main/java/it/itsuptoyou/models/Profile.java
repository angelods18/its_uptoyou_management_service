package it.itsuptoyou.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Profile {

	private String name;
	private String surname;
	private LocalDateTime birthdate;
	private String gender;
	private String school;
	private String job;
}
