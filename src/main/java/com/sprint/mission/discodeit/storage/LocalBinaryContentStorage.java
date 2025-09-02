package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.UUID;

@Repository
public class LocalBinaryContentStorage implements BinaryContentStorage{
    private final BinaryContentRepository binaryContentRepository;
    private final String storagePath;

    public LocalBinaryContentStorage(BinaryContentRepository binaryContentRepository,
                                     @Value("${storage.path}") String storagePath) {
        this.binaryContentRepository = binaryContentRepository;
        this.storagePath = storagePath;
    }

    public void init(){
        File dir = new File(storagePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("디렉터리 생성 실패: " + storagePath);
            }
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        String path = resolvePath(id);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + path, e);
        }
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        String path = resolvePath(id);
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("파일을 찾을 수 없음: " + path, e);
        }
    }

    @Override
    public ResponseEntity<?> download(UUID id) {
        String path = resolvePath(id);
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BinaryContent not found: " + id));

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.getFileName() + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(content.getContentType()))
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String resolvePath(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BinaryContent not found: " + id));
        String extension = "";

        String fileName = content.getFileName();
        int idx = fileName.lastIndexOf('.');
        if (idx > 0) {
            extension = fileName.substring(idx);
        }
        return storagePath + id + extension;
    }
}
