package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AuthAuditEventType;
import com.sprint.mission.discodeit.entity.AuthAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, UUID> {

    Page<AuthAuditLog> findByUserId(UUID userId, Pageable pageable);

    Page<AuthAuditLog> findByUsername(String username, Pageable pageable);

    Page<AuthAuditLog> findByEventType(AuthAuditEventType eventType, Pageable pageable);

    Page<AuthAuditLog> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable);

    Page<AuthAuditLog> findByIpAddress(String ipAddress, Pageable pageable);
}
