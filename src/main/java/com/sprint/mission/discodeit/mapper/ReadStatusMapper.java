package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  ReadStatusDto toDto(ReadStatus readStatus);
  List<ReadStatusDto> toDtoList(List<ReadStatus> readStatuses);

}
