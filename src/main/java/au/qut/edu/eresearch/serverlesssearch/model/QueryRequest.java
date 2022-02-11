package au.qut.edu.eresearch.serverlesssearch.model;

public class QueryRequest {
    private String indexName;
    private String query;

    public QueryRequest() {
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
