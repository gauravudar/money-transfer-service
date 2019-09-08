package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.exception.ErrorResponse;
import com.revolut.moneytransfer.exception.TransactionException;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<TransactionException> {
	private static Logger log = Logger.getLogger(ServiceExceptionMapper.class);

	public ServiceExceptionMapper() {
	}

	public Response toResponse(TransactionException transactionException) {
		if (log.isDebugEnabled()) {
			log.debug("Mapping exception to Response....");
		}
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(transactionException.getMessage());

		// return internal server error for DAO exceptions
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
	}

}
