package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

import static com.sprint.mission.discodeit.util.Logger.*;

public class JCFMessageService implements MessageService {

    JCFUserService jcfUserService;
    JCFChannelService jcfChannelService;
    Map<UUID, Message> data;

    public JCFMessageService(JCFChannelService jcfChannelService, JCFUserService jcfUserService) {
        this.data = new HashMap<>();
        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
    }

    // 메시지 생성
    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        log("createMessage", String.format(
                "메시지 생성 완료 → ID: %s, 작성자 ID: %s, 채널 ID: %s, 내용: \"%s\"",
                message.getId(),
                message.getUserId(),
                message.getChannelId(),
                message.getContent()
        ));

        return message;
    }

    // 삭제되지 않은 메시지 조회
    @Override
    public Message findById(UUID id, boolean log) {
        Message message = data.get(id);

        if (message != null && !message.isDeleted()) {
            if (log) {
                log("findMessageById", String.format("메시지 조회 성공 → ID: %s, 작성자 ID: %s, 채널 ID: %s, 내용: \"%s\"",
                        message.getId(),
                        message.getUserId(),
                        message.getChannelId(),
                        message.getContent()
                ));

                return message;
            }
        }

        if (log) {
            log("findMessageById", String.format("조회 실패 → ID %s의 메시지가 존재하지 않거나 삭제됨", id));
        }

        return message;
    }

    // 삭제되지 않은 메시지 리스트 반환
    @Override
    public List<Message> findAll() {
        List<Message> result = data.values().stream()
                .filter(message -> !message.isDeleted())
                .toList();

        log("findAllMessage", String.format("전체 메세지 수: %d개", result.size()));
        return result;
    }

    // 메시지 정보 업데이트 (삭제된 메세지는 제외)
    @Override
    public void update(UUID id, String content) {
        Message message = data.get(id);
        if (message != null && !message.isDeleted()) {
            String oldContent = message.getContent();
            message.update(content);
            log("updateMessage", String.format(
                    "메시지 수정 완료 → 내용: \"%s\" → \"%s\"",
                    oldContent, content
            ));
        } else {
            log("updateMessage", String.format(
                    "수정 실패: ID %s의 메시지가 존재하지 않거나 삭제됨", id
            ));
        }
    }

    // 메시지 삭제 (소프트 삭제 적용)
    @Override
    public void delete(UUID id) {
        Message message = data.get(id);
        if (message != null && !message.isDeleted()) {
            message.delete();
            log("deleteMessage", String.format(
                    "메시지 삭제 완료 → ID: %s | 내용: \"%s\"",
                    message.getId(), message.getContent()
            ));
        } else {
            log("deleteMessage", String.format(
                    "삭제 실패: ID %s의 메시지가 존재하지 않거나 이미 삭제됨", id
            ));
        }
    }

    // 삭제 여부와 무관하게 메시지 존재 여부 확인
    public boolean exists(UUID id) {
        return data.containsKey(id);
    }
}