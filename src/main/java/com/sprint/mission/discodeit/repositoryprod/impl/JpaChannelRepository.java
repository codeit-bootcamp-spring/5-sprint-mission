package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.repositoryprod.ProdChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class JpaChannelRepository extends JpaBaseRepository<ProdChannel> implements ProdChannelRepository {

    protected JpaChannelRepository() {
        super(ProdChannel.class);
    }
}
