package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final Path directory = Path.of("Message");

    public FileMessageRepository() throws IOException {
        try {
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException e) {
            System.out.println(directory + " Directory already exists!");
        }
    }

    @Override
    public Message save(Message message) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Message/" + message.getId().toString()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Message/" + id.toString()))) {
            Message message = (Message) ois.readObject();
            return Optional.of(message);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        File[] files = directory.toFile().listFiles();

        for (File file : files) {
            if (file.isFile()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                    Message message = (Message) ois.readObject();
                    messages.add(message);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return messages;
    }

    @Override
    public Message update(UUID id, Message message) {
        Message m;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Message/" + id.toString()))) {
            m = (Message) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        m.setName(message.getName());
        m.setTitle(message.getTitle());
        m.setContent(message.getContent());

        save(m);
        return message;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Path.of("Message/" + id.toString());
        if (Files.exists(path)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        Path path = Path.of("Message/" + id.toString());
        try {
            Files.delete(path);
            System.out.println("삭제 성공");
        } catch (NoSuchFileException e) {
            System.out.println("삭제 실패");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
