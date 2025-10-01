package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// User 엔티티에 대한 DB 접근을 담당하는 Repository 인터페이스
// JpaRepository<User, UUID>를 상속받아 기본 CRUD 메서드 제공
public interface UserRepository extends JpaRepository<User, UUID> {

    // username(로그인 아이디)으로 사용자 조회
    Optional<User> findByUsername(String username);

    // 이메일 중복 여부 확인
    boolean existsByEmail(String email);

    // username 중복 여부 확인
    boolean existsByUsername(String username);

    // 모든 사용자 조회 시 프로필(profile)과 상태(status)를 함께 가져오는 쿼리
    // - fetch join을 사용하여 N+1 문제 방지
    @Query("SELECT u FROM User u "
            + "LEFT JOIN FETCH u.profile "
            + "JOIN FETCH u.status")
    List<User> findAllWithProfileAndStatus();
}
