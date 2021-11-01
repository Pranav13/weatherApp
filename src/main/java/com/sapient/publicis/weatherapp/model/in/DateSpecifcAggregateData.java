package com.sapient.publicis.weatherapp.model.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sapient.publicis.weatherapp.model.out.ListData;
import com.sapient.publicis.weatherapp.model.out.MainDetails;
import com.sapient.publicis.weatherapp.model.out.Weather;
import com.sapient.publicis.weatherapp.util.WeatherServiceUtility;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "dateValue", "minTemp", "maxTemp", "warning" })
public class DateSpecifcAggregateData implements  DateSpecifcAggregate{
	public static final Comparator<DateSpecifcAggregateData> DATE_BASE_COMPARATOR = (o1, o2) -> o1.dateValue
			.compareTo(o2.dateValue);

	@JsonIgnore
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private double minTemp = 1000;

	@JsonIgnore
	//@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private double maxTemp = 0;

	private final String dateValue;

	@JsonIgnore
	private boolean rainPredicted;
	
	private String warning;

	public DateSpecifcAggregateData(final String dateOnly) {
		this.dateValue = dateOnly;
	}

	public void accept(final ListData value) {
		final List<Weather> weather = value.getWeather();
		if (!CollectionUtils.isEmpty(weather)) {
			this.rainPredicted = this.rainPredicted
					|| weather.stream().anyMatch(t -> "Rain".equalsIgnoreCase(t.getMain())
							|| (t.getDescription() != null && t.getDescription().contains("rain")));
		}

		final MainDetails mainInfo = value.getMain();
		minTemp = Math.min(minTemp, mainInfo.getTempMin());
		maxTemp = Math.max(maxTemp, mainInfo.getTempMax());
	}

	public void reconcile(final DateSpecifcAggregateData other) {
		minTemp = Math.min(minTemp, other.minTemp);
		maxTemp = Math.max(maxTemp, other.maxTemp);
	}


	// \u00B0 Unicode of degree
	public String getMinimumTemperature() {
		return String.format("%.3f (%.3f \u2103)", minTemp, WeatherServiceUtility.kelvinToDegreeCelcius(minTemp));
	}

	public String getMaximumTemperature() {
		return String.format("%.3f (%.3f \u2103)", maxTemp, WeatherServiceUtility.kelvinToDegreeCelcius(maxTemp));
	}

}