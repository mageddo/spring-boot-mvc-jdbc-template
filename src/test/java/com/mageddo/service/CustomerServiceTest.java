package com.mageddo.service;

import com.mageddo.entity.CustomerEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:00 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerServiceTest {


	@Autowired
	private CustomerService customerService;

	@Test
	public void updateCustomerBalanceDefaultFlowSuccess() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);

		customerService.doCustomerBalanceTurnover(customer.getId(), 50);
		Assert.assertEquals((Double)50D, customerService.findCustomerById(customer.getId()).getBalance());
		customerService.doCustomerBalanceTurnover(customer.getId(), -3.00);
		Assert.assertEquals((Double)47.0, customerService.findCustomerById(customer.getId()).getBalance());

	}

	@Test
	public void updateCustomerBalanceConcurrency() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);
		customerService.doCustomerBalanceTurnover(customer.getId(), 50);

		final Thread t1 = new Thread(() -> {
			try {
				boolean ok = customerService.updateCustomerBalanceWithSleep(customer.getId(), -50, 0, 3000);
				Assert.assertTrue("", ok);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t1.start();
		Thread.sleep(1000);
		Assert.assertEquals(new Double(50.0), customerService.findCustomerById(customer.getId()).getBalance());

		final boolean ok = customerService.doCustomerBalanceTurnover(customer.getId(), -3.00);
		Assert.assertFalse(ok);

		t1.join();
		Assert.assertEquals(new Double(0.0), customerService.findCustomerById(customer.getId()).getBalance());

	}


	@Test
	public void updateCustomerBalanceConcurrencyProblem() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);
		customerService.doCustomerBalanceTurnover(customer.getId(), 50);

		final Thread t1 = new Thread(() -> {
			try {
				boolean ok = customerService.updateCustomerBalanceWithSleep(customer.getId(), -50, 0, 3000);
				Assert.assertTrue("", ok);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t1.start();
		Thread.sleep(1000);
		Assert.assertEquals(new Double(50.0), customerService.findCustomerById(customer.getId()).getBalance());

		final boolean ok = customerService.updateCustomerBalanceConcurrencyProblem(customer.getId(), -3.00);
		Assert.assertFalse(ok);

		t1.join();
		Assert.assertEquals(new Double(0.0), customerService.findCustomerById(customer.getId()).getBalance());

	}

}