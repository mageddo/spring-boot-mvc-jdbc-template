package com.mageddo.dao;

import java.util.List;

import com.mageddo.entity.CustomerEntity;

/**
 * Created by elvis on 13/08/16.
 */
public interface CustomerDAO {

	List<CustomerEntity> findByName(String name);
	void create(CustomerEntity customerEntity);
	void update(CustomerEntity customerEntity);

	/**
	 * Movimenta o saldo do cliente
	 * @param customerId
	 * @param turnoverValue o valor a ser movimentado negativo ou positivo
	 * @return se movimentou
	 */
	boolean updateCustomerBalance(Long customerId, double turnoverValue);

	CustomerEntity findCustomerById(Long customerId);
}
