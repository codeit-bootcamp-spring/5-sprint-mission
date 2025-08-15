package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("dev")
public class FileUserRepository extends FileBaseRepository<User> implements UserRepository {

    public FileUserRepository(AppProperties appProperties) {
        super(User.class, appProperties.storage());
    }

    private static String norm(String s) {
        return s == null ? null : s.strip();
    }

    private static boolean startsWithIgnoreCase(String value, String prefix) {
        if (value == null || prefix == null) return false;
        int len = prefix.length();
        if (value.length() < len) return false;
        return value.regionMatches(true, 0, prefix, 0, len);
    }

    private static final Comparator<User> BY_EMAIL =
            Comparator.comparing(User::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
    private static final Comparator<User> BY_USERNAME =
            Comparator.comparing(User::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
    private static final Comparator<User> BY_GLOBAL_NAME =
            Comparator.comparing(User::getGlobalName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    @Override
    public Optional<User> findByEmail(String email) {
        String key = norm(email);
        if (key == null || key.isBlank()) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(key))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String key = norm(username);
        if (key == null || key.isBlank()) return Optional.empty();
        return findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(key))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        String key = norm(email);
        if (key == null || key.isBlank()) return false;
        return findByEmail(key).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        String key = norm(username);
        if (key == null || key.isBlank()) return false;
        return findByUsername(key).isPresent();
    }

    @Override
    public List<User> searchByEmail(String email) {
        String prefix = norm(email);
        if (prefix == null || prefix.isBlank()) return List.of();
        return findAll().stream()
                .filter(u -> startsWithIgnoreCase(u.getEmail(), prefix))
                .sorted(BY_EMAIL)
                .toList();
    }

    @Override
    public List<User> searchByUsername(String username) {
        String prefix = norm(username);
        if (prefix == null || prefix.isBlank()) return List.of();
        return findAll().stream()
                .filter(u -> startsWithIgnoreCase(u.getUsername(), prefix))
                .sorted(BY_USERNAME)
                .toList();
    }

    @Override
    public List<User> searchByGlobalName(String globalName) {
        String prefix = norm(globalName);
        if (prefix == null || prefix.isBlank()) return List.of();
        return findAll().stream()
                .filter(u -> startsWithIgnoreCase(u.getGlobalName(), prefix))
                .sorted(BY_GLOBAL_NAME)
                .toList();
    }
}
