package com.betting.config;

import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.betting.exception.BrokerException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<Object> handleNotFound(NoSuchElementException exception) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(exception.getMessage());
	}

	@ExceptionHandler({ IllegalStateException.class })
	public ResponseEntity<Object> handleIllegalState(IllegalStateException exception) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(exception.getMessage());
	}

	@ExceptionHandler({ BrokerException.class })
	public ResponseEntity<Object> handleBrokerException(IllegalStateException exception) {
		return ResponseEntity
				.status(HttpStatus.BAD_GATEWAY)
				.body(exception.getMessage());
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleException(Exception exception) {
		log.error("Unexpected error.", exception);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
	}
}
