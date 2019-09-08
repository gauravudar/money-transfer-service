package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.Account;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class AccountDaoTest {

	private static final DaoFactory H_2_DAO_FACTORY = DaoFactory.getDAOFactory();

	@BeforeClass
	public static void setup() {
		// prepare test database and test data. Test data are initialised from
		// src/test/resources/sampledata.sql
		H_2_DAO_FACTORY.populateTestData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllAccounts() throws TransactionException {
		List<Account> allAccounts = H_2_DAO_FACTORY.getAccountDAO().getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}

	@Test
	public void testGetAccountById() throws TransactionException {
		Account account = H_2_DAO_FACTORY.getAccountDAO().getAccountById(1L);
		assertTrue(account.getUserName().equals("gaurav"));
	}

	@Test
	public void testGetNonExistingAccById() throws TransactionException {
		Account account = H_2_DAO_FACTORY.getAccountDAO().getAccountById(100L);
		assertTrue(account == null);
	}

	@Test
	public void testCreateAccount() throws TransactionException {
		BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		Account a = new Account("test2", balance, "CNY");
		long aid = H_2_DAO_FACTORY.getAccountDAO().createAccount(a);
		Account afterCreation = H_2_DAO_FACTORY.getAccountDAO().getAccountById(aid);
		assertTrue(afterCreation.getUserName().equals("test2"));
		assertTrue(afterCreation.getCurrencyCode().equals("CNY"));
		assertTrue(afterCreation.getBalance().equals(balance));
	}

	@Test
	public void testDeleteAccount() throws TransactionException {
		int rowCount = H_2_DAO_FACTORY.getAccountDAO().deleteAccountById(2L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(H_2_DAO_FACTORY.getAccountDAO().getAccountById(2L) == null);
	}

	@Test
	public void testDeleteNonExistingAccount() throws TransactionException {
		int rowCount = H_2_DAO_FACTORY.getAccountDAO().deleteAccountById(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

	@Test
	public void testUpdateAccountBalanceSufficientFund() throws TransactionException {

		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdated = H_2_DAO_FACTORY.getAccountDAO().updateAccountBalance(1L, deltaDeposit);
		assertTrue(rowsUpdated == 1);
		assertTrue(H_2_DAO_FACTORY.getAccountDAO().getAccountById(1L).getBalance().equals(afterDeposit));
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = H_2_DAO_FACTORY.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 1);
		assertTrue(H_2_DAO_FACTORY.getAccountDAO().getAccountById(1L).getBalance().equals(afterWithDraw));

	}

	@Test(expected = TransactionException.class)
	public void testUpdateAccountBalanceNotEnoughFund() throws TransactionException {
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = H_2_DAO_FACTORY.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}

}