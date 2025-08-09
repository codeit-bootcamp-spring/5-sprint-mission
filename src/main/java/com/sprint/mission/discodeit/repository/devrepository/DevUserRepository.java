package com.sprint.mission.discodeit.repository.devrepository;

import com.sprint.mission.discodeit.domain.entitydev.DevUser;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface DevUserRepository extends BaseRepository<DevUser> {

    Optional<DevUser> findByEmail(String email);

    Optional<DevUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<DevUser> searchByEmail(String email);

    List<DevUser> searchByUsername(String username);

    List<DevUser> searchByGlobalName(String globalName);
}
