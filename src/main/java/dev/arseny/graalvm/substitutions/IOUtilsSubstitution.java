package dev.arseny.graalvm.substitutions;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.IOUtils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@TargetClass(IOUtils.class)
public final class IOUtilsSubstitution {
    /**
     * Ensure that any writes to the given file is written to the storage device that contains it.
     * @param fileToSync the file to fsync
     * @param isDir if true, the given file is a directory (we open for read and ignore IOExceptions,
     *  because not all file systems and operating systems allow to fsync on a directory)
     */
    @Substitute
    public static void fsync(Path fileToSync, boolean isDir) throws IOException {
        // If the file is a directory we have to open read-only, for regular files we must open r/w for the fsync to have an effect.
        // See http://blog.httrack.com/blog/2013/11/15/everything-you-always-wanted-to-know-about-fsync/
        if (isDir && Constants.WINDOWS) {
            // opening a directory on Windows fails, directories can not be fsynced there
            if (Files.exists(fileToSync) == false) {
                // yet do not suppress trying to fsync directories that do not exist
                throw new NoSuchFileException(fileToSync.toString());
            }
            return;
        }
        try (final FileChannel file = FileChannel.open(fileToSync, isDir ? StandardOpenOption.READ : StandardOpenOption.WRITE)) {
            try {
                file.force(true);
            } catch (IOException | AmazonS3Exception e) {
                if (isDir) {
                    assert (Constants.LINUX || Constants.MAC_OS_X) == false :
                            "On Linux and MacOSX fsyncing a directory should not throw IOException, " +
                                    "we just don't want to rely on that in production (undocumented). Got: " + e;
                    // Ignore exception if it is a directory
                    return;
                }
                // Throw original exception
                throw e;
            }
        } catch (AmazonS3Exception e) {
            return;
        }
    }
}
