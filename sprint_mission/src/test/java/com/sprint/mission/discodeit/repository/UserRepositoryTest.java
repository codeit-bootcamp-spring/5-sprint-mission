package com.sprint.mission.discodeit.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing // 생성일/수정일 자동 채우기
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    private User createUser(String username, String email) {
        return new User(username,email,"1234",null);
    }

    @Test
//    Optional<User> findByUsername(String username);
    void findByUsernameTest(){
        // given : 테스트에 필요한 테스트셋을 만드는 단계
        userRepository.save(createUser("test1","test1@test.com"));
        em.flush(); //강제로 commit을 수행하는 단계
        em.clear(); //캐시를 비우는 명령

        // when: 테스트 대상을 찾아오는 단계
        Optional<User> found = userRepository.findByUsername("test1");

        // then: 테스트 검증을 수행하는 단계
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("test1");
        assertThat(found.get().getEmail()).isEqualTo("test1@test.com");
        // test를 수행하면 자동으로 rollback 실행
    }

//    boolean existsByEmail(String email);
    @Test
    void findByEmailTest(){
        //given
        userRepository.save(createUser("test1","test1@test.com"));
        em.flush();
        em.clear();

        //when
        Boolean result = userRepository.existsByEmail("test1@test.com");

        //then
        assertThat(userRepository.existsByEmail("test1@test.com")).isTrue();

    }

//    List<User> findAllWithProfileAndStatus();
    @Test
    void findAllWithProfileAndStatusTest(){
// BinaryContent(profile) 생성
        BinaryContent profile = new BinaryContent("avatar.png", 123L, "image/png");
        em.persist(profile); //엔티티를 영속 컨텍스트에 저장


        // User 생성
        User user = new User("testUser", "test@test.com", "1234", profile);
        em.persist(user);

        // UserStatus 생성
        UserStatus status = new UserStatus(user, Instant.now());
        em.persist(status);

        em.flush();
        em.clear();

        // 테스트 대상 호출
        List<User> users = userRepository.findAllWithProfileAndStatus();

        // 검증
        User found = users.get(0);
        assertThat(found.getUsername()).isEqualTo("testUser");
        assertThat(found.getProfile()).isNotNull();
        assertThat(found.getStatus()).isNotNull();
    }


}
