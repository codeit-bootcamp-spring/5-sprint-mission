package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.domain.service.BinaryContentService;
import com.sprint.mission.discodeit.infra.event.kafka.BinaryContentEventConsumer;
import com.sprint.mission.discodeit.infra.storage.BinaryContentStorage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BinaryContentEventConsumerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BinaryContentEventConsumer listener;
}
