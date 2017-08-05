package com.mageddo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by elvis on 07/05/17.
 */
public class SpringEnv {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringUtils.class);

	private final StandardEnvironment env;

	public SpringEnv(final String[] args) {

		LOGGER.info("status=begin, args={}", args);
		env = new StandardEnvironment();

		try {
			final MutablePropertySources propertySources = env.getPropertySources();
			propertySources.addLast(new SimpleCommandLinePropertySource(args));
			addPropertySource(propertySources, "");

			final String activeProfiles = env.getProperty("spring-profiles-active");
			if(activeProfiles != null && !activeProfiles.trim().isEmpty()){
				for (final String profile : activeProfiles.split(", ?")) {
					addPropertySource(propertySources, profile);
				}
			}
			LOGGER.info("status=success");
		} catch (IOException e) {
			LOGGER.error("status=fail-load-profile, msg={}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public StandardEnvironment getEnv() {
		return env;
	}

	private void addPropertySource(MutablePropertySources propertySources, String profileName) throws IOException {
		LOGGER.info("status=begin, profile={}", profileName);
		final String propertiesName = getPropertiesName(profileName);
		final Properties properties = loadProfileProperties(propertiesName);
		if (properties != null) {
			propertySources.addLast(new PropertiesPropertySource(propertiesName, properties));
		}
		LOGGER.info("status=begin, profile={}, properties={}", profileName, propertiesName);
	}

	private Properties loadProfileProperties(String propertiesName) throws IOException {

		final InputStream profileIn = ClassUtils
			.getDefaultClassLoader()
			.getResourceAsStream(propertiesName);

		if (profileIn == null) {
			return null;
		}
		final Properties properties = new Properties();
		properties.load(profileIn);
		return properties;
	}

	private String getPropertiesName(String profileName) {
		return "application" + (StringUtils.isEmpty(profileName) ? "" : "-" + profileName) + ".properties";
	}

}
