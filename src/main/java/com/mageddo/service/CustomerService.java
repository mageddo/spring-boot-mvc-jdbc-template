package com.mageddo.service;

import com.mageddo.dao.CustomerDAO;
import com.mageddo.entity.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by elvis on 13/08/16.
 */
@Service
@Transactional
public class CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	private CustomerDAO customerDAO;

	public List<CustomerEntity> findByName(String name){
		return this.customerDAO.findByName(name);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void createCustomer(CustomerEntity customer) {
		customerDAO.create(customer);
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = DuplicateKeyException.class)
	public void createCustomerWithoutFail(CustomerEntity customer) {
		customerDAO.create(customer);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void createCustomerWithoutFailMandatory(CustomerEntity customer) {
		createCustomerWithoutFail(customer);
	}

	@Transactional
	public boolean doCustomerBalanceTurnover(Long customerId, double turnoverValue) {
		LOGGER.info("status=begin, customerId={}", customerId);
		final boolean ok = customerDAO.doCustomerBalanceTurnover(customerId, turnoverValue);
		LOGGER.info("status=success");
		return ok;
	}

	@Transactional
	public boolean updateCustomerBalanceConcurrencyProblem(Long customerId, double turnoverValue) {
		LOGGER.info("status=begin, customerId={}", customerId);
		final CustomerEntity customer = customerDAO.findCustomerById(customerId);
		final double newBalance;
		if(turnoverValue > 0.0){
			newBalance = customer.getBalance() - turnoverValue;
		}else{
			newBalance = customer.getBalance() + turnoverValue;
		}
		if(newBalance < 0.0){
			throw new IllegalStateException("No balance available");
		}
		final boolean ok = customerDAO.updateCustomerBalance(customerId, newBalance);
		LOGGER.info("status=success, ok={}", ok);
		return ok;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean updateCustomerBalanceWithSleep(Long customerId, double turnoverValue, int before, int after) throws InterruptedException {
		LOGGER.info("status=begin, customerId={}", customerId);
		Thread.sleep(before);
		final boolean balanceUpdate = this.doCustomerBalanceTurnover(customerId, turnoverValue);
		Thread.sleep(after);
		LOGGER.info("status=success");
		return balanceUpdate;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public CustomerEntity findCustomerById(Long customerId) {
		LOGGER.info("status=begin, customerId={}", customerId);
		final CustomerEntity customerById = customerDAO.findCustomerById(customerId);
		LOGGER.info("status=success, customerId={}, value={}", customerId, customerById.getBalance());
		return customerById;
	}

}
