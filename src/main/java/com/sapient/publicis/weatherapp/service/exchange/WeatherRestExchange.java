package com.sapient.publicis.weatherapp.service.exchange;

import java.util.Map;

public interface WeatherRestExchange {

	String BASE_URL = "https://api.openweathermap.org/data/{DEST_API_VERSION}/forecast?q={cityList}&&appid={APP_KEY}";

	<T> T retreive(Class<T> type, Map<String, ?> map);

}
