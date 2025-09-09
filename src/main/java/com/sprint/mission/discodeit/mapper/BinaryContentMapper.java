package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {
  BinaryContentDto toDto(BinaryContent binaryContent);

  List<BinaryContentDto> toDtos(List<BinaryContent> binaryContents);

}
