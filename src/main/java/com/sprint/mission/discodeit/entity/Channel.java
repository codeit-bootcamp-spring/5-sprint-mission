package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.SoftDeletable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements SoftDeletable {

    // 소프트 삭제 여부 (true면 삭제된 상태)
    private boolean deleted = false;

    private final UUID id;                                      // 고유 아이디
    private final Long createdAt;                               // 생성일
    private final List<UUID> userIds = new ArrayList<>();       // 채널 참가한 유저의 UUID를 담을 수 있는 리스트
    private final List<UUID> messageIds = new ArrayList<>();    // 채널에 작성된 메세지의 UUID를 담을 수 있는 리스트
    private Long updatedAt;                                     // 정보 업데이트일
    private String name;                                        // 채널명
    private String description;                                 // 채널소개

    // 생성자, count는 채널 내의 유저 수로 처음 만들 때는 1이라 가정
    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.description = description;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public List<UUID> getUserIds() { return userIds; }
    public List<UUID> getMessageIds() { return messageIds; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCount() { return userIds.size(); }        // 채널의 유저수는 userIds.size()로 가능하니 기존 count필드는 삭제

    // 정보 업데이트
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 유저 추가
    public boolean addUser(UUID userId) {
        if (this.userIds.contains(userId)) {
            return false;
        }

        userIds.add(userId);
        this.updatedAt = System.currentTimeMillis(); // 유저 추가할 때도 시간 업데이트
        return true;
    }

    // 유저 제거
    public boolean removeUser(UUID userId) {
        if (!this.userIds.contains(userId)) {
            return false;
        }

        userIds.remove(userId);
        this.updatedAt = System.currentTimeMillis();
        return true;
    }

    // 메세지 아이디 추가
    public void addMessage(UUID messageId) {
        messageIds.add(messageId);
        this.updatedAt = System.currentTimeMillis();
    }

    // 소프트 삭제 여부 반환
    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    // 소프트 삭제 처리
    @Override
    public void delete() {
        deleted = true;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count =" + userIds.size() +
                ", userIds=" + userIds +
                '}';
    }
}
