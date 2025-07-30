package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageRepository() {
        this.DIRECTORY = "MESSAGE";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isValidDirectory(Path dirPath) {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            System.out.println("[Repo]Message directory does not exist ro is not a directory: " + DIRECTORY);
            return false;
        }
        return true;
    }


    @Override
    public Message save(Message message) {
        Path path = Paths.get(DIRECTORY, message.getId().toString() + EXTENSION);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(message);
            System.out.println("[Repo]Message save success." + message.getId());
        } catch (IOException e) {
            System.out.println("[Repo]Message save failed." + e.getMessage());
            throw new RuntimeException("[Repo]Message save failed.]", e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        Path dirPath = Paths.get(DIRECTORY);

        if (!isValidDirectory(dirPath)) {
            return Optional.empty();
        }
        Message message = null;
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            message = (Message) ois.readObject();
            System.out.println("[Repo]Message found success." + message.getId());
        } catch (FileNotFoundException e) {
            System.out.println("[Repo]Message file not found for ID." + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        List<Message> allMessage = new ArrayList<>();
        Path dirPath = Paths.get(DIRECTORY);

        if (!isValidDirectory(dirPath)) {
            System.err.println("[Repo]Warning: Message directory is not valid. Returning empty list");
            return allMessage;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*" + EXTENSION)) {
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(entry.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Message message = (Message) ois.readObject();
                    if (message != null) {
                        allMessage.add(message);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("[Repo]Error reading message file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("[Repo]Error accessing message directory: " + DIRECTORY, e);
        }
        return allMessage;
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> messageList = new ArrayList<>();
        Path dirPath = Paths.get(DIRECTORY);

        if (!isValidDirectory(dirPath)) {
            System.err.println("[Repo]Warning: Message directory is not valid. Returning empty list");
            return messageList;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*" + EXTENSION)) {
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(entry.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Message message = (Message) ois.readObject();

                    if (message != null) {
                        messageList.add(message);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("[Repo]Error reading message file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("[Repo]Error accessing message directory: " + DIRECTORY, e);
        }
        return messageList;
    }

    @Override
    public void delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try {
            if (Files.deleteIfExists(path)) {
                System.out.println("[Repo]Message file deleted: " + id);
            } else {
                System.out.println("[Repo]Message file not found: " + id);
            }
        } catch (IOException e) {
            System.err.println("[Repo]Error deleting Message file: " + id + ". Details: " + e.getMessage());
            throw new RuntimeException("[Repo]Failed to delete message file.", e);
        }
    }
}
