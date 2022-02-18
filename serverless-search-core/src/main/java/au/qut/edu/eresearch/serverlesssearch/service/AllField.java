package au.qut.edu.eresearch.serverlesssearch.service;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

public class AllField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();

    public static final String FIELD_NAME = "String";

    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        FIELD_TYPE.setTokenized(true);
        FIELD_TYPE.freeze();
    }

    public AllField(String value) {
        super(FIELD_NAME, value, FIELD_TYPE);
    }

    public AllField(BytesRef value) {
        super(FIELD_NAME, value, FIELD_TYPE);
    }
}
