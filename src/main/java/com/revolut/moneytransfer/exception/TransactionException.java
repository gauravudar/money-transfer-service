package com.revolut.moneytransfer.exception;

public class TransactionException extends Exception {

	private static final long serialVersionUID = 1L;

	public TransactionException(String msg) {
		super(msg);
	}

	public TransactionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
