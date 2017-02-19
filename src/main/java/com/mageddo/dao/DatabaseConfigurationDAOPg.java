package com.mageddo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:39 PM
 */
@Profile("Pg")
@Repository
public class DatabaseConfigurationDAOPg implements DatabaseConfigurationDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void resetDatabase(){
		final List<String> query = jdbcTemplate.query(
			" SELECT tablename FROM pg_tables WHERE tableowner = 'root' AND schemaname = 'public';",
			(rs, rowNum) -> {

				final String tableName = rs.getString("tablename");
				jdbcTemplate.execute(String.format("TRUNCATE TABLE %s CASCADE", tableName));
				return tableName;

			});
	}

}
