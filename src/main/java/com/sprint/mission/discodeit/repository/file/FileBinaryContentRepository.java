package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;

@Repository("fileBinaryContentRepository")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", "BinaryContent");
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(userNullable);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(content -> ids.contains(content.getId()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(bc -> Objects.equals(bc.getMessageId(), messageId))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        if (messageId == null) {
            return;
        }

        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                            BinaryContent bc = (BinaryContent) ois.readObject();
                            if (bc != null && Objects.equals(bc.getMessageId(), messageId)) {
                                Files.deleteIfExists(path);
                            }
                        } catch (IOException | ClassNotFoundException e) {
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
