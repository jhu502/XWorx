package com.flame.orm;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class XTransaction {
	private PlatformTransactionManager txManager;
	private DefaultTransactionDefinition transactionDef;
	private TransactionStatus transactionStatus;
	
	public XTransaction() {
		this.txManager = PersistenceHelper.service().getTransactionManager();
		transactionDef = new DefaultTransactionDefinition();
		transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);//新发起一个事务
	}
	
	public void begin() {
		transactionStatus = txManager.getTransaction(transactionDef);
	}
	
	public void commit() {
		txManager.commit(transactionStatus);
	}
	
	public void rollback() {
		txManager.rollback(transactionStatus);
	}
}