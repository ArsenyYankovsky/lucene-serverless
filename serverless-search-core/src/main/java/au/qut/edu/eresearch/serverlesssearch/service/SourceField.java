package au.qut.edu.eresearch.serverlesssearch.service;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

public class SourceField extends Field {

    public static final FieldType FIELD_TYPE = new FieldType();

    public static final String FIELD_NAME = "_source";

    static {
        FIELD_TYPE.setIndexOptions(IndexOptions.NONE); // not indexed
        FIELD_TYPE.setStored(true);
        FIELD_TYPE.setOmitNorms(true);
        FIELD_TYPE.freeze();
    }

    public SourceField(String value) {
        super(FIELD_NAME, value, FIELD_TYPE);
    }

    public SourceField(BytesRef value) {
        super(FIELD_NAME, value, FIELD_TYPE);
    }
}
