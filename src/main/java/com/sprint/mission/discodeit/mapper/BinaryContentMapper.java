package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Mapping(target = "bytes", source = "id", qualifiedByName = "loadBytes")
  BinaryContentDto toDto(BinaryContent binaryContent,
      @Context BinaryContentStorage binaryContentStorage);

  @Named("loadBytes")
  default byte[] toBytes(UUID id, @Context BinaryContentStorage storage) {
    try (InputStream inputStream = storage.get(id)) {
      return inputStream.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load bytes for id " + id, e);
    }
  }
}
