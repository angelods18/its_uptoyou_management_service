package it.itsuptoyou.models;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Profile {

	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate birthdate;
	private Location address;
	private String gender;
	private String school;
	private String job;
}
