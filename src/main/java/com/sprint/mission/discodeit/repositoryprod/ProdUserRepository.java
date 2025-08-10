package com.sprint.mission.discodeit.repositoryprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdUser;

import java.util.List;
import java.util.Optional;

public interface ProdUserRepository extends ProdBaseRepository<ProdUser> {

    Optional<ProdUser> findByEmail(String email);

    Optional<ProdUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<ProdUser> searchByEmail(String email);

    List<ProdUser> searchByUsername(String username);

    List<ProdUser> searchByGlobalName(String globalName);
}
