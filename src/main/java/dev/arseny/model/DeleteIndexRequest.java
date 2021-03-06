package dev.arseny.model;

public class DeleteIndexRequest {
    private String indexName;

    public DeleteIndexRequest() {
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
