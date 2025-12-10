package com.sprint.mission.discodeit.user.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.user.domain.exception.UserProfileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProfileImageManager {

    private final BinaryContentRepository binaryContentRepository;

    private final ApplicationEventPublisher eventPublisher;

    public BinaryContent save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UserProfileUploadException(e);
        }

        BinaryContent saved = binaryContentRepository.save(
            new BinaryContent(file.getOriginalFilename(), file.getSize(), file.getContentType())
        );

        eventPublisher.publishEvent(new BinaryContentCreatedEvent(saved.getId(), bytes));

        return saved;
    }

    public void delete(BinaryContent profile) {
        if (profile != null) {
            binaryContentRepository.delete(profile);
        }
    }
}
