package com.sprint.mission.discodeit.initializer;

import com.sprint.mission.discodeit.domain.deventity.DevUser;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DevUserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            userRepository.save(new DevUser("a@a.aa", "user1", "1111aaaa", LocalDate.of(1995, 4, 10), true, "globalName1"));
            userRepository.save(new DevUser("b@b.bb", "user2", "2222bbbb", LocalDate.of(1995, 4, 11), false, "globalName2"));
            userRepository.save(new DevUser("c@c.cc", "user3", "3333cccc", LocalDate.of(1995, 3, 11), false, "globalName3"));
        }
    }
}
