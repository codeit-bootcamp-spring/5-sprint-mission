package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ReadStatus rs where rs.user.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    default ReadStatus getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
            new NotFoundException(
                "ReadStatus with id %s not found".formatted(id))
        );
    }
}
