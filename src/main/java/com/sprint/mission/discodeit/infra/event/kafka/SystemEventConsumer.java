package com.sprint.mission.discodeit.infra.event.kafka;

import com.sprint.mission.discodeit.infra.event.binarycontent.BinaryContentUploadFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SystemEventConsumer {

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "system-group")
    public void onS3UploadFailed(BinaryContentUploadFailedEvent event) {
        log.error("[CRITICAL] S3 업로드 실패 발생! id={}, requestId={}, cause={}",
            event.binaryContentId(), event.requestId(), event.errorMessage());
    }
}
