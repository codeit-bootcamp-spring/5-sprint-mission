package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.exception.multipartfile.MultipartReadException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileMapper {

  public List<NewBinaryContent> toNewBinaryContentList(List<MultipartFile> multipartFiles) {
    return Optional.ofNullable(multipartFiles).orElse(List.of()).stream()
        .map(this::toNewBinaryContent)
        .flatMap(Optional::stream)
        .toList();
  }

  public Optional<NewBinaryContent> toNewBinaryContent(MultipartFile multipartFile) {

    if (multipartFile == null || multipartFile.isEmpty()) {
      return Optional.empty();
    }

    try {
      return Optional.of(new NewBinaryContent(
          multipartFile.getOriginalFilename(),
          multipartFile.getContentType(),
          multipartFile.getBytes()));
    } catch (IOException e) {
      throw new MultipartReadException(e);
    }
  }
}