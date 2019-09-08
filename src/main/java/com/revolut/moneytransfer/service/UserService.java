package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.DaoFactory;
import com.revolut.moneytransfer.exception.TransactionException;
import com.revolut.moneytransfer.model.User;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
 
	private final DaoFactory daoFactory = DaoFactory.getDAOFactory();
    
	private static Logger log = Logger.getLogger(UserService.class);

	/**
	 * Find by userName
	 * @param userName
	 * @return
	 * @throws TransactionException
	 */
    @GET
    @Path("/{userName}")
    public User getUserByName(@PathParam("userName") String userName) throws TransactionException {
        if (log.isDebugEnabled())
            log.debug("Request Received for get User by Name " + userName);
        final User user = daoFactory.getUserDAO().getUserByName(userName);
        if (user == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
        return user;
    }
    
    /**
	 * Find by all
	 * @return
	 * @throws TransactionException
	 */
    @GET
    @Path("/all")
    public List<User> getAllUsers() throws TransactionException {
        return daoFactory.getUserDAO().getAllUsers();
    }
    
    /**
     * Create User
     * @param user
     * @return
     * @throws TransactionException
     */
    @POST
    @Path("/create")
    public User createUser(User user) throws TransactionException {
        if (daoFactory.getUserDAO().getUserByName(user.getUserName()) != null) {
            throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
        }
        final long uId = daoFactory.getUserDAO().populateUser(user);
        return daoFactory.getUserDAO().getUserById(uId);
    }
    
    /**
     * Find by User Id
     * @param userId
     * @param user
     * @return
     * @throws TransactionException
     */
    @PUT
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") long userId, User user) throws TransactionException {
        final int updateCount = daoFactory.getUserDAO().updateUser(userId, user);
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Delete by User Id
     * @param userId
     * @return
     * @throws TransactionException
     */
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) throws TransactionException {
        int deleteCount = daoFactory.getUserDAO().deleteUser(userId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
