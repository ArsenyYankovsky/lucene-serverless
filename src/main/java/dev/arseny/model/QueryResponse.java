package dev.arseny.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryResponse {
    private String totalDocuments;
    private List<Map<String, String>> documents = new ArrayList<>();

    public QueryResponse() {
    }

    public String getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(String totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public List<Map<String, String>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Map<String, String>> documents) {
        this.documents = documents;
    }
}
