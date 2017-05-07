package com.mageddo;

import com.mageddo.utils.DBUtils;
import com.mageddo.utils.DefaultTransactionDefinition;
import com.mageddo.utils.SpringUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class Application {

	public static void main(String[] args) {

		SpringUtils.prepareEnv(args);

		final TransactionTemplate template = new TransactionTemplate(
			DBUtils.getTx(),
			new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT)
		);

		template.execute(ts -> {
			return DBUtils.getTemplate().queryForList("SELECT 1 FROM DUAL");
		});

	}



}
