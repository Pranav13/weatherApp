package com.sapient.publicis.weatherapp.service.impl;

import com.google.common.collect.ImmutableMap;
import com.sapient.publicis.weatherapp.model.in.*;
import com.sapient.publicis.weatherapp.model.out.ListData;
import com.sapient.publicis.weatherapp.model.out.WeatherResponse;
import com.sapient.publicis.weatherapp.service.DateWiseSummaryStatistics;
import com.sapient.publicis.weatherapp.service.WeatherService;
import com.sapient.publicis.weatherapp.service.exchange.WeatherRestExchange;
import com.sapient.publicis.weatherapp.util.WeatherServiceConstants;
import com.sapient.publicis.weatherapp.util.WeatherServiceUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

	private static final ThreadLocal<Locale> THREAD_LOCAL_LOCALE = new ThreadLocal<>();

	private final WeatherRestExchange restTemplateHandler;

	private final Collector<ListData, ?, Map<String, DateSpecifcAggregateData2>> groupByDate;
	
	@Autowired
	private MessageSource messages;
	
	@Override
	@Cacheable(value = "weatherCache", key = "#weatherProcessingRequest.locations.concat('_')"
			+ ".concat(#weatherProcessingRequest.maxDate.getTime)"
			+ ".concat(#weatherProcessingRequest.minDate.getTime)")
	public WeatherProcessingResponse process(final WeatherProcessingRequest weatherProcessingRequest) {
		log.error("not returning from cache");
		final WeatherResponse body = restTemplateHandler.retreive(WeatherResponse.class,
				ImmutableMap.of("cityList", weatherProcessingRequest.getLocations()));

		final Supplier<DateWiseSummaryStatistics> supplier = DateWiseSummaryStatistics::new;
		final BiConsumer<DateWiseSummaryStatistics, ? super ListData> accumulator = DateWiseSummaryStatistics::accept;
		final BiConsumer<DateWiseSummaryStatistics, DateWiseSummaryStatistics> combiner = DateWiseSummaryStatistics::combine;
		final DateWiseSummaryStatistics dateWiseSummaryStatistics = body.getList().stream()
				.filter(a -> a.isInExpectedDuration(weatherProcessingRequest)).collect(supplier, accumulator, combiner);

		final Collection<DateSpecifcAggregateData> dateWiseWeatherReport = dateWiseSummaryStatistics
				.getDateWiseWeatherReport();
		
		if (CollectionUtils.isEmpty(dateWiseWeatherReport)) {
			return new WeatherProcessingResponse(
					messages.getMessage(WeatherServiceConstants.WEATHER_SERVICE_CONSTANTS_NO_DATA, null,
							weatherProcessingRequest.getLocale()));
		}

		try {
			THREAD_LOCAL_LOCALE.set(weatherProcessingRequest.getLocale());
			final Supplier<Collection<DateSpecifcAggregateData>> collectionFactory = () -> new TreeSet<>(
					DateSpecifcAggregateData.DATE_BASE_COMPARATOR);
			final Collection<DateSpecifcAggregateData> data = dateWiseWeatherReport.stream().map(this::enrichWarning)
					.collect(Collectors.toCollection(collectionFactory));
			return new WeatherProcessingResponse(data);
		} finally {
			THREAD_LOCAL_LOCALE.remove();
		}
	}

	
	public WeatherServiceImpl(@Autowired final WeatherRestExchange restTemplateHandler) {
		this.restTemplateHandler = restTemplateHandler;

		final Collector<ListData, ?, DateSpecifcAggregateData2> downstream = new Collector<ListData, DateSpecifcAggregateData2, DateSpecifcAggregateData2>() {

			final BiConsumer<DateSpecifcAggregateData2, ListData> biConsumer = DateSpecifcAggregateData2::accept;
			final BinaryOperator<DateSpecifcAggregateData2> binaryOperator = DateSpecifcAggregateData2::reconcile;

			// final Function<DateSpecifcAggregateData2, DateSpecifcAggregateData2> function
			final UnaryOperator<DateSpecifcAggregateData2> function = t -> enrichWarning(t).finisher();

			@Override
			public Supplier<DateSpecifcAggregateData2> supplier() {
				return DateSpecifcAggregateData2::new;
			}

			@Override
			public BiConsumer<DateSpecifcAggregateData2, ListData> accumulator() {
				return biConsumer;
			}

			@Override
			public BinaryOperator<DateSpecifcAggregateData2> combiner() {
				return binaryOperator;
			}

			@Override
			public Function<DateSpecifcAggregateData2, DateSpecifcAggregateData2> finisher() {
				return function;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.emptySet();
			}

		};
		groupByDate = Collectors.groupingBy(ListData::getDateOnly, downstream);
	}
	
	public <T extends DateSpecifcAggregate> T enrichWarning(final T dateSpecifcAggregate) {
		if (WeatherServiceUtility.kelvinToDegreeCelcius(dateSpecifcAggregate.getMaxTemp()) > 40) {
			dateSpecifcAggregate.setWarning(messages.getMessage(WeatherServiceConstants.WEATHER_SERVICE_CONSTANTS_HEAT_WARNING,
					null, THREAD_LOCAL_LOCALE.get()));
		}

		if (dateSpecifcAggregate.isRainPredicted()) {
			dateSpecifcAggregate.setWarning(messages.getMessage(WeatherServiceConstants.WEATHER_SERVICE_CONSTANTS_RAIN_WARNING,
					null, THREAD_LOCAL_LOCALE.get()));
		}
		
		return dateSpecifcAggregate;
	}

}
