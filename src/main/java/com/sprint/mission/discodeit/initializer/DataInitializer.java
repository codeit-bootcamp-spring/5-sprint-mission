package com.sprint.mission.discodeit.initializer;

import com.sprint.mission.discodeit.domain.entitydev.DevUser;
import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuild;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class DataInitializer implements ApplicationRunner {

    private final DevUserRepository userRepository;
    private final DevGuildRepository guildRepository;

    private UUID u1;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedGuild();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        u1 = userRepository.save(new DevUser("a@a.aa", "user1", "1111aaaa",
                LocalDate.of(1995, 4, 10), true, "globalName1")).getId();
        userRepository.save(new DevUser("b@b.bb", "user2", "2222bbbb",
                LocalDate.of(1995, 4, 11), false, "globalName2"));
        userRepository.save(new DevUser("c@c.cc", "user3", "3333cccc",
                LocalDate.of(1995, 3, 11), false, "globalName3"));
    }

    private void seedGuild() {
        if (guildRepository.count() > 0) return;

        if (u1 == null) {
            UUID ownerId = userRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No users available for guild owner."))
                    .getId();
        }
        
        guildRepository.save(new DevGuild("Test Guild", true, u1));
    }
}
