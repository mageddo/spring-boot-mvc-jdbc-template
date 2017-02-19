package com.mageddo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:39 PM
 */
@Repository
public class DatabaseConfigurationService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void resetDatabase(){
		jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
		final List<String> query = jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> {

			final String tableName = rs.getString("TABLE_NAME");
			jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
			return tableName;

		});

		jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");
	}

}
