package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.SoftDeletable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.Instant.*;

public class User implements SoftDeletable {

    // 소프트 삭제 여부 (true면 삭제된 상태)
    private boolean deleted = false;

    private final UUID id;                                      // 고유 아이디
    private final Long createdAt;                               // 생성일
    private final List<UUID> channelIds =  new ArrayList<>();   // 참가한 채널의 UUID를 담을 수 있는 리스트
    private final List<UUID> messageIds =  new ArrayList<>();   // 작성한 메시지의 UUID를 담을 수 있는 리스트
    private Long updatedAt;                                     // 정보 업데이트일
    private String name;                                        // 이름
    private int age;                                            // 나이
    private String email;                                       // 미션 1 PR에서 추가하길 권고 -> 반영
    private String password;                                    // 미션 1 PR에서 추가하길 권고 -> 반영

    // 생성자
    public User(String name, int age,  String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = now().getEpochSecond();
        this.updatedAt = now().getEpochSecond();
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public List<UUID> getChannelIds() { return channelIds; }
    public List<UUID> getMessageIds() { return messageIds; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public int getAge() { return age; }

    // 정보 업데이트
    public void update(String name, int age) {
        this.name = name;
        this.age = age;
        this.updatedAt = now().getEpochSecond();
    }

    // 채널 참가
    public boolean joinChannel(UUID channelId) {
        if (this.channelIds.contains(channelId)) {
            return false;
        }

        channelIds.add(channelId);
        this.updatedAt = now().getEpochSecond(); // 채널 추가할 때도 시간 업데이트
        return true;
    }

    // 채널 퇴장
    public boolean leaveChannel(UUID channelId) {
        if (!this.channelIds.contains(channelId)) {
            return false;
        }

        channelIds.remove(channelId);
        this.updatedAt = now().getEpochSecond();
        return true;
    }

    // 메시지 아이디 추가
    public boolean addMessage(UUID messageId) {
        if (this.messageIds.contains(messageId)) {
            return false;
        }

        messageIds.add(messageId);
        this.updatedAt = now().getEpochSecond();
        return true;
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
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", channelIds=" + channelIds +
                '}';
    }
}
