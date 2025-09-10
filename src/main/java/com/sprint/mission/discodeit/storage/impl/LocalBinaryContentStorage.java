package com.sprint.mission.discodeit.storage.impl;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("discodeit.storage.local.root-paath") Path root) {
        this.root = root;
    }

    public void init() {
        if(!Files.exists(root)) {
            try{
                Files.createDirectories(root);
            }catch (IOException e){
                e.printStackTrace();
                throw new RuntimeException("Could not create directory: " + root);
            }
        }
    }

    @Override
    public UUID put(UUID uuid, List<Byte> bite) {
        return;
    }

    @Override
    public InputStream get(UUID uuid) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        return null;
    }

    private Path resolvePath(UUID uuid) {
       return root.resolve(uuid.toString());
    }
}
