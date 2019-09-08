package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.User;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class UserDaoTest {
	
	private static Logger log = Logger.getLogger(UserDaoTest.class);
	
	private static final DaoFactory H_2_DAO_FACTORY = DaoFactory.getDAOFactory();

	@BeforeClass
	public static void setup() {
		// prepare test database and test data by executing sql script sampledata.sql
		log.debug("setting up test database and sample data....");
		H_2_DAO_FACTORY.populateTestData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllUsers() throws TransactionException {
		List<User> allUsers = H_2_DAO_FACTORY.getUserDAO().getAllUsers();
		assertTrue(allUsers.size() > 1);
	}

	@Test
	public void testGetUserById() throws TransactionException {
		User u = H_2_DAO_FACTORY.getUserDAO().getUserById(2L);
		assertTrue(u.getUserName().equals("daksh"));
	}

	@Test
	public void testGetNonExistingUserById() throws TransactionException {
		User u = H_2_DAO_FACTORY.getUserDAO().getUserById(500L);
		assertTrue(u == null);
	}

	@Test
	public void testGetNonExistingUserByName() throws TransactionException {
		User u = H_2_DAO_FACTORY.getUserDAO().getUserByName("abcdeftg");
		assertTrue(u == null);
	}

	@Test
	public void testCreateUser() throws TransactionException {
		User u = new User("test1", "test1@gmail.com");
		long id = H_2_DAO_FACTORY.getUserDAO().populateUser(u);
		User uAfterInsert = H_2_DAO_FACTORY.getUserDAO().getUserById(id);
		assertTrue(uAfterInsert.getUserName().equals("test1"));
		assertTrue(u.getEmailAddress().equals("test1@gmail.com"));
	}

	@Test
	public void testUpdateUser() throws TransactionException {
		User u = new User(1L, "test2", "test2@gmail.com");
		int rowCount = H_2_DAO_FACTORY.getUserDAO().updateUser(1L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(H_2_DAO_FACTORY.getUserDAO().getUserById(1L).getEmailAddress().equals("test2@gmail.com"));
	}

	@Test
	public void testUpdateNonExistingUser() throws TransactionException {
		User u = new User(500L, "test2", "test2@gmail.com");
		int rowCount = H_2_DAO_FACTORY.getUserDAO().updateUser(500L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}

	@Test
	public void testDeleteUser() throws TransactionException {
		int rowCount = H_2_DAO_FACTORY.getUserDAO().deleteUser(1L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(H_2_DAO_FACTORY.getUserDAO().getUserById(1L) == null);
	}

	@Test
	public void testDeleteNonExistingUser() throws TransactionException {
		int rowCount = H_2_DAO_FACTORY.getUserDAO().deleteUser(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

}
