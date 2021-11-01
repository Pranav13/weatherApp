package com.sapient.publicis.weatherapp.model;


import com.sapient.publicis.weatherapp.model.out.WeatherResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static com.google.code.beanmatchers.BeanMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class ModelTest {

	private final Set<Class<?>> set = new HashSet<>();

	@Test
	public void testBean0() {
		validateRecursively(WeatherResponse.class);
		set.clear();
	}

	private void validateRecursively(final Class<?> class1) {
		if (!set.add(class1)) {
			return;
		}

		assertThat(class1, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters(), hasValidBeanHashCode(),
				hasValidBeanEquals(), hasValidBeanToString()));

		final Field[] declaredFields = class1.getDeclaredFields();
		for (final Field field : declaredFields) {
			final Class<?> type = field.getType();
			if (type.getName().contains("sapient")) {
				validateRecursively(type);
			} else if (Iterable.class.isAssignableFrom(type)) {
				final Type genericType = field.getGenericType();
				final String typeName = genericType.getTypeName();
				final String trim = typeName.substring(typeName.indexOf('<') + 1, typeName.indexOf('>')).trim();
				try {
					if (trim.contains("sapient")) {
						validateRecursively(Class.forName(trim));
					}
				} catch (final ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if (type.isArray()) {
				final Class<?> componentType = type.getComponentType();
				if (componentType.getName().contains("sapient")) {
					validateRecursively(componentType);
				}
			}
		}

	}

}
