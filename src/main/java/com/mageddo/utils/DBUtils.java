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
	private static final DataSource datasource;
	private static final JdbcTemplate template;

	static {

		/*
			Documentação das propriedades do pool https://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html
		 */
		final Properties connProp = new Properties();
		connProp.setProperty("username", "sa");
		connProp.setProperty("password", "");

		datasource = new SimpleDriverDataSource(new Driver(), "jdbc:h2:mem:test", connProp);

//		final org.apache.tomcat.jdbc.pool.DataSourceFactory sourceFactory = new org.apache.tomcat.jdbc.pool.DataSourceFactory();
//		connProp
//		try {
//			sourceFactory.createDataSource(connProp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

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
