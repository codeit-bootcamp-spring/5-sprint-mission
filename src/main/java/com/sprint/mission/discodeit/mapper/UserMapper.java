package com.sprint.mission.discodeit.mapper; // 매퍼 패키지 경로 지정

import com.sprint.mission.discodeit.dto.data.UserDto;        // 사용자 DTO 임포트
import com.sprint.mission.discodeit.dto.data.BinaryContentDto; // 바이너리 DTO 임포트(명시적 사용 시)
import com.sprint.mission.discodeit.entity.User;             // 사용자 엔티티 임포트
import org.springframework.stereotype.Component;             // 스프링 컴포넌트 임포트

@Component                                   // 스프링 빈 등록
public class UserMapper {                    // 사용자 매퍼 클래스 선언
    private final BinaryContentMapper binaryContentMapper; // 프로필 변환용 매퍼 의존성

    public UserMapper(BinaryContentMapper binaryContentMapper) { // 생성자 주입
        this.binaryContentMapper = binaryContentMapper;          // 의존성 필드에 할당
    }

    public UserDto toDto(User e) {                    // User 엔티티를 UserDto로 변환
        if (e == null) return null;                   // 널 입력 방어
        return new UserDto(                           // record 생성 및 반환
                e.getId(),                                // 사용자 ID 매핑
                e.getCreatedAt(),                         // 생성 시각 매핑
                e.getUpdatedAt(),                         // 수정 시각 매핑
                e.getUsername(),                          // 사용자명 매핑
                e.getEmail(),                             // 이메일 매핑
                e.getProfile() == null                    // 프로필 엔티티 존재 여부 확인
                        ? null                                // 없으면 null
                        : binaryContentMapper.toDto(e.getProfile()), // 있으면 BinaryContentDto로 변환
                e.getStatus() == null                     // 상태 엔티티 존재 여부 확인(필드명 가정)
                        ? null                                // 없으면 null
                        : e.getStatus().isOnline()            // 있으면 온라인 여부 반환(메서드명은 엔티티에 맞춰 조정)
        );                                            // DTO 생성 종료
    }
}
