package com.revolut.moneytransfer.dao;

public abstract class DaoFactory {

	public abstract UserDao getUserDAO();

	public abstract AccountDao getAccountDAO();

	public abstract void populateTestData();

	public static DaoFactory getDAOFactory() {
			return new H2DbFactory();
		}
}
