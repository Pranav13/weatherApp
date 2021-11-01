package com.sapient.publicis.weatherapp.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sapient.publicis.weatherapp.common.DateTxtDeserializer;
import com.sapient.publicis.weatherapp.common.TimeStampDeserializer;
import com.sapient.publicis.weatherapp.model.in.WeatherProcessingRequest;
import com.sapient.publicis.weatherapp.util.WeatherServiceConstants;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListData {

	private static final ThreadLocal<DateFormat> FORMAT = ThreadLocal
			.withInitial(() -> new SimpleDateFormat(WeatherServiceConstants.DATE_FORMAT_YYYY_MM_DD));

	static ThreadLocal<SimpleDateFormat> format1 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	@JsonDeserialize(using = TimeStampDeserializer.class)
	@JsonProperty("dt")
	private Date dt;
	//@Setter(AccessLevel.NONE)
	private String dateOnly;
	@JsonProperty("main")
	private MainDetails main;
	@JsonProperty("weather")
	private List<Weather> weather;
	@JsonProperty("clouds")
	private Clouds clouds;
	@JsonProperty("wind")
	private Wind wind;
	
	@JsonProperty("rain")
	private RainDetails rain;
	
	@JsonProperty("snow")
	private SnowDetails snow;
	
	@JsonProperty("sys")
	private Sys sys;

	@Setter(AccessLevel.NONE)
	@JsonProperty("dt_txt")
	private Date dtTxt;

	@JsonDeserialize(using = DateTxtDeserializer.class)
	public void setDtTxt(final Date dtTxt) {
		this.dtTxt = dtTxt;
		this.setDateOnly(dtTxt == null ? null : format1.get().format(dtTxt));
	}

	public boolean isInExpectedDuration(final WeatherProcessingRequest weatherProcessingRequest) {
		//return
		//		dtTxt.after(weatherProcessingRequest.getMinDate()) && dtTxt.before(weatherProcessingRequest.getMaxDate());
		return dtTxt.compareTo(weatherProcessingRequest.getMinDate()) >= 0
				&& dtTxt.compareTo(weatherProcessingRequest.getMaxDate()) < 0;
	}

}
