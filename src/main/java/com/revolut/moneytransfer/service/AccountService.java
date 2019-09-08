package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.DaoFactory;
import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Balance;
import com.revolut.moneytransfer.model.CurrencyCodeValidator;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Account Service 
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
	
    private final DaoFactory daoFactory = DaoFactory.getDAOFactory();
    
    private static Logger log = Logger.getLogger(AccountService.class);

    
    /**
     * Find all accounts
     * @return
     * @throws TransactionException
     */
    @GET
    @Path("/all")
    public List<Account> getAllAccounts() throws TransactionException {
        return daoFactory.getAccountDAO().getAllAccounts();
    }

    /**
     * Find by account id
     * @param accountId
     * @return
     * @throws TransactionException
     */
    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) throws TransactionException {
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }
    
    /**
     * Find balance by account Id
     * @param accountId
     * @return
     * @throws TransactionException
     */
    @GET
    @Path("/{accountId}/balance")
    public Balance getBalance(@PathParam("accountId") long accountId) throws TransactionException {
        final Account account = daoFactory.getAccountDAO().getAccountById(accountId);

        if(account == null){
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        Balance netBalance = new Balance();
        netBalance.setCurrentBalance(account.getBalance());
        return netBalance;
    }
    
    /**
     * Create Account
     * @param account
     * @return
     * @throws TransactionException
     */
    @PUT
    @Path("/create")
    public Account createAccount(Account account) throws TransactionException {
        final long accountId = daoFactory.getAccountDAO().createAccount(account);
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    /**
     * Deposit amount by account Id
     * @param accountId
     * @param amount
     * @return
     * @throws TransactionException
     */
    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws TransactionException {

        if (amount.compareTo(CurrencyCodeValidator.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        daoFactory.getAccountDAO().updateAccountBalance(accountId,amount.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }

    /**
     * Withdraw amount by account Id
     * @param accountId
     * @param amount
     * @return
     * @throws TransactionException
     */
    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws TransactionException {

        if (amount.compareTo(CurrencyCodeValidator.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isDebugEnabled())
            log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " +accountId);
        daoFactory.getAccountDAO().updateAccountBalance(accountId,delta.setScale(4, RoundingMode.HALF_EVEN));
        return daoFactory.getAccountDAO().getAccountById(accountId);
    }


    /**
     * Delete amount by account Id
     * @param accountId
     * @return
     * @throws TransactionException
     */
    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws TransactionException {
        int deleteCount = daoFactory.getAccountDAO().deleteAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
