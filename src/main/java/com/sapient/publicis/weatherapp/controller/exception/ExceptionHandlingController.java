package com.sapient.publicis.weatherapp.controller.exception;


import com.sapient.publicis.weatherapp.exception.WeatherServiceException;
import com.sapient.publicis.weatherapp.model.in.WeatherProcessingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

	private ResponseEntity<WeatherProcessingResponse> createResponseEntity(final HttpStatus status,
																		   final String message) {
		final List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setAccept(acceptableMediaTypes);
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(new WeatherProcessingResponse(message), responseHeaders, status);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<WeatherProcessingResponse> generic(final MethodArgumentNotValidException e) {
		log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), e);
		return createResponseEntity(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<WeatherProcessingResponse> generic(final Exception e) {
		log.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e);
		return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	@ExceptionHandler(WeatherServiceException.class)
	public ResponseEntity<WeatherProcessingResponse> weather(final WeatherServiceException e) {
		log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), e);
		return createResponseEntity(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
	}
}