package com.mageddo.service;

import com.mageddo.dao.DatabaseConfigurationDAO;
import com.mageddo.entity.CustomerEntity;
import com.mageddo.utils.DefaultTransactionDefinition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:00 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"Pg"})
public class CustomerServicePGIsolationTest {


	@Autowired
	private CustomerService customerService;

	@Autowired
	private DatabaseConfigurationDAO databaseConfigurationDAO;

	@After
	public void reset(){
		databaseConfigurationDAO.resetDatabase();
	}

	/**
	 * Exemplo de saque de forma serial em que desde que a logica de negocio esteja correta o
	 * saque então acontecerá de forma normal e esperada
	 * @throws Exception
	 */
	@Test
	public void updateCustomerBalanceDefaultFlowSuccess() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);

		customerService.updateCustomerBalanceTurnoverAtDB(customer.getId(), 50);
		Assert.assertEquals((Double)50D, customerService.findCustomerById(customer.getId()).getBalance());
		customerService.updateCustomerBalanceTurnoverAtDB(customer.getId(), -3.00);
		Assert.assertEquals((Double)47.0, customerService.findCustomerById(customer.getId()).getBalance());

	}

	/**
	 * Exemplo classico de saque com concorrencia em que esta está sendo tratada na base
	 * impedindo com successo que seja sacado mais do que o cliente tem de saldo, todavia,
	 * na consulta de saldo se tem um problema de status trazendo ainda o saldo antigo enquanto o primeiro saque
	 * não se completa, ao passo que dependendo do caso a consulta deveria esperar o saque terminar
	 * @throws Exception
	 */
	@Test
	public void updateCustomerBalanceConcurrency() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		final DefaultTransactionDefinition td = new DefaultTransactionDefinition(PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED);
		customerService.createCustomer(customer, td);
		customerService.updateCustomerBalanceTurnoverAtDBTd(customer.getId(), 50, td);

		final Thread t1 = new Thread(() -> {
			try {
				boolean ok = customerService.updateCustomerBalanceAtDBWithSleepTd(customer.getId(), -50, 0, 3000, td);
				Assert.assertTrue("", ok);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t1.start();

		Thread.sleep(1000);

		Assert.assertEquals(new Double(50.0), customerService.findCustomerById(customer.getId()).getBalance());

		// Não consegue sacar pois a transação anterior tirou todo o dinheiro
		// perceba que a consulta anterior pegou o valor errado mas o saque nao falha por ser todo feito me base
		final boolean ok = customerService.updateCustomerBalanceTurnoverAtDB(customer.getId(), -3.00);
		Assert.assertFalse(ok);

		Assert.assertEquals(new Double(0.0), customerService.findCustomerById(customer.getId()).getBalance());

	}


	/**
	 * 2o caso classico de concorrencia em que o saque é feito no código, não tendo a integridade garantida
	 * pois os isolamentos estão em READ_COMMITED, o saque além de deixar tirar todo o saldo do cliente, no segundo saque
	 * deixa tirar denovo e ainda o deixa com saldo positivo
	 * @throws Exception
	 */
	@Test
	public void updateCustomerBalanceConcurrencyProblem() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);
		customerService.updateCustomerBalanceTurnoverAtDB(customer.getId(), 50);

		final Thread t1 = new Thread(() -> {
			try {
				boolean ok = customerService.updateCustomerBalanceConcurrencyProblemWithSleep(customer.getId(), -50, 0, 3000);
				Assert.assertTrue("", ok);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t1.start();
		Thread.sleep(1000);
		Assert.assertEquals(new Double(50.0), customerService.findCustomerById(customer.getId()).getBalance());

		final boolean ok = customerService.updateCustomerBalanceConcurrencyProblem(customer.getId(), -3.00);
		Assert.assertTrue("Devia ter atualizado mesmo sem saldo devido ao problema de concorrencia", ok);

		t1.join();
		Assert.assertEquals(new Double(47.0), customerService.findCustomerById(customer.getId()).getBalance());

	}

	@Test
	public void updateCustomerBalanceConcurrencyProblemFix() throws Exception {

		final CustomerEntity customer = new CustomerEntity("Mary", "Santos");
		customerService.createCustomer(customer);
		customerService.updateCustomerBalanceTurnoverAtDB(customer.getId(), 50);

		final Thread t1 = new Thread(() -> {
			try {
				boolean ok = customerService.updateCustomerBalanceConcurrencyProblemWithSleep(customer.getId(), -50, 0, 3000);
				Assert.assertTrue("", ok);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		t1.start();
		Thread.sleep(1000);
		Assert.assertEquals(new Double(50.0), customerService.findCustomerByIdSerial(customer.getId()).getBalance());

		final boolean ok = customerService.updateCustomerBalanceConcurrencyProblem(customer.getId(), -3.00);
		Assert.assertTrue("Devia ter atualizado mesmo sem saldo devido ao problema de concorrencia", ok);

		t1.join();
		Assert.assertEquals(new Double(47.0), customerService.findCustomerById(customer.getId()).getBalance());

	}

}