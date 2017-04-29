package com.mageddo.utils;

/**
 * Created by elvis on 29/04/17.
 */
public class DefaultTransactionDefinition  extends org.springframework.transaction.support.DefaultTransactionDefinition {

	public DefaultTransactionDefinition(int propagation, int isolation) {
		setPropagationBehavior(propagation);
		setIsolationLevel(isolation);
	}
}
