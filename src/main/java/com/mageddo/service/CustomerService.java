package com.mageddo.service;

import java.util.List;

import javax.inject.Inject;

import com.mageddo.dao.CustomerDAO;
import com.mageddo.entity.CustomerEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public void createCustomer(CustomerEntity customer) {
		customerDAO.create(customer);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void createCustomerWithoutException(CustomerEntity customer) {
		customerDAO.create(customer);
	}



}
