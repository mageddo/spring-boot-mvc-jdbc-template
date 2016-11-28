package com.mageddo.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 11/28/16 2:45 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DatabaseConfigurationServiceTest {

	@Inject
	private CustomerService customerService;

	@Inject
	private DatabaseConfigurationService databaseConfigurationService;

	@Test
	public void resetDatabase() throws Exception {
		Assert.assertEquals("Jeff", customerService.findByName("Jeff").get(0).getFirstName());
		databaseConfigurationService.resetDatabase();
		Assert.assertEquals(0, customerService.findByName("Jeff").size());

	}

}