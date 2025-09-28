package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private TestEntityManager em;

    private User createUser(UserCreateRequest userCreateRequest,
                            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest){


        return User.builder()
                .username(userCreateRequest.username())
                .email(userCreateRequest.email())
                .password(userCreateRequest.password())
                .build();
    }

    @Test
    @DisplayName("findAllWithProfileAndStatus")
    void findAllWithProfileAndStatusTest(){
        User user = userRepository.save(createUser(new UserCreateRequest("bus","bus@sdf.com","bus12"),null));
        UserStatus status = new UserStatus(user , Instant.now());
        em.flush();
        em.clear();
        List<User> found=userRepository.findAllWithProfileAndStatus();
        assertThat(found).isNotEmpty();



    }


}
