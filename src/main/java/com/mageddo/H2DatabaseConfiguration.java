package com.mageddo;

import com.mageddo.configuration.PropertyUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by elvis on 25/08/16.
 */
@Component
public class H2DatabaseConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DatabaseConfiguration.class);

	@Inject
	JdbcTemplate jdbcTemplate;

	@Inject
	PropertyUtils propertyUtils;

	@Bean(destroyMethod = "stop", initMethod = "start")
	public Server h2TCPServer() throws SQLException {
		if(propertyUtils.isH2DatabaseActive()){
			return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","9491");
		}
		return null;
	}

	@Bean(destroyMethod = "stop", initMethod = "start")
	public Server h2WebServer() throws SQLException {
		if(propertyUtils.isH2DatabaseActive()){
			return Server.createWebServer("-web","-webAllowOthers","-webPort","9492");
		}
		return null;
	}

	@PostConstruct
	public void sqlFactory() throws Exception {

		if(!propertyUtils.isH2DatabaseActive()){
			LOGGER.info("status=h2database inactive");
			return ;
		}

		LOGGER.info("status=begin");
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255) UNIQUE, last_name VARCHAR(255), balance NUMBER(12, 2))");

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = Arrays.asList("John Woo 1.99", "Jeff Dean 15.0", "Josh Bloch 35.0", "Mark Long 50.50").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> LOGGER.info(String.format("Inserting customer record for %s %s", name[0], name[1], name[2])));

		// Uses JdbcTemplate's batchUpdate operation to bulk load data
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name, balance) VALUES (?,?,?)", splitUpNames);
		LOGGER.info("status=success");

	}
}
