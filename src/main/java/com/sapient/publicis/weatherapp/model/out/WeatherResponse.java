package com.sapient.publicis.weatherapp.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class WeatherResponse {

	@JsonProperty("cod")
	private String cod;
	@JsonProperty("message")
	private float message;
	@JsonProperty("cnt")
	private int cnt;
	@JsonProperty("list")
	private List<ListData> list;
	@JsonProperty("city")
	private City city;

}