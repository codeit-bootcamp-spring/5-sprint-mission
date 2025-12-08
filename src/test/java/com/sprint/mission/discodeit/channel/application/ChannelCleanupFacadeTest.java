package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.dto.ChannelDeletedEvent;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelCleanupFacade 단위 테스트")
class ChannelCleanupFacadeTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ChannelCleanupFacade channelCleanupFacade;

    @Test
    @DisplayName("메시지가 2500개일 때, 배치가 3번(1000, 1000, 500)으로 나누어 실행되는지 검증")
    void cleanup_withHugeMessages_executionsPartitioned() {
        // given
        UUID channelId = UUID.randomUUID();

        // 2500개의 메시지 ID 생성
        Set<UUID> hugeMessageIds = Stream.generate(UUID::randomUUID)
            .limit(2500)
            .collect(Collectors.toSet());

        // Mock 설정
        given(messageRepository.findIdsByChannelId(channelId)).willReturn(hugeMessageIds);
        given(readStatusRepository.findUserIdsByChannelId(channelId)).willReturn(Set.of()); // 읽음 상태 없음 가정

        // 첨부파일 조회 시 더미 데이터 반환 (실제 로직 통과용)
        given(messageAttachmentRepository.findAllByMessageIdIn(any()))
            .willAnswer(invocation -> {
                List<UUID> ids = invocation.getArgument(0);
                // 요청받은 ID 개수만큼 더미 첨부파일 리스트 반환
                return ids.stream().map(this::createDummyAttachment).toList();
            });

        // when
        channelCleanupFacade.cleanup(new ChannelDeletedEvent(channelId, ChannelType.PRIVATE));

        // then
        // 1. findAllByMessageIdIn 메서드가 정확히 3번 호출되었는지 확인
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
        then(messageAttachmentRepository).should(times(3)).findAllByMessageIdIn(captor.capture());

        List<List<UUID>> capturedArguments = captor.getAllValues();

        // 2. 각 호출마다 리스트 사이즈 검증
        assertThat(capturedArguments).hasSize(3);
        assertThat(capturedArguments.get(0)).hasSize(1000); // 첫 번째 배치
        assertThat(capturedArguments.get(1)).hasSize(1000); // 두 번째 배치
        assertThat(capturedArguments.get(2)).hasSize(500);  // 마지막 자투리

        // 3. 삭제(deleteAllInBatch)도 3번 호출되었는지 확인
        then(messageAttachmentRepository).should(times(3)).deleteAllInBatch(any());
        then(binaryContentRepository).should(times(3)).deleteAllByIdInBatch(any());

        // 4. 최종적으로 메시지 삭제 호출 확인
        then(messageRepository).should().deleteByChannelId(channelId);
    }

    @Test
    @DisplayName("PRIVATE 채널 삭제 시 READ_STATUSES와 SUBSCRIBED_CHANNELS 캐시가 evict된다")
    void cleanup_withPrivateChannel_evictsReadStatusesAndSubscribedChannels() {
        // given
        UUID channelId = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        given(messageRepository.findIdsByChannelId(channelId)).willReturn(Set.of());
        given(readStatusRepository.findUserIdsByChannelId(channelId)).willReturn(participantIds);

        // when
        channelCleanupFacade.cleanup(new ChannelDeletedEvent(channelId, ChannelType.PRIVATE));

        // then
        then(cacheService).should().evictAll(CacheName.READ_STATUSES, participantIds);
        then(cacheService).should().evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
        then(cacheService).should(never()).clear(any());
    }

    @Test
    @DisplayName("PUBLIC 채널 삭제 시 READ_STATUSES evict와 PUBLIC_CHANNELS clear가 호출된다")
    void cleanup_withPublicChannel_evictsReadStatusesAndClearsPublicChannels() {
        // given
        UUID channelId = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        given(messageRepository.findIdsByChannelId(channelId)).willReturn(Set.of());
        given(readStatusRepository.findUserIdsByChannelId(channelId)).willReturn(participantIds);

        // when
        channelCleanupFacade.cleanup(new ChannelDeletedEvent(channelId, ChannelType.PUBLIC));

        // then
        then(cacheService).should().evictAll(CacheName.READ_STATUSES, participantIds);
        then(cacheService).should().clear(CacheName.PUBLIC_CHANNELS);
        then(cacheService).should(never()).evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
    }

    private MessageAttachment createDummyAttachment(UUID ignored) {
        MessageAttachment attachmentMock = mock(MessageAttachment.class);
        BinaryContent binaryContentMock = mock(BinaryContent.class);

        given(attachmentMock.getAttachment()).willReturn(binaryContentMock);
        given(binaryContentMock.getId()).willReturn(UUID.randomUUID());

        return attachmentMock;
    }
}
