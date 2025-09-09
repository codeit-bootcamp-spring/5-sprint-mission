package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BinaryContentMapper {

  @Mapping(target = "bytes", ignore = true)
  public abstract BinaryContentDto.Detail toDetail(BinaryContent entity);

  @Mapping(target = "bytes", ignore = true)
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
                                  .build();
  }
}
