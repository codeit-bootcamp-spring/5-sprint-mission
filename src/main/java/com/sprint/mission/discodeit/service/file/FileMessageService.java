package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageService() {
        this.DIRECTORY = "MESSAGE/MessageService";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message createMessage(Channel channel, String message, UUID author, boolean allMentioned) {
        if (channel == null) {
            throw new NullPointerException("channel id is null.");
        } if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is null or empty.");
        } if (author == null) {
            throw new NullPointerException("author is null.");
        }

        Message msg = new Message(channel.getId(), message, author, allMentioned);
        Path path = Paths.get(DIRECTORY, msg.getId() + EXTENSION);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(msg);
            return msg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message findById(UUID messageId) {
        Message msg = null;
        Path path = Paths.get(DIRECTORY, messageId + EXTENSION);

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            msg = (Message) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return msg;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENSION));
        if( files != null ) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Message msg = (Message) ois.readObject();
                    messages.add(msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return messages;
    }

    @Override
    public Message update(UUID messageId, String message, boolean allMentioned) {
        Path path = Paths.get(DIRECTORY, messageId + EXTENSION);
        Message msg = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(path.toFile().exists()) {
                msg = (Message) ois.readObject();
                msg.update(message, allMentioned);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(path.toFile(), false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

    @Override
    public Message deleteById(UUID messageId) {
        Path path = Paths.get(DIRECTORY, messageId + EXTENSION);
        Message msg = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(path.toFile().exists()) {
                msg = (Message) ois.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return msg;
    }
}
