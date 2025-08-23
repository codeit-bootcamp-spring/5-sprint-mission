package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfUserRepository extends AbstractJcfRepository<User> implements UserRepository {

  public JcfUserRepository() {
    super(User.class);
  }

  private static final Comparator<User> BY_USERNAME =
      Comparator.comparing(User::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
  private static final Comparator<User> BY_EMAIL =
      Comparator.comparing(User::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }

    return data.values().stream()
        .filter(u -> u.isNotDeleted() && username.equalsIgnoreCase(u.getUsername()))
        .findFirst();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    if (email == null || email.isBlank()) {
      return Optional.empty();
    }

    final String key = email.strip();
    return data.values().stream()
        .filter(u -> u.isNotDeleted() && email.equalsIgnoreCase(u.getEmail()))
        .findFirst();
  }

  @Override
  public boolean existsByUsername(String username) {
    if (username == null || username.isBlank()) {
      return false;
    }

    return findByUsername(username).isPresent();
  }

  @Override
  public boolean existsByEmail(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }

    return findByEmail(email).isPresent();
  }

  @Override
  public List<User> searchByUsernameKeyword(String keyword) {
    return searchByContains(User::getUsername, keyword, BY_USERNAME);
  }

  @Override
  public List<User> searchByEmailKeyword(String keyword) {
    return searchByContains(User::getEmail, keyword, BY_EMAIL);
  }

  @Override
  public List<User> searchByUsernamePrefix(String usernamePrefix) {
    return searchByPrefix(User::getUsername, usernamePrefix, BY_USERNAME);
  }

  @Override
  public List<User> searchByEmailPrefix(String emailPrefix) {
    return searchByPrefix(User::getEmail, emailPrefix, BY_EMAIL);
  }

  private List<User> searchByContains(Function<User, String> extractor, String keyword,
      Comparator<User> order) {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }
    String k = keyword.strip();
    return findAll().stream()
        .filter(u -> containsIgnoreCase(extractor.apply(u), k))
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

  private static boolean containsIgnoreCase(String value, String needle) {
    if (value == null || needle == null) {
      return false;
    }
    if (needle.isEmpty()) {
      return true;
    }
    return value.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
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
}
