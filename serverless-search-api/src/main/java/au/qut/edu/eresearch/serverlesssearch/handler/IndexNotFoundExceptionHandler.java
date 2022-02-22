package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.service.IndexNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IndexNotFoundExceptionHandler implements ExceptionMapper<IndexNotFoundException> {

    @Override
    public Response toResponse(IndexNotFoundException exception)
    {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
