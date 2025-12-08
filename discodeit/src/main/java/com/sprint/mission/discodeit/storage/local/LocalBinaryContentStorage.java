package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final Path root;
  private final ApplicationEventPublisher publisher;

  public LocalBinaryContentStorage(
          UserRepository userRepository, NotificationRepository notificationRepository, @Value("${discodeit.storage.local.root-path}") Path root, ApplicationEventPublisher publisher
  ) {
      this.userRepository = userRepository;
      this.notificationRepository = notificationRepository;
      this.root = root;
      this.publisher = publisher;
  }

  @PostConstruct
  public void init() {
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }


  @Retryable(
          maxAttempts = 5, // 재시도 횟수
          backoff = @Backoff(delay = 1000) // 작업간 재시작 delay
  )
  public UUID put(UUID binaryContentId, byte[] bytes) {
    Path filePath = resolvePath(binaryContentId);
    if (Files.exists(filePath)) {
      throw new IllegalArgumentException("File with key " + binaryContentId + " already exists");
    }
    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      outputStream.write(bytes);

//      throw new IOException("강제 테스트용 IOException");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return binaryContentId;
  }

  public InputStream get(UUID binaryContentId) {
    Path filePath = resolvePath(binaryContentId);
    if (Files.notExists(filePath)) {
      throw new NoSuchElementException("File with key " + binaryContentId + " does not exist");
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private Path resolvePath(UUID key) {
    return root.resolve(key.toString());
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto metaData) {
    InputStream inputStream = get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metaData.fileName() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
        .body(resource);
  }


  // Recover: 모든 재시도 실패 시 호출됨.
  // 첫 파라미터는 예외, 뒤에는 원래 메서드 파라미터 순서와 타입이 와야 함.
  @Recover
  public UUID recover(Exception ex, UUID binaryContentId, byte[] bytes) {

    publisher.publishEvent(new S3UploadFailedEvent(ex,binaryContentId));

    //    // 1. RequestId (MDC에서 꺼냄)
//    String requestId = MDC.get("requestId");
//
//    // 2. 실패한 BinaryContentId
//    UUID failedId = binaryContentId;
//
//    // 3. 에러 메시지
//    String errorMessage = ex.getMessage();
//
////    String receiverId;
////    String title;
////    String content;
//
//    List<User> users = userRepository.findByRole(Role.ADMIN);
//    if(users.isEmpty()) {
//      users = userRepository.findByRole(Role.CHANNEL_MANAGER);
//    }
//    if(users.isEmpty()) {
//      users = userRepository.findByRole(Role.USER);
//    }
//    for(User user : users) {
//      UUID  userId = user.getId();
//      String title= "Local 파일 저장 실패";
//      String content = "RequestId: "+ requestId + " BinaryContentId: "+ binaryContentId + " Error: "+ errorMessage;
//
//      Notification noti= new Notification(userId.toString(), title, content);
//      notificationRepository.save(noti);
//    }

    return null;
  }

}
