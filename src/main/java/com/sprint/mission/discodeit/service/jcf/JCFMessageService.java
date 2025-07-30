package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message; // Message 도메인 모델 import
import com.sprint.mission.discodeit.service.ChannelService; // ChannelService 인터페이스 import
import com.sprint.mission.discodeit.service.MessageService; // MessageService 인터페이스 import
import com.sprint.mission.discodeit.service.UserService; // UserService 인터페이스 import
import java.util.ArrayList; // ArrayList 클래스 import
import java.util.List; // List 인터페이스 import
import java.util.Map; // Map 인터페이스 import
import java.util.Optional; // Optional 클래스 import
import java.util.UUID; // UUID 클래스 import
import java.util.concurrent.ConcurrentHashMap; // ConcurrentHashMap 클래스 import


//Java Collections Framework (JCF)를 활용하여 Message 데이터를 관리하는 MessageService 구현체입니다.
//데이터는 메모리(Map)에 저장됩니다.
//심화 요구사항에 따라 User와 Channel 서비스에 의존하여 메시지 생성 시 유효성을 검증합니다.
public class JCFMessageService implements MessageService {

    // Message 데이터를 저장할 Map
    private final Map<UUID, Message> data;
    private final UserService userService; // User 서비스에 대한 의존성
    private final ChannelService channelService; // Channel 서비스에 대한 의존성


    //JCFMessageService의 생성자입니다.
    //데이터 저장을 위한 Map을 초기화하며, User 및 Channel 서비스의 인스턴스를 주입받습니다.
    //@param userService    사용자 서비스 인스턴스
    //@param channelService 채널 서비스 인스턴스
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new ConcurrentHashMap<>(); // ConcurrentHashMap을 사용하여 스레드 안전성 확보
        this.userService = userService;
        this.channelService = channelService;
    }


    //새로운 Message를 생성하고 Map에 저장합니다.
    //메시지 생성 시, 연관된 사용자(User)와 채널(Channel)의 존재 여부를 검증합니다.
    //@param message 생성할 Message 객체
    //@return 생성된 Message 객체 (유효하지 않은 User 또는 Channel ID인 경우 null)
    @Override
    public Message create(Message message) {
        if (message == null || data.containsKey(message.getMessageId())) {
            System.err.println("오류: Message 생성에 실패했습니다. Message가 null이거나 MessageID가 이미 존재합니다: " + (message != null ? message.getMessageId() : "null"));
            return null;
        }

        // 심화 요구사항: 메시지 생성 시 User와 Channel의 존재 여부 검증
        if (!userService.findById(message.getUserId()).isPresent()) {
            System.err.println("오류: Message 생성에 실패 했습니다. User ID: " + message.getUserId() + "가 존재하지 않습니다.");
            return null;
        }
        if (!channelService.findById(message.getChannelId()).isPresent()) {
            System.err.println("오류: Message 생성에 실패 했습니다. Channel ID: " + message.getChannelId() + "가 존재하지 않습니다.");
            return null;
        }

        data.put(message.getMessageId(), message); // Map에 Message 객체 저장
        System.out.println("메세지 생성: " + message);
        return message;
    }


    //주어진 ID에 해당하는 Message를 Map에서 조회합니다.
    //@param id 조회할 Message의 UUID
    //@return 해당 ID의 Message 객체 (존재하지 않으면 Optional.empty())
    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageId== null) {
            System.err.println("오류: findById에 실패했습니다.. Message ID가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(messageId)); // Map에서 ID에 해당하는 Message를 찾아 Optional로 반환
    }


    //Map에 저장된 모든 Message 객체를 조회합니다.
    //@return 모든 Message 객체의 리스트
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values()); // Map의 모든 값(Message 객체)들을 ArrayList로 변환하여 반환
    }


    //주어진 ID에 해당하는 Message를 Map에서 업데이트합니다.
    //Message 객체의 update 메서드를 호출하여 필드를 수정하고, updatedAt을 업데이트합니다.
    //@param id 업데이트할 Message의 UUID
    //@param updatedMessage 업데이트할 내용을 담은 Message 객체 (id, createdAt, userId, channelId 제외)
    //@return 업데이트된 Message 객체 (존재하지 않으면 Optional.empty())
    @Override
    public Optional<Message> update(UUID messageId, Message updatedMessage) {
        if (messageId == null || updatedMessage == null) {
            System.err.println("오류: Message 업데이트에 실패했습니다. Message ID 또는 updatedMessage가 null입니다.");
            return Optional.empty();
        }
        return Optional.ofNullable(data.get(messageId))
                .map(existingMessage -> {
                    // 기존 Message 객체의 update 메서드를 호출하여 필드 업데이트
                    existingMessage.updateMessage(updatedMessage.getContent());
                    System.out.println("Message 업데이트: " + existingMessage);
                    return existingMessage;
                });
    }


    //주어진 ID에 해당하는 Message를 Map에서 삭제합니다.
    //@param messageId 삭제할 Message의 UUID
    //@return 삭제 성공 여부 (true: 성공, false: 실패)
    @Override
    public boolean delete(UUID messageId) {
        if (messageId == null) {
            System.err.println("오류: Message 삭제에 실패했습니다. Message ID가 null입니다.");
            return false;
        }
        Message removedMessage = data.remove(messageId); // Map에서 ID에 해당하는 Message 삭제
        if (removedMessage != null) {
            System.out.println("Message 삭제: " + removedMessage);
            return true; // 삭제 성공
        }
        System.out.println("Message ID: " + messageId + " 인 Message가 존재하지 않아 삭제 실패하였습니다.");
        return false; // 해당 ID의 Message가 존재하지 않아 삭제 실패
    }
}