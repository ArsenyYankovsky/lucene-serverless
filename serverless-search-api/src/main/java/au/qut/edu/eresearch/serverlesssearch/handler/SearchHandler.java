package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.SearchResults;
import au.qut.edu.eresearch.serverlesssearch.model.SearchRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SearchHandler {

    @Inject
    protected IndexService indexService;

    @GET
    @Path("/{index}/_search")
    @Produces( MediaType.APPLICATION_JSON )
    public SearchResults search(@PathParam("index") String index,
                                @QueryParam("q") String query) {
        return indexService.query(new SearchRequest(index, query));
    }






}
