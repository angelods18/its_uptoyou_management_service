package it.itsuptoyou.exceptions;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

public class CustomException extends Exception{

	private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
	
    private final List<String> errorCodes = new ArrayList<>();

	protected CustomException() {
		// TODO Auto-generated constructor stub
	}
	
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorPayload getPayload() {
        return new ErrorPayload(errorCodes);
    }

    protected CustomException withHttpStatus(final HttpStatus httpStatus) {
        Assert.notNull(httpStatus, "null httpStatus in exception");

        this.httpStatus = httpStatus;
        return this;
    }

    protected CustomException withErrorCodes(final String... errorCodes) {
        return withErrorCodes(asList(errorCodes));
    }

    protected CustomException withErrorCodes(final Collection<String> errorCodes) {
        Assert.notEmpty(errorCodes, "empty errorCodes in exception");

        this.errorCodes.addAll(ErrorCodes.normalize(errorCodes));
        return this;
    }

}
