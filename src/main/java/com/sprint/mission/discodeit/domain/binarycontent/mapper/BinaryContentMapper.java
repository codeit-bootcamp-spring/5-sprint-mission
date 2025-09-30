package com.sprint.mission.discodeit.domain.binarycontent.mapper;

import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  BinaryContentDto toDto(BinaryContent binaryContent);
}
