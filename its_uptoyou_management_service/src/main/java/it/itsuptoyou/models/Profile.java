package it.itsuptoyou.models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Profile {

	private String name;
	private String surname;
	private LocalDate birthdate;
	private Location address;
	private String gender;
	private String school;
	private String job;
}
