
package com.sapient.publicis.weatherapp.service;


import com.sapient.publicis.weatherapp.model.in.WeatherProcessingRequest;
import com.sapient.publicis.weatherapp.model.in.WeatherProcessingResponse;


public interface WeatherService {

	WeatherProcessingResponse process(WeatherProcessingRequest request);

}
