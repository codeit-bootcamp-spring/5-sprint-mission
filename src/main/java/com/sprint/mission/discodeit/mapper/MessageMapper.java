package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper; // 첨부 매퍼 의존성
    private final UserMapper userMapper;                   // 사용자 매퍼 의존성

    public MessageMapper(                       // 생성자 주입
       BinaryContentMapper binaryContentMapper,// 첨부 매퍼
       UserMapper userMapper                   // 사용자 매퍼
    ) {
        this.binaryContentMapper = binaryContentMapper; // 필드 세팅
        this.userMapper = userMapper;                   // 필드 세팅
    }

    public MessageDto toDto(Message e) {                    // 엔티티→DTO
        if (e == null) return null;                         // 널 가드
        UserDto author = userMapper.toDto(e.getAuthor());   // 작성자 매핑
        List<BinaryContentDto> atts =                       // 첨부 매핑
                e.getAttachments() == null ? List.of() :        // 널이면 빈 리스트
                        e.getAttachments().stream()                     // 스트림
                                .map(binaryContentMapper::toDto)               // 개별 매핑
                                .collect(Collectors.toList());                 // 수집
        return new MessageDto(                              // record 생성
                e.getId(),                                      // id
                e.getCreatedAt(),                               // 생성 시각
                e.getUpdatedAt(),                               // 수정 시각
                e.getContent(),                                 // 본문
                e.getChannel() != null ? e.getChannel().getId() : null, // 채널 ID
                author,                                         // 작성자 DTO
                atts                                            // 첨부 리스트
        );                                                  // 반환
    }
}
