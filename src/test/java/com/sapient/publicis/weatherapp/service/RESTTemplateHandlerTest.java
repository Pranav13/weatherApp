package com.sapient.publicis.weatherapp.service;


import com.sapient.publicis.weatherapp.service.exchange.WeatherRestExchange;
import com.sapient.publicis.weatherapp.service.exchange.impl.RESTTemplateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RESTTemplateHandlerTest {

	private static final String MOCK_REST_RESPONSE = "Mocking";

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks // auto inject helloRepository
	private final WeatherRestExchange weatherRestExchange = new RESTTemplateHandler("a", "b");

	@BeforeEach
	void setMockOutput() {
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(), Mockito.anyMap()))
				.thenReturn(new ResponseEntity<>(MOCK_REST_RESPONSE, HttpStatus.OK));
	}

	@Test
	void testRetreive() {
		final String retreive = weatherRestExchange.retreive(String.class, Collections.emptyMap());
		assertThat(retreive).isEqualTo(MOCK_REST_RESPONSE);

	}

}
