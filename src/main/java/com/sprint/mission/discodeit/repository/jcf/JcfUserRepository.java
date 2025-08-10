package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class JcfUserRepository extends JcfBaseRepository<User> implements UserRepository {

    @Override
    protected String getEntityTypeName() {
        return "User";
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) return false;
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public List<User> searchByEmail(String email) {
        if (email == null || email.isBlank()) return List.of();
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getEmail().toLowerCase().startsWith(email.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchByUsername(String username) {
        if (username == null || username.isBlank()) return List.of();
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getUsername().toLowerCase().startsWith(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchByGlobalName(String globalName) {
        if (globalName == null || globalName.isBlank()) return List.of();
        return data.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getUsername().toLowerCase().startsWith(globalName.toLowerCase()))
                .collect(Collectors.toList());
    }
}
