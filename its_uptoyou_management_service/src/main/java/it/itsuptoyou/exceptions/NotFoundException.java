package it.itsuptoyou.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException{

	public NotFoundException(final String resourceCode) {
		// TODO Auto-generated constructor stub
		withHttpStatus(HttpStatus.NOT_FOUND).withErrorCodes("service.notFound."+resourceCode);
	}
	
	public NotFoundException(final Class<?> resourceType) {
        this(resourceType.getSimpleName());
    }
}
