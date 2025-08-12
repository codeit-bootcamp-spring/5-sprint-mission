package com.sprint.mission.discodeit.service.dto.user;

/** 프로필 이미지 업로드용 DTO (BinaryContent 생성에 필요한 메타/데이터) */
public class ProfileImageUpload {
    public String contentType;
    public String originalName;
    public long size;
    public String storageKey;
    public byte[] data; // 외부 저장만 쓰면 null 가능
}
