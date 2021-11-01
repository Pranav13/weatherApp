package com.sapient.publicis.weatherapp.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sys {
	@JsonProperty("pod")
	private String pod;
}
