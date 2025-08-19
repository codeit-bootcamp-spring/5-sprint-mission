package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary // 같은 타입 서비스가 여러 개면 우선 적용
@RequiredArgsConstructor // final 필드 기반 생성자 자동 생성 (this 생략 가능)

public class FileMessageService implements MessageService {


    private final MessageRepository repository;
    private final UserService userService; // 메세지 생성 전 유저 존재 위해 주입
    private final BinaryContentRepository binaryContentRepository; // 메세지 삭제시 첨부파일 삭제 위해 주입

    @Override
    public Message create(MessageCreateRequest request, MultipartFile file) {

        //1. 보낸 유저가 존재하는지 확인
        Optional<User> sender = userService.findEntityById(request.getSender());
        if (sender.isEmpty()) {
            throw new IllegalArgumentException("보낸 유저가 존재하지 않습니다.");
        }

        //2. Message 객체 생성
        Message message = new Message(
                request.getContent(),
                request.getSender(),
                request.getChannelId()
        );

        //3. 첨부파일이 있다면 추가
        if (request.getAttachmentIds() != null) {
            message.setAttachmentIds(request.getAttachmentIds());

        }
        repository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 메세지 ID가 null입니다.");
        }
        Message original = repository.findById(id);
        if (original == null) {
            throw new IllegalArgumentException("존재하지 않는 메세지입니다.");
        }
        return new Message(original); // 복사본 리턴
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return repository.findAll().stream() //repo에 모든 message 객체를 리스트로 가져옴
                .filter(m -> m.getChannelId().equals(channelId)) //m으로 하나하나 꺼낸뒤 파라미터의 ChannelId와 같은 경우만 남김
                .toList(); // 필터링 결과 리스트 반환
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("수정 요청이 null입니다.");
        }

        UUID messageId = request.getId();
        if (messageId == null) {
            throw new IllegalArgumentException("수정할 메시지 ID가 null입니다.");
        }

        Message original = repository.findById(messageId);
        if (original == null) {
            throw new IllegalArgumentException("해당 ID의 메시지가 존재하지 않습니다.");
        }
        // 내용 수정
        original.setContent(request.getContent());

        // 수정 시간 업데이트
        original.updateTime();

        // 저장
        repository.update(original);
        return original;
    }

    @Override
    public void delete(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("삭제할 메세지가 null입니다.");
        }
        Message original = repository.findById(messageId);
        if (original == null) {
            throw new IllegalArgumentException("삭제할 메세지가 존재하지 않습니다.");
        }

        //1. 첨부파일 먼저 삭제
        if (original.getAttachmentIds() != null && !original.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : original.getAttachmentIds()) {
                binaryContentRepository.deleteByOwnerId(attachmentId);
            }
        }
        //2. 메세지 삭제
        repository.delete(messageId);
    }
}
