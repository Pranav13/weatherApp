
package com.sapient.publicis.weatherapp.util;

public class WeatherServiceUtility {

	private WeatherServiceUtility() {

	}

	public static double kelvinToDegreeCelcius(final double kelvin) {
		return kelvin - 273.15;
	}

}
