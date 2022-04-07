package it.itsuptoyou.validation;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OptionalValidatorCollection implements Validator{

	private static final Map<Class<?>, List<Validator>> VALIDATORS = new ConcurrentHashMap<>();
	
	
	@OptionalValidator
	private static List<Validator> validators;
	
	void registerValidator(final Validator validator) {
		validators.add(validator);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		VALIDATORS.computeIfAbsent(clazz, c -> validators.stream().filter(v -> v.supports(c)).collect(toList()));
		return true;
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		VALIDATORS.get(target.getClass()).forEach(v -> v.validate(target, errors));
	}
}
