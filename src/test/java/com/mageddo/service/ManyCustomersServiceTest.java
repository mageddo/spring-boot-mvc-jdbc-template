package com.mageddo.service;

import com.mageddo.entity.CustomerEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.IllegalTransactionStateException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:04 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManyCustomersServiceTest {

    @Inject
    ManyCustomersService manyCustomersService;

    @Inject
    CustomerService customerService;

    @Inject
    private DatabaseConfigurationService databaseConfigurationService;

    @After
    public void construct() {
        databaseConfigurationService.resetDatabase();
    }

    @Test
    public void createCustomers() throws Exception {
        manyCustomersService.createCustomers(Arrays.asList(new CustomerEntity("Elvis", "Souza"),
                new CustomerEntity("Bruna", "Souza")));

        final List<CustomerEntity> users = customerService.findByName("Souza");

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("Elvis", users.get(0).getFirstName());
        Assert.assertEquals("Bruna", users.get(1).getFirstName());
    }

    @Test
    public void createCustomersWithoutFail() throws Exception {

        manyCustomersService.createCustomersWithoutFail(Arrays.asList(new CustomerEntity("Elvis", "Souza"),
                new CustomerEntity("Elvis", "Freitas"), new CustomerEntity("Bruna", "Souza")));

        final List<CustomerEntity> users = customerService.findByName("Souza");

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("Elvis", users.get(0).getFirstName());
        Assert.assertEquals("Souza", users.get(0).getLastName());
        Assert.assertEquals("Bruna", users.get(1).getFirstName());
    }

    /**
     * Este teste prova que não existe proxy entre os metodos da mesma classe
     *
     * Este teste que nao tem transacao chama um metodo NOT_SUPPORTED,
     * esse metodo chama outro da mesma classe que é REQUIRES_NEW
     * como não tem proxy ele não cria nova transação, o resultado é que quando é chamado o método de outra classe
     * que é MANDATORY recebe exceção falando que não tinha transação disponível
     *
     * @throws Exception
     */
    @Test(expected = IllegalTransactionStateException.class)
    public void createCustomersWithoutFailNotTransactional() throws Exception {
        try {
            manyCustomersService.createCustomersWithoutFailNotTransactional(Arrays.asList(new CustomerEntity("Elvis", "Souza")));
        }catch(IllegalTransactionStateException e){
            Assert.assertEquals(
              "Esperava que não existisse transação pois o proxiamento entre metodos da mesma classe nao funciona no spring",
              "No existing transaction found for transaction marked with propagation 'mandatory'", e.getMessage()
            );
            throw e;
        }

    }

}