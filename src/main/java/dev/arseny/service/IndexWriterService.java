package dev.arseny.service;

import com.upplication.s3fs.S3FileSystemProvider;
import dev.arseny.RequestUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoDeletionPolicy;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;


@ApplicationScoped
public class IndexWriterService {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);
    private S3FileSystemProvider s3FileSystemProvider = new S3FileSystemProvider();

    public IndexWriter getIndexWriter(String indexName) {
        String endpoint = "s3://s3.amazonaws.com/";

        Path path = this.s3FileSystemProvider.getFileSystem(URI.create(endpoint), System.getenv()).getPath("/" + System.getenv("BUCKET_NAME") + "/" + indexName);

        try {
            IndexWriter indexWriter = new IndexWriter(
                    FSDirectory.open(path),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE)
            );

            return indexWriter;
        } catch (IOException e) {
            LOG.error("Error while trying to create an index writer for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }
}
