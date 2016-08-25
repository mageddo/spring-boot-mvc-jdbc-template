package com.mageddo.service;

import javax.inject.Inject;
import java.util.List;

import com.mageddo.dao.CustomerDAO;
import com.mageddo.entity.CustomerEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by elvis on 13/08/16.
 */
@Service
@Transactional
public class CustomerService {

	@Inject
	private CustomerDAO customerDAO;

	public List<CustomerEntity> findByName(String name){
		return this.customerDAO.findByName(name);
	}
}
