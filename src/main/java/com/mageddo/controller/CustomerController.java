package com.mageddo.controller;

import javax.inject.Inject;
import java.util.List;

import com.mageddo.entity.CustomerEntity;
import com.mageddo.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

	@RequestMapping(value = "/customer/{customer}",method = RequestMethod.GET)
	@ResponseBody
	public List<CustomerEntity> findByName(@PathVariable("customer") String customer){
		return customerService.findByName(customer);
	}
}
