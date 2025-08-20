package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

  private static final Comparator<User> BY_EMAIL =
      Comparator.comparing(User::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
  private static final Comparator<User> BY_USERNAME =
      Comparator.comparing(User::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

  public FileUserRepository(AppProperties appProperties) {
    super(User.class, appProperties.storage());
  }

  private static boolean startsWithIgnoreCase(String value, String prefix) {
    if (value == null || prefix == null) {
      return false;
    }
    int len = prefix.length();
    if (value.length() < len) {
      return false;
    }
    return value.regionMatches(true, 0, prefix, 0, len);
  }

  private static boolean containsIgnoreCase(String value, String needle) {
    if (value == null || needle == null) {
      return false;
    }
    if (needle.isEmpty()) {
      return true;
    }
    final String v = value.toLowerCase();
    final String n = needle.toLowerCase();
    return v.contains(n);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }
    return findAll().stream()
        .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
        .findFirst();
  }

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }
    return findAll().stream()
        .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
        .findFirst();
  }

  @Override
  public boolean existsByEmail(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }
    return findByEmail(email).isPresent();
  }

  @Override
  public boolean existsByUsername(String username) {
    if (username == null || username.isBlank()) {
      return false;
    }
    return findByUsername(username).isPresent();
  }

  private List<User> searchByContains(Function<User, String> extractor, String keyword,
      Comparator<User> order) {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }
    final String q = keyword.strip();
    return findAll().stream()
        .filter(u -> containsIgnoreCase(extractor.apply(u), q))
        .sorted(order)
        .toList();
  }

  private List<User> searchByPrefix(Function<User, String> extractor, String prefix,
      Comparator<User> order) {
    if (prefix == null || prefix.isBlank()) {
      return List.of();
    }
    final String p = prefix.strip();
    return findAll().stream()
        .filter(u -> startsWithIgnoreCase(extractor.apply(u), p))
        .sorted(order)
        .toList();
  }

  @Override
  public List<User> searchByEmailKeyword(String keyword) {
    return searchByContains(User::getEmail, keyword, BY_EMAIL);
  }

  @Override
  public List<User> searchByUsernameKeyword(String keyword) {
    return searchByContains(User::getUsername, keyword, BY_USERNAME);
  }

  @Override
  public List<User> searchByEmailPrefix(String prefix) {
    return searchByPrefix(User::getEmail, prefix, BY_EMAIL);
  }

  @Override
  public List<User> searchByUsernamePrefix(String prefix) {
    return searchByPrefix(User::getUsername, prefix, BY_USERNAME);
  }
}
