package com.revolut.moneytransfer.dao.impl;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.H2DbFactory;
import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.CurrencyCodeValidator;
import com.revolut.moneytransfer.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDao {

	private static Logger log = Logger.getLogger(AccountDaoImpl.class);
	private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
	private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
	private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
	private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";
	private final static String SQL_DELETE_ACC_BY_ID = "DELETE from Account WHERE AccountId = ? ";
	private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";

    /**
     * Get all accounts
     */
	public List<Account> getAllAccounts() throws TransactionException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Account> allAccounts = new ArrayList<>();
		try {
			conn = H2DbFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("getAllAccounts(): Get  Account " + acc);
				allAccounts.add(acc);
			}
			return allAccounts;
		} catch (SQLException e) {
			throw new TransactionException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}
	
	/**
	 * Get account by id
	 */
	public Account getAccountById(long accountId) throws TransactionException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			conn = H2DbFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
			stmt.setLong(1, accountId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
						rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("Retrieve Account By Id: " + acc);
			}
			return acc;
		} catch (SQLException e) {
			throw new TransactionException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}
	
	/**
	 * Create account
	 */
	public long createAccount(Account account) throws TransactionException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			conn = H2DbFactory.getConnection();
			stmt = conn.prepareStatement(SQL_CREATE_ACC);
			stmt.setString(1, account.getUserName());
			stmt.setBigDecimal(2, account.getBalance());
			stmt.setString(3, account.getCurrencyCode());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createAccount(): Creating account failed, no rows affected.");
				throw new TransactionException("Account Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new TransactionException("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Account  " + account);
			throw new TransactionException("createAccount(): Error creating user account " + account, e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}
	
	/**
	 * Delete account by id
	 */
	public int deleteAccountById(long accountId) throws TransactionException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = H2DbFactory.getConnection();
			stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
			stmt.setLong(1, accountId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new TransactionException("deleteAccountById(): Error deleting user account Id " + accountId, e);
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}
	
	/**
	 * Update account balance
	 */
	public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws TransactionException {
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			conn = H2DbFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, accountId);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				targetAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("updateAccountBalance from Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new TransactionException("updateAccountBalance(): fail to lock account : " + accountId);
			}
			// update account upon success locking
			BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
			if (balance.compareTo(CurrencyCodeValidator.zeroAmount) < 0) {
				throw new TransactionException("Not sufficient Fund for account: " + accountId);
			}

			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, balance);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			conn.commit();
			if (log.isDebugEnabled())
				log.debug("New Balance after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new TransactionException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return updateCount;
	}

	/**
	 * Transfer balance between two accounts.
	 */
	public int transferAccountBalance(UserTransaction userTransaction) throws TransactionException {
		int result = -1;
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;


		try {
			conn = H2DbFactory.getConnection();
			conn.setAutoCommit(false);
			// lock the credit and debit account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getFromAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance from Account: " + fromAccount);
			}
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getToAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
						rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance to Account: " + toAccount);
			}

			// check locking status
			if (fromAccount == null || toAccount == null) {
				throw new TransactionException("Fail to lock both accounts for write");
			}

			// check transaction currency
			if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
				throw new TransactionException(
						"Fail to transfer Fund, transaction ccy are different from source/destination");
			}

			// check ccy is the same for both accounts
			if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
				throw new TransactionException(
						"Fail to transfer Fund, the source and destination account are in different currency");
			}

			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
			if (fromAccountLeftOver.compareTo(CurrencyCodeValidator.zeroAmount) < 0) {
				throw new TransactionException("Not enough Fund from source Account ");
			}
			// proceed with update
			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setLong(2, userTransaction.getFromAccountId());
			updateStmt.addBatch();
			updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
			updateStmt.setLong(2, userTransaction.getToAccountId());
			updateStmt.addBatch();
			int[] rowsUpdated = updateStmt.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			if (log.isDebugEnabled()) {
				log.debug("Number of rows updated for the transfer : " + result);
			}
			// If there is no error, commit the transaction
			conn.commit();
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,
					se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new TransactionException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return result;
	}

}
