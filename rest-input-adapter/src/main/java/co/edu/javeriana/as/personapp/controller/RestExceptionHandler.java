package co.edu.javeriana.as.personapp.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistsException;

@RestControllerAdvice
public class RestExceptionHandler {
	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(NoExistException exception) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleInvalidInput(RuntimeException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException exception) {
		return error(HttpStatus.CONFLICT, "The operation conflicts with existing related data");
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<Map<String, String>> handleAlreadyExists(AlreadyExistsException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage());
	}

	private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
		Map<String, String> body = new LinkedHashMap<>();
		body.put("status", status.getReasonPhrase());
		body.put("message", message);
		return ResponseEntity.status(status).body(body);
	}
}
