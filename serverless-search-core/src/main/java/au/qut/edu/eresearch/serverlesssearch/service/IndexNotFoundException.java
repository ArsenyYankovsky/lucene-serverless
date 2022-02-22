package au.qut.edu.eresearch.serverlesssearch.service;

public class IndexNotFoundException extends RuntimeException {

    private final String index;

    public IndexNotFoundException(String index, Throwable cause) {
        super(String.format("no such index [%s]", index), cause);
        this.index = index;
    }

    public IndexNotFoundException(String index) {
        super(String.format("no such index [%s]", index));
        this.index = index;
    }
}
