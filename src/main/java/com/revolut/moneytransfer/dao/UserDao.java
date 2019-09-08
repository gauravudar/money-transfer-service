package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.User;

import java.util.List;

public interface UserDao {
	
	List<User> getAllUsers() throws TransactionException;

	User getUserById(long userId) throws TransactionException;

	User getUserByName(String userName) throws TransactionException;

	/**
	 * @param user:
	 * user to be created
	 * @return userId generated from insertion. return -1 on error
	 */
	long populateUser(User user) throws TransactionException;

	int updateUser(Long userId, User user) throws TransactionException;

	int deleteUser(long userId) throws TransactionException;

}
