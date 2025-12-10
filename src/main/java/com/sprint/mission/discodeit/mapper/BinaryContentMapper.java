package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {
    BinaryContentStorage.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BinaryContentMapper {

  @Autowired
  protected BinaryContentStorage binaryContentStorage;

  @Mapping(target = "bytes", expression = "java(getBytes(entity))")
  public abstract BinaryContentDto.Detail toDetail(BinaryContent entity);

  public abstract BinaryContentDto.DetailResponse toDetailResponse(BinaryContentDto.Detail detail);

  public BinaryContent toEntity(BinaryContentDto.CreateCommand command) {
    return BinaryContent.builder()
                        .fileName(command.getFileName())
                        .contentType(command.getContentType())
                        .size(command.getSize())
                        .build();
  }

  public BinaryContentDto.Detail toDetail(BinaryContent entity, byte[] bytes) {

    if (entity == null) {
      return null;
    }

    return BinaryContentDto.Detail.builder()
                                  .id(entity.getId())
                                  .fileName(entity.getFileName())
                                  .contentType(entity.getContentType())
                                  .size(entity.getSize())
                                  .bytes(bytes)
                                  .status(entity.getStatus())
                                  .build();
  }

  protected byte[] getBytes(BinaryContent entity) {

    if (!entity.getStatus()
               .equals(BinaryContentStatus.SUCCESS.name())) {
      return new byte[0];
    }

    try {
      return binaryContentStorage.get(entity.getId())
                                 .readAllBytes();
    } catch (Exception e) {
      return new byte[0];
    }
  }
}
