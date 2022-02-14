package au.qut.edu.eresearch.serverlesssearch.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class IndexService {

    public static final String LUCENE_INDEX_ROOT_DIRECTORY = "/mnt/data/";

    private static final Logger LOG = Logger.getLogger(IndexService.class);

    public IndexWriter getIndexWriter(String indexName) {
        try {
            IndexWriter indexWriter = new IndexWriter(
                    FSDirectory.open(Paths.get(LUCENE_INDEX_ROOT_DIRECTORY + indexName)),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE)
            );
            return indexWriter;
        } catch (IOException e) {
            LOG.error("Error while trying to create an index writer for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }

    public IndexSearcher getIndexSearcher(String indexName) {
        try {
            DirectoryReader newDirectoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(LUCENE_INDEX_ROOT_DIRECTORY + indexName)));
            return new IndexSearcher(newDirectoryReader);
        } catch (IOException e) {
            LOG.error("Error while trying to create an index searcher for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }


}
