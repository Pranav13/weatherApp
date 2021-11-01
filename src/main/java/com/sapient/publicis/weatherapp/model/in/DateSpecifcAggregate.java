package com.sapient.publicis.weatherapp.model.in;

public interface DateSpecifcAggregate {

	double getMaxTemp();

	void setWarning(String message);

	boolean isRainPredicted();

}
