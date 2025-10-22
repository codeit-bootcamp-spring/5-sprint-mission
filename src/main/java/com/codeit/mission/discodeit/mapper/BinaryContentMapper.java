package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    BinaryContentDto toDto(BinaryContent binaryContent);
}
