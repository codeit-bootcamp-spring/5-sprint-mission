package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class JpaMessageRepository extends JpaBaseRepository<ProdMessage> implements com.sprint.mission.discodeit.repositoryprod.ProdMessageRepository {

    protected JpaMessageRepository() {
        super(ProdMessage.class);
    }
}
