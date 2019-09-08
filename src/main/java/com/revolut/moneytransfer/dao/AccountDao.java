package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.List;


public interface AccountDao {

    List<Account> getAllAccounts() throws TransactionException;
    Account getAccountById(long accountId) throws TransactionException;
    long createAccount(Account account) throws TransactionException;
    int deleteAccountById(long accountId) throws TransactionException;
    int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws TransactionException;
    int transferAccountBalance(UserTransaction userTransaction) throws TransactionException;
}
