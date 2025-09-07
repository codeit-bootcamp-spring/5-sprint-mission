package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
public class User extends BaseUpdatableEntity {

    @Column(nullable = false, length = 50) // 사용자명 컬럼
    private String username;               // 사용자명(고유)

    @Column(nullable = false, length = 100) // 이메일 컬럼
    private String email;                   // 이메일(고유)

    @Column(nullable = false, length = 60)  // 비밀번호(해시 저장 가정)
    private String password;                // 패스워드

    /**
     * 사용자 프로필 바이너리 1:1(선택)
     * - 소유측(User)이 FK를 가짐(profile_id)
     * - 삭제/교체 시 orphanRemoval 동작
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // 프로필 교체/삭제 반영
    @JoinColumn(name = "profile_id")                           // FK: profile_id
    private BinaryContent profile;                           // 실제로는 프로필 BinaryContent 엔티티

    /**
     * 사용자 상태 1:1 (부모: User)
     * - UserStatus가 FK(user_id)를 가짐
     * - 라이프사이클을 함께 관리
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    // 기본 생성자 (JPA)
    protected User() { }                                       // 외부에서 직접 생성 막기

    // 필수 필드 생성자
    public User(String username, String email, String password) {
        this.username = username;                              // 사용자명 설정
        this.email = email;                                    // 이메일 설정
        this.password = password;                              // 비밀번호 설정
    }

    // --- 보조 메서드들(업데이트/연관관계 편의) ---

    /** 프로필 교체(없으면 설정, 있으면 교체) */
    public void changeProfile(BinaryContent newProfile) {
        this.profile = newProfile;                           // 단순 대입(1:1 + orphanRemoval=true)
    }

    /** 상태(UserStatus) 부착 - 양방향 연관관계 편의 메서드 */
    public void attachStatus(UserStatus status) {
        this.status = status;                                  // User <-status 세팅
        if (status != null) {                                  // null 체크
            status.setUser(this);                              // 반대편에도 this 설정
        }
    }

    /** 사용자 프로퍼티 업데이트(필요한 항목만 선택적 변경) */
    public void update(String newUsername, String newEmail, String newPassword) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;                       // 사용자명 변경
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;                             // 이메일 변경
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;                       // 비밀번호 변경(해시 가정)
        }
    }
}
