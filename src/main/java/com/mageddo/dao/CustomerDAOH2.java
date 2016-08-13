package com.mageddo.dao;

import javax.inject.Inject;
import java.util.List;

import com.mageddo.entity.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by elvis on 13/08/16.
 */

@Repository
public class CustomerDAOH2 implements CustomerDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDAOH2.class);

	@Inject
	JdbcTemplate jdbcTemplate;

	@Override
	public List<CustomerEntity> findByName(String name) {
		LOGGER.info("status=begin,name={}",  name);
		final List<CustomerEntity> customerEntities = jdbcTemplate.query(
				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{name},
				(rs, rowNum) -> new CustomerEntity(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
		);
		LOGGER.info("status=success");
		return customerEntities;
	}
}
