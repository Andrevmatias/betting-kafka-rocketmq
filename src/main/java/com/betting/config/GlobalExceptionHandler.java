package com.betting.config;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<Object> handleNotFound(NoSuchElementException exception){
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler({ IllegalStateException.class })
	public ResponseEntity<Object> handleIllegalState(IllegalStateException exception){
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleException(Exception exception){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
