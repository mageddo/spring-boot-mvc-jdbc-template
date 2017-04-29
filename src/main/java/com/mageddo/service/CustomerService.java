package com.mageddo.service;

import com.mageddo.dao.CustomerDAO;
import com.mageddo.entity.CustomerEntity;
import com.mageddo.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

	@Autowired
	private PlatformTransactionManager txManger;

	public List<CustomerEntity> findByName(String name){
		return this.customerDAO.findByName(name);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void createCustomer(CustomerEntity customer) {
		customerDAO.create(customer);
	}

	public void createCustomer(CustomerEntity customer, TransactionDefinition td) {

		final TransactionTemplate template = new TransactionTemplate(txManger, td);
		template.execute(ts -> {
			customerDAO.create(customer);
			return null;
		});

	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = DuplicateKeyException.class)
	public void createCustomerWithoutFail(CustomerEntity customer) {
		customerDAO.create(customer);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void createCustomerWithoutFailMandatory(CustomerEntity customer) {
		createCustomerWithoutFail(customer);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean updateCustomerBalanceTurnoverAtDB(Long customerId, double turnoverValue) {
		LOGGER.info("status=begin, customerId={}, turnoverValue={}", customerId, turnoverValue);
		final boolean ok = customerDAO.updateCustomerBalanceTurnoverAtDB(customerId, turnoverValue);
		LOGGER.info("status=success");
		return ok;
	}

	public boolean updateCustomerBalanceTurnoverAtDBTd(Long customerId, double turnoverValue, TransactionDefinition td) {

		final TransactionTemplate template = new TransactionTemplate(txManger, td);
		return template.execute(ts -> {
			return this.updateCustomerBalanceTurnoverAtDB(customerId, turnoverValue);
		});

	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
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
		LOGGER.info("status=begin, value={}, balance={}, newBalance={}", turnoverValue, customer.getBalance(), newBalance);
		final boolean ok = customerDAO.updateCustomerBalance(customerId, newBalance);
		LOGGER.info("status=success, ok={}", ok);
		return ok;
	}

	public boolean updateCustomerBalanceAtDBWithSleepTd(Long customerId, double turnoverValue, int before, int after,
			TransactionDefinition td) throws InterruptedException {
		return new TransactionTemplate(txManger, td).execute(ts -> {
			return this.updateCustomerBalanceAtDBWithSleep(customerId, turnoverValue, before, after);
		});
	}

	@Transactional
	public boolean updateCustomerBalanceAtDBWithSleep(Long customerId, double turnoverValue, int before, int after) {
		Utils.sleep(before);
		LOGGER.info("status=begin, customerId={}, turnover={}, before={}, after={}", customerId, turnoverValue, before, after);
		final boolean balanceUpdate = this.updateCustomerBalanceTurnoverAtDB(customerId, turnoverValue);
		Utils.sleep(after);
		LOGGER.info("status=success, customerId={}, turnover={}", customerId, turnoverValue);
		return balanceUpdate;
	}

	public CustomerEntity findCustomerById(Long customerId, TransactionDefinition td) {
		return new TransactionTemplate(txManger, td).execute(ts -> this.findCustomerById(customerId));
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CustomerEntity findCustomerById(Long customerId) {
		LOGGER.info("status=begin, customerId={}", customerId);
		final CustomerEntity customerById = customerDAO.findCustomerById(customerId);
		LOGGER.info("status=success, customerId={}, value={}", customerId, customerById.getBalance());
		return customerById;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public CustomerEntity findCustomerByIdSerial(Long customerId) {

		LOGGER.info("status=begin, customerId={}", customerId);
		final CustomerEntity customerById = customerDAO.findCustomerById(customerId);
		LOGGER.info("status=success, customerId={}, value={}", customerId, customerById.getBalance());
		return customerById;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public boolean updateCustomerBalanceConcurrencyProblemWithSleep(long customerId, double turnoverValue,
																																	int before, int after) throws InterruptedException {

		LOGGER.info("status=begin, customerId={}", customerId);
		Thread.sleep(before);
		final boolean balanceUpdate = this.updateCustomerBalanceConcurrencyProblem(customerId, turnoverValue);
		Thread.sleep(after);
		LOGGER.info("status=success");
		return balanceUpdate;
	}
}
