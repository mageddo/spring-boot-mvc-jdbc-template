package com.mageddo.controller;

import java.util.List;

import javax.inject.Inject;

import com.mageddo.entity.CustomerEntity;
import com.mageddo.service.CustomerService;
import com.mageddo.service.ManyCustomersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by elvis on 13/08/16.
 */

@Controller
public class CustomerController {

	@Inject
	private CustomerService customerService;

	@Inject
	ManyCustomersService manyCustomersService;

	@RequestMapping(value = "/customer/{customer}",method = RequestMethod.GET)
	@ResponseBody
	public List<CustomerEntity> findByName(@PathVariable("customer") String customer){
		return customerService.findByName(customer);
	}

	@RequestMapping(value = "/customer/",method = RequestMethod.POST)
	@ResponseBody
	public void createCustomer(@RequestBody CustomerEntity customer){
		customerService.createCustomer(customer);
	}

	@RequestMapping(value = "/customers/",method = RequestMethod.POST)
	@ResponseBody
	public void createCustomers(@RequestBody List<CustomerEntity> customers){
		manyCustomersService.createCustomers(customers);
	}

}
