package com.revolut.moneytransfer.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.moneytransfer.dao.DaoFactory;
import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.CurrencyCodeValidator;
import com.revolut.moneytransfer.model.TransactionStatus;
import com.revolut.moneytransfer.model.UserTransaction;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

	private final DaoFactory daoFactory = DaoFactory.getDAOFactory();
	
	/**
	 * Transfer fund between two accounts.
	 * @param transaction
	 * @return
	 * @throws TransactionException
	 */
	@POST
    @Path("/transferFund")
	public Response transferFund(UserTransaction transaction) throws TransactionException {

		String currency = transaction.getCurrencyCode();
		if (CurrencyCodeValidator.INSTANCE.validateCcyCode(currency)) {
			int updateCount = daoFactory.getAccountDAO().transferAccountBalance(transaction);
			if (updateCount == 2) {
                TransactionStatus status = new TransactionStatus();
                status.setStatus("success");
				return Response.ok(status).build();
			} else {
				// transaction failed
				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
			}

		} else {
			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
		}

	}

}
