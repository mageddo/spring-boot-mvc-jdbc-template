package com.mageddo.service;

import com.mageddo.entity.CustomerEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 8/26/16 12:18 PM
 */
@Service
public class ManyCustomersService {

    @Inject
    private CustomerService customerService;

    @Transactional
    public void createCustomers(List<CustomerEntity> customerEntities){
        for(CustomerEntity customerEntity: customerEntities){
            customerService.createCustomer(customerEntity);
        }
    }

    @Transactional
    public void createCustomersWithoutFail(List<CustomerEntity> customerEntities){
        for(CustomerEntity customerEntity: customerEntities){
            customerService.createCustomerWithoutFail(customerEntity);
        }
    }
}
