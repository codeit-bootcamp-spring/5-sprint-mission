package com.sprint.mission.discodeit.repository.file.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"), "data");
    private static final String EXTENSION = ".ser";

    public static void init(Path subDir) {
        Path path = BASE_PATH.resolve(subDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("File directory init exception");
            }
        }
    }

    public static <T> void save(Path subDir, T data) {
        Path path = BASE_PATH.resolve(subDir.toString().concat(EXTENSION));
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(data.getClass().getName() + " file saving exception");
        }
    }

    public static <T> T findOne(Path subDir, Class<T> clz) {
        Path directory = BASE_PATH.resolve(subDir.toString().concat(EXTENSION));

        try (
                FileInputStream fis = new FileInputStream(directory.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if (clz.isInstance(obj)) {
                return clz.cast(obj);
            } else {
                throw new RuntimeException("Type mismatch: " + clz.getName());
            }
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    public static <T> List<T> findAll(Path subDir, Class<T> clz) {
        Path directory = BASE_PATH.resolve(subDir);
        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream.map(path -> {
                try (
                        FileInputStream fis = new FileInputStream(path.toFile());
                        ObjectInputStream ois = new ObjectInputStream(fis)
                ) {
                    Object o = ois.readObject();
                    if (clz.isInstance(o)) {
                        return clz.cast(o);
                    } else {
                        throw new RuntimeException("Type mismatch: " + clz.getName());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("File parsing exception while reading all");
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException("File parsing IOException while reading");
        }
    }

    public static void delete(Path subDir) {
        Path path = BASE_PATH.resolve(subDir.toString().concat(EXTENSION));

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("File deleting Exception");
        }
    }
}
