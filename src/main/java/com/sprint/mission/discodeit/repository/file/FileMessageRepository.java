package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final String MESSAGE_DATA_DIR = "message_data";

    public FileMessageRepository() {
        File dataDir = new File(MESSAGE_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getMessageFilePath(UUID messageId) {
        return MESSAGE_DATA_DIR + File.separator + messageId.toString() + ".ser";
    }

    @Override
    public Message save(Message message) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getMessageFilePath(message.getId())))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getMessageFilePath(id)))) {
            return Optional.of((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        File dataDir = new File(MESSAGE_DATA_DIR);
        File[] messageFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (messageFiles != null) {
            for (File file : messageFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    messages.add((Message) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

    @Override
    public void deleteById(UUID id) {
        File messageFile = new File(getMessageFilePath(id));
        if (messageFile.exists()) {
            messageFile.delete();
        }
    }

    @Override
    public void clear() {
        File dataDir = new File(MESSAGE_DATA_DIR);
        File[] messageFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (messageFiles != null) {
            for (File file : messageFiles) {
                file.delete();
            }
        }
    }
}
