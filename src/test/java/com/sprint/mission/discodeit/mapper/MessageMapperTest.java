package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createBinaryContentWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createChannelWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createMessageWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageMapper 단위 테스트")
class MessageMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private MessageMapper messageMapper;

    @Test
    @DisplayName("Message를 DTO로 변환한다")
    void toDto_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();

        User author = createUserWithId(authorId, "author");
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "General channel");
        Message message = createMessageWithId(messageId, "Hello, world!", channel, author);
        BinaryContent attachment = createBinaryContentWithId(attachmentId, "image.png", 1024L, "image/png");
        List<BinaryContent> attachments = List.of(attachment);

        UserDto authorDto = new UserDto(authorId, "author", "author@example.com", null, true, Role.USER);
        BinaryContentDto attachmentDto = new BinaryContentDto(
            attachmentId, "image.png", 1024L, "image/png", BinaryContentStatus.SUCCESS);

        given(userMapper.toDto(author)).willReturn(authorDto);
        given(binaryContentMapper.toDtoList(attachments)).willReturn(List.of(attachmentDto));

        // when
        MessageDto result = messageMapper.toDto(message, attachments);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(messageId);
        assertThat(result.content()).isEqualTo("Hello, world!");
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.author()).isEqualTo(authorDto);
        assertThat(result.attachments()).hasSize(1);
        assertThat(result.attachments().get(0).id()).isEqualTo(attachmentId);
    }

    @Test
    @DisplayName("첨부파일 없이 Message를 DTO로 변환한다")
    void toDto_NoAttachments() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        User author = createUserWithId(authorId, "author");
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "General channel");
        Message message = createMessageWithId(messageId, "No attachments", channel, author);

        UserDto authorDto = new UserDto(authorId, "author", "author@example.com", null, false, Role.USER);

        given(userMapper.toDto(author)).willReturn(authorDto);
        given(binaryContentMapper.toDtoList(List.of())).willReturn(List.of());

        // when
        MessageDto result = messageMapper.toDto(message, List.of());

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(messageId);
        assertThat(result.attachments()).isEmpty();
    }

    @Test
    @DisplayName("null Message를 변환하면 null을 반환한다")
    void toDto_NullMessage() {
        // when
        MessageDto result = messageMapper.toDto(null, List.of());

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDtoWithAuthorDto로 UserDto를 직접 전달하여 변환한다")
    void toDtoWithAuthorDto_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        User author = createUserWithId(authorId, "author");
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "General channel");
        Message message = createMessageWithId(messageId, "With author DTO", channel, author);

        UserDto authorDto = new UserDto(authorId, "author", "author@example.com", null, true, Role.ADMIN);

        given(binaryContentMapper.toDtoList(any())).willReturn(List.of());

        // when
        MessageDto result = messageMapper.toDtoWithAuthorDto(message, authorDto, List.of());

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(messageId);
        assertThat(result.author()).isEqualTo(authorDto);
        assertThat(result.author().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("toDtoWithAuthorDto에서 null Message를 변환하면 null을 반환한다")
    void toDtoWithAuthorDto_NullMessage() {
        // given
        UserDto authorDto = new UserDto(UUID.randomUUID(), "author", "author@example.com", null, true, Role.USER);

        // when
        MessageDto result = messageMapper.toDtoWithAuthorDto(null, authorDto, List.of());

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("여러 첨부파일이 있는 Message를 DTO로 변환한다")
    void toDto_MultipleAttachments() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID attachment1Id = UUID.randomUUID();
        UUID attachment2Id = UUID.randomUUID();

        User author = createUserWithId(authorId, "author");
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "General channel");
        Message message = createMessageWithId(messageId, "Multiple files", channel, author);
        BinaryContent attachment1 = createBinaryContentWithId(attachment1Id, "doc.pdf", 1024L, "application/pdf");
        BinaryContent attachment2 = createBinaryContentWithId(attachment2Id, "photo.jpg", 2048L, "image/jpeg");
        List<BinaryContent> attachments = List.of(attachment1, attachment2);

        UserDto authorDto = new UserDto(authorId, "author", "author@example.com", null, true, Role.USER);
        List<BinaryContentDto> attachmentDtos = List.of(
            new BinaryContentDto(attachment1Id, "doc.pdf", 1024L, "application/pdf", BinaryContentStatus.SUCCESS),
            new BinaryContentDto(attachment2Id, "photo.jpg", 2048L, "image/jpeg", BinaryContentStatus.SUCCESS)
        );

        given(userMapper.toDto(author)).willReturn(authorDto);
        given(binaryContentMapper.toDtoList(attachments)).willReturn(attachmentDtos);

        // when
        MessageDto result = messageMapper.toDto(message, attachments);

        // then
        assertThat(result).isNotNull();
        assertThat(result.attachments()).hasSize(2);
        assertThat(result.attachments().get(0).fileName()).isEqualTo("doc.pdf");
        assertThat(result.attachments().get(1).fileName()).isEqualTo("photo.jpg");
    }
}
