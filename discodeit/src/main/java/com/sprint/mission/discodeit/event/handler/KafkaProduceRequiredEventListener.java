package com.sprint.mission.discodeit.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
//@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;

    @CacheEvict(value ="notification",key = "'noti'")
    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) throws JsonProcessingException {
        log.info("[AFTER_COMMIT] 메시지 생성 커밋 완료, {}", event.id());
        List<ReadStatus> statuses = readStatusRepository.findAllByChannel(event.message().getChannel());
        List<ReadStatus> target=new ArrayList<>();
        for(ReadStatus readStatus:statuses){
            if(readStatus.isNotificationEnabled()){
                if( !readStatus.getUser().getId().equals(event.message().getAuthor().getId()) ){
                    target.add(readStatus);
                }
            }
        }
        List<Notification> notifications=new ArrayList<>();
        for(ReadStatus readStatus:target){
            Notification notifi = new Notification(
                    readStatus.getUser(),""+event.message().getAuthor().getUsername()+
                    " ("+readStatus.getChannel().getName()+")", event.message().getContent());
            notifications.add(notifi);
        }

        for(Notification notifi:notifications){
            notificationRepository.save(notifi);
        }
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);

    }

    @CacheEvict(key = "'noti'")
    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        log.info("[AFTER_COMMIT] 권한 수정 커밋 완료, {}", event.user().getId());
        Notification notifi = new Notification( event.user(),"권한이 변경되었습니다.",
                ""+event.oldRole()+" -> "+event.newRole());
        notificationRepository.save(notifi);

    }



    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {

    }


}
