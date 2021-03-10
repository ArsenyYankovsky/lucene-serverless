package dev.arseny.service;

import dev.arseny.RequestUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class IndexSearcherService {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);

    private DirectoryReader directoryReader;

    public IndexSearcher getIndexSearcher(String indexName) {
        try {
            DirectoryReader newDirectoryReader;

            if (directoryReader == null) {
                newDirectoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(IndexConstants.LUCENE_INDEX_ROOT_DIRECTORY + indexName)));
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
