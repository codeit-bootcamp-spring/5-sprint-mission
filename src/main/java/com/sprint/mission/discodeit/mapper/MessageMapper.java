package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.CreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MessageMapper {

  @Autowired
  protected BinaryContentMapper binaryContentMapper;

  @Mapping(target = "attachments", expression = "java(toAttachmentDetails(message.getAttachments()))")
  public abstract MessageDto.Detail toDetail(Message message);

  public abstract MessageDto.DetailResponse toDetailResponse(MessageDto.Detail detail);

  protected List<BinaryContentDto.Detail> toAttachmentDetails(List<BinaryContent> attachments) {

    if (attachments == null) {
      return List.of();
    }

    return attachments.stream()
                      .map(binaryContentMapper::toDetail)
                      .toList();
  }

  public Message toEntity(MessageDto.CreateCommand create, Channel channel, User author,
      List<BinaryContent> attachments) {
    return Message.builder()
                  .channel(channel)
                  .author(author)
                  .content(create.getContent())
                  .attachments(attachments)
                  .build();
  }


  public CreateCommand toCommand(CreateRequest request, List<MultipartFile> attachments) {
    return CreateCommand.builder()
                        .channelId(request.getChannelId())
                        .authorId(request.getAuthorId())
                        .content(request.getContent())
                        .attachments(attachments)
                        .build();
  }


  public UpdateCommand toCommand(UpdateRequest request, UUID id) {
    return UpdateCommand.builder()
                        .id(id)
                        .content(request.getNewContent())
                        .build();
  }
}
