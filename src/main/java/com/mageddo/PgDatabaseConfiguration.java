package com.mageddo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by elvis on 18/02/17.
 */
@Profile("Pg")
@Configuration
public class PgDatabaseConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(PgDatabaseConfiguration.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void sqlFactory() throws Exception {

		LOGGER.info("status=begin");
		jdbcTemplate.execute("DROP TABLE IF EXISTS customers ");
		jdbcTemplate.execute("CREATE TABLE customers(" +
			"id SERIAL primary key, first_name VARCHAR(255) UNIQUE, last_name VARCHAR(255), balance DECIMAL(12, 2))");

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = Arrays.asList("John Woo 1.99", "Jeff Dean 15.0", "Josh Bloch 35.0", "Mark Long 50.50").stream()
			.map(name -> {
				final String[] split = name.split(" ");
				return new Object[]{split[0], split[1], Double.parseDouble(split[2])};
			})
			.collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> LOGGER.info(String.format("Inserting customer record for %s %s", name[0], name[1], name[2])));

		// Uses JdbcTemplate's batchUpdate operation to bulk load data
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name, balance) VALUES (?,?,?)", splitUpNames);
		LOGGER.info("status=success");

	}

}
