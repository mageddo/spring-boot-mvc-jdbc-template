package com.mageddo.service;

import com.mageddo.entity.CustomerEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void construct(){
        databaseConfigurationService.resetDatabase();
    }

    @Test
    public void createCustomers() throws Exception {
        manyCustomersService.createCustomers(Arrays.asList(new CustomerEntity(999, "Elvis", "Souza"),
                new CustomerEntity(1000, "Bruna", "Souza")));

        final List<CustomerEntity> users = customerService.findByName("Souza");

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("Elvis", users.get(0).getFirstName());
        Assert.assertEquals("Bruna", users.get(1).getFirstName());
    }

}