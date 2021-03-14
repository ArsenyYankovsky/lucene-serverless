package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.upplication.s3fs.S3FileSystemProvider;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TargetClass(S3FileSystemProvider.class)
public final class S3FileSystemProviderSubstitution {
    @Substitute
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        if (options != null && Arrays.asList(options).contains(StandardCopyOption.ATOMIC_MOVE)) {
            List<CopyOption> optionsList = new ArrayList<>(Arrays.asList(options));
            optionsList.remove(StandardCopyOption.ATOMIC_MOVE);
            options = optionsList.toArray(new CopyOption[optionsList.size()]);
        }

        copy(source, target, options);
        delete(source);
    }

    @Alias
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
    }

    @Alias
    public void delete(Path path) throws IOException {
    }
}
