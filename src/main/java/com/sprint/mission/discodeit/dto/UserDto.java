package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import java.util.UUID;

public class UserDto {

    /**
     * 사용자 생성 요청 DTO
     * <p>{@link #name} - 중복 불가</p>
     * <p>{@link #email} - 중복 불가</p>
     * <p>{@link #password} - 비밀번호</p>
     * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
     */
    public record Create(
            String name,
            String email,
            String password,
            @Nullable BinaryContentDto profileImage
    ) {}

    /**
     * 사용자 업데이트 요청 DTO
     * <p>{@link #name} - 중복 불가</p>
     * <p>{@link #email} - 중복 불가</p>
     * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
     */
    public record Update (
        UUID id,
        @Nullable String name,
        @Nullable String email,
        @Nullable BinaryContentDto profileImage
    ) {}

    /**
     * 사용자 조회 응답 DTO
     * <p>{@link #id} - 아이디</p>
     * <p>{@link #name} - 이름</p>
     * <p>{@link #email} - 이메일</p>
     * <p>{@link #online} - 온라인 여부</p>
     */
    public record View(
            UUID id,
            String name,
            String email,
            boolean online
    ) {}

}
