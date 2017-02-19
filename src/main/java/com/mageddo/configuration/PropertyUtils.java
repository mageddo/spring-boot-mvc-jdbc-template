package com.mageddo.configuration;

import static com.mageddo.configuration.SystemProperties.H2DATABASE_ACTIVE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by elvis on 25/08/16.
 */
@Component
public class PropertyUtils {

	@Autowired
	private Environment environment;

	public boolean isH2DatabaseActive(){
		return "true".equalsIgnoreCase(environment.getProperty(H2DATABASE_ACTIVE.getKey()));
	}
}
