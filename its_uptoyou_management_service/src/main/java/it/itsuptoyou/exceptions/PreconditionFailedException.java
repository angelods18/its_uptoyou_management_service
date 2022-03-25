package it.itsuptoyou.exceptions;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends CustomException{

	public PreconditionFailedException(final String resourceCode) {
		// TODO Auto-generated constructor stub
		withHttpStatus(HttpStatus.CONFLICT).withErrorCodes("preconditionFailed."+resourceCode);
	}
	
	public PreconditionFailedException(final String resourceCode, String reason) {
		// TODO Auto-generated constructor stub
		withHttpStatus(HttpStatus.CONFLICT).withErrorCodes("preconditionFailed."+resourceCode+"."+reason);
	}
	
	public PreconditionFailedException(final Class<?> resourceType) {
        this(resourceType.getSimpleName());
    }
}
