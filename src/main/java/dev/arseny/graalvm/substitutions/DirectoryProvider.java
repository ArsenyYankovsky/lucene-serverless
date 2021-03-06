package dev.arseny.graalvm.substitutions;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class DirectoryProvider {

    @ConfigProperty(name = "lucene.index.directory", defaultValue = "index")
    String indexDirectory;


    @Produces
    public Directory directory() {
        try {
            return new SimpleFSDirectory(Paths.get(indexDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load index", e);
        }
    }
}
