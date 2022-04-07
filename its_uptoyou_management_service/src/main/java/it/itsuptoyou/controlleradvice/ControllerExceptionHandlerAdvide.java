package it.itsuptoyou.controlleradvice;

import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.AccessDeniedException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import it.itsuptoyou.exceptions.CustomException;
import it.itsuptoyou.exceptions.ErrorCodes;
import it.itsuptoyou.exceptions.ErrorPayload;
import it.itsuptoyou.validation.OptionalValidatorCollection;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class ControllerExceptionHandlerAdvide extends ResponseEntityExceptionHandler {

	@Autowired
	private OptionalValidatorCollection optionalValidators;
	
	@InitBinder
	public void initBinder(final WebDataBinder dataBinder) {
		dataBinder.addValidators(optionalValidators);
	}
		
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
			HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		// TODO Auto-generated method stub
		return new ResponseEntity<>(
				new ErrorPayload("validation.required.path."+ ex.getParameter().getParameterName()), status);
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		return new ResponseEntity<>(
				new ErrorPayload("validation.required."+ ex.getParameterName()), status);
	}
	
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		// TODO Auto-generated method stub
		return new ResponseEntity<>(ErrorCodes.validation("?", ex.getBindingResult()), status);
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		if(ex instanceof MethodArgumentTypeMismatchException) {
			final MethodArgumentTypeMismatchException e = (MethodArgumentTypeMismatchException) ex;
			
			final Throwable rootCause = e.getRootCause();
			log.trace("root " + rootCause);
			final MethodParameter parameter = e.getParameter();
			log.trace("parameter " + parameter);
			
			return new ResponseEntity<>(ErrorCodes.validation(rootCause, parameter.getParameterName()), status);
		}
		
		return super.handleTypeMismatch(ex, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		final Throwable cause = ex.getCause();
		
		if(cause instanceof MismatchedInputException) {
			log.error("input mismatch", cause);
			return new ResponseEntity<>(ErrorCodes.validation(cause, "json"), status);
		}
		log.error("input not readable", ex);
		return super.handleHttpMessageNotReadable(ex, headers, status, request);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorPayload> handleUnauthorizedError(final AccessDeniedException e) {
		log.error("Application error: {}", e.getStackTrace()[0]);
		log.debug("Full exception", e);
		return new ResponseEntity<>(new ErrorPayload("service.forbidden"), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorPayload> handleApplicationError(final CustomException e) {
		log.error("Application error: {}", e.getStackTrace()[0]);
		log.debug("Full exception", e);
		return new ResponseEntity<>( e.getPayload(), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorPayload> handleInternalError(final Exception e) {
		log.error("Application error: {}", e.getStackTrace()[0]);
		log.debug("Unexpected error", e);
		return new ResponseEntity<>(new ErrorPayload("server.internal"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UndeclaredThrowableException.class)
	public ResponseEntity<ErrorPayload> handleExceptionInAspect(final UndeclaredThrowableException e) {
		log.warn("Rethrowing aspect error");
		final Exception cause = (Exception) e.getCause();
		
		if (cause instanceof CustomException) {
			return handleApplicationError((CustomException) cause);
		}
		return handleInternalError(cause);
		
	}
	
	
}
