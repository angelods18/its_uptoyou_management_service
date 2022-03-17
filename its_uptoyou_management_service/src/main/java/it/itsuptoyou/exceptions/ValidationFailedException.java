package it.itsuptoyou.exceptions;

import static java.util.stream.Stream.concat;

import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

@SuppressWarnings("serial")
public class ValidationFailedException extends CustomException {

    public ValidationFailedException(final String baseResource, final Errors errors) {
        withHttpStatus(HttpStatus.BAD_REQUEST).withErrorCodes(extractErrors(baseResource, errors));
    }

    public ValidationFailedException(final String baseResource, final String error) {
    	withHttpStatus(HttpStatus.BAD_REQUEST).withErrorCodes(extractErrors(baseResource, error));
    }
    
    @Deprecated
    public ValidationFailedException(final String... errorCodes) {
        withHttpStatus(HttpStatus.BAD_REQUEST).withErrorCodes(errorCodes);
    }

    private String[] extractErrors(final String baseResource, final Errors errors) {
        return concat(globalErrors(baseResource, errors), fieldErrors(baseResource, errors))
                .toArray(size -> new String[size]);
    }
    
    private String extractErrors(final String baseResource, final String errors) {
        return "validation."+errors+"."+baseResource;
    }

    private Stream<String> globalErrors(final String baseResource, final Errors errors) {
        return errors.getGlobalErrors().stream().map(ge -> "validation." + ge.getCode() + "." + baseResource);
    }

    private Stream<String> fieldErrors(final String baseResource, final Errors errors) {
        return errors.getFieldErrors().stream()
                .map(fe -> "validation." + fe.getCode() + "." + baseResource + "." + trashListIndices(fe.getField()));
    }

    private static String trashListIndices(final String fieldPath) {
        return fieldPath.replaceAll("\\[\\d+\\]", "");
    }

}

