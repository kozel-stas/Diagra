package com.diagra;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TransitionServiceImpl implements TransitionService {

    private static final Logger LOG = LogManager.getLogger(TransitionServiceImpl.class);
    private static final String MEDIA_TYPE = "media-type";

    private final File dir;

    public TransitionServiceImpl(@Value("#{systemProperties['user.dir']}") String dir) {
        Objects.requireNonNull(dir);
        this.dir = new File(dir + "/transition");
        this.dir.mkdir();
        Preconditions.checkState(this.dir.isDirectory());
        new ExpiryThread().start();
    }

    @Override
    public Resource load(String transitionLink) {
        UUID.fromString(transitionLink);
        File file = new File(dir.getAbsolutePath() + "/" + transitionLink + ".srv");
        if (file.exists()) {
            try {
                UserDefinedFileAttributeView attr = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
                ByteBuffer byteBuffer = ByteBuffer.allocate(attr.size(MEDIA_TYPE));
                attr.read(MEDIA_TYPE, byteBuffer);
                MediaType mediaType = MediaType.parseMediaType(new String(byteBuffer.array()));
                return new Resource(
                        mediaType,
                        new FileSystemResource(file)
                );
            } catch (IOException e) {
                LOG.error(e);
                return null;
            } finally {
                file.delete();
            }
        }
        return null;
    }

    @Override
    public String generateTransitionLink(Resource resource) {
        try {
            String filename = UUID.randomUUID().toString();
            File file = new File(dir.getAbsolutePath() + "/" + filename + ".srv");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(IOUtils.toByteArray(resource.getResource().getInputStream()));
            UserDefinedFileAttributeView attr = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
            attr.write(MEDIA_TYPE, ByteBuffer.wrap(resource.getMediaType().toString().getBytes()));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final class ExpiryThread extends Thread {

        public ExpiryThread() {
            setName("Transition-expired-thread");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException e) {
                    LOG.warn(e);
                    break;
                }
                File[] files = dir.listFiles();
                if (files == null) {
                    continue;
                }
                for (File file : files) {
                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        if (attr.creationTime().toMillis() + TimeUnit.MINUTES.toMillis(20) < System.currentTimeMillis()) {
                            file.delete();
                        }
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                }
            }
            LOG.info("Expired thread was stopped.");
        }

    }

}
