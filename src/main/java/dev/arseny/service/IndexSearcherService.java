package dev.arseny.service;

import com.upplication.s3fs.S3FileSystemProvider;
import dev.arseny.RequestUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

@ApplicationScoped
public class IndexSearcherService {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);

    private DirectoryReader directoryReader;

    public IndexSearcher getIndexSearcher(String indexName) {
        try {
            DirectoryReader newDirectoryReader;

            String endpoint = "s3://s3.amazonaws.com/";

            Path path = new S3FileSystemProvider().newFileSystem(URI.create(endpoint), System.getenv()).getPath(IndexConstants.BUCKET_NAME + "/" + indexName);

            if (directoryReader == null) {
                newDirectoryReader = DirectoryReader.open(FSDirectory.open(path));
            } else {
                newDirectoryReader = DirectoryReader.openIfChanged(directoryReader);
            }

            if (newDirectoryReader != null) {
                this.directoryReader = newDirectoryReader;
            }

            return new IndexSearcher(this.directoryReader);
        } catch (IOException e) {
            LOG.error("Error while trying to create an index searcher for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }
}
