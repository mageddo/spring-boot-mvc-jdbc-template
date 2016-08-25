package com.mageddo.configuration;

/**
 * Created by elvis on 25/08/16.
 */
public enum SystemProperties {

	H2DATABASE_ACTIVE("h2database.active");

	private final String key;

	SystemProperties(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
