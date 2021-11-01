package com.sapient.publicis.weatherapp.controller;


import com.sapient.publicis.weatherapp.exception.WeatherServiceException;
import com.sapient.publicis.weatherapp.model.in.WeatherProcessingRequest;
import com.sapient.publicis.weatherapp.model.in.WeatherProcessingResponse;
import com.sapient.publicis.weatherapp.service.WeatherService;
import com.sapient.publicis.weatherapp.util.WeatherServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@RestController
public class WeatherServiceController {
	private final WeatherService weatherService;

	public WeatherServiceController(@Autowired final WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@GetMapping("/weather/forecast")
	@Valid
	public ResponseEntity<WeatherProcessingResponse> getWeatherPredictionByLocation(
			@RequestParam(value = "city") final String location,
			@RequestParam(value = "minNDays", defaultValue = "1") @Min(1) final int minNDays,
			@RequestParam(value = "maxNDays", defaultValue = "3") @Max(5) final int maxNDays,
			@RequestParam(value = "thresholdDate", required = false) @DateTimeFormat(pattern = WeatherServiceConstants.DATE_FORMAT_YYYY_MM_DD) final Date thresholdDate,
			@RequestHeader(value = "Accept-Language", required = false) final String language) {

		if (minNDays > maxNDays) {
			throw new WeatherServiceException(String.format("Min date(%d) > Max date(%d)", minNDays, maxNDays));
		}
		final WeatherProcessingRequest request = new WeatherProcessingRequest(location, thresholdDate, minNDays,
				maxNDays, language);
		final WeatherProcessingResponse response = weatherService.process(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
