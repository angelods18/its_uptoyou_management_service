package it.itsuptoyou.models;

import lombok.Data;

@Data
public class ServiceResponse {

	private boolean status;
	private String message;
	private String exception;
	private Object body;
}
