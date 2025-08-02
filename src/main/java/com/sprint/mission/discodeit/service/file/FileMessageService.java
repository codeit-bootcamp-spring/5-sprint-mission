package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private static final String MESSAGE_DATA_DIR = "message_data";

    public FileMessageService() {
        File dataDir = new File(MESSAGE_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getMessageFilePath(UUID messageId) {
        return MESSAGE_DATA_DIR + File.separator + messageId.toString() + ".ser";
    }

    @Override
    public Message create(UUID channelId, UUID authorId, String content) {
        Message message = new Message(channelId, authorId, content);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getMessageFilePath(message.getId())))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getMessageFilePath(messageId)))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
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
    public Message update(UUID messageId, String content) {
        Message message = find(messageId);
        if (message != null) {
            message.update(content);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getMessageFilePath(message.getId())))) {
                oos.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        File messageFile = new File(getMessageFilePath(messageId));
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
