package com.mageddo.utils;

import org.h2.Driver;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Created by elvis on 07/05/17.
 */
public class DBUtils {

	private static final DataSourceTransactionManager manager;
	private static  DataSource datasource;
	private static final JdbcTemplate template;

	static {

		/*
			Documentação das propriedades do pool https://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html
		 */
		final Properties connProp = new Properties();
		connProp.setProperty("username", "sa");
		connProp.setProperty("password", "");
		connProp.setProperty("url", "jdbc:h2:mem:test");
		connProp.setProperty("driverClassName", "org.h2.Driver");

//		datasource = new SimpleDriverDataSource(new Driver(), "jdbc:h2:tcp://h2.dev:9092/h2/data", connProp);
//		datasource = new SimpleDriverDataSource(new Driver(), "jdbc:h2:mem:test", connProp);

		final org.apache.tomcat.jdbc.pool.DataSourceFactory sourceFactory = new org.apache.tomcat.jdbc.pool.DataSourceFactory();
		try {
			datasource = sourceFactory.createDataSource(connProp);
		} catch (Exception e) {
			throw new RuntimeException("Can not create pool", e);
		}

		manager = new DataSourceTransactionManager(datasource);
		template = new JdbcTemplate(datasource);
	}

	public static PlatformTransactionManager getTx(){
		return manager;
	}

	public static JdbcTemplate getTemplate(){
		return template;
	}

	public static DataSource getDatasource() {
		return datasource;
	}
}
