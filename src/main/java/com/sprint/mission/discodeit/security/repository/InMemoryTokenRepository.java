package com.sprint.mission.discodeit.security.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

public class InMemoryTokenRepository implements PersistentTokenRepository {

  private final Map<String, PersistentRememberMeToken> tokens = new ConcurrentHashMap<>();

  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    tokens.put(token.getSeries(), token);
  }

  @Override
  public void updateToken(String series, String tokenValue, java.util.Date lastUsed) {
    tokens.computeIfPresent(series, (key, oldToken) ->
        new PersistentRememberMeToken(oldToken.getUsername(), key, tokenValue, lastUsed)
    );
  }

  @Override
  public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    return tokens.get(seriesId);
  }

  @Override
  public void removeUserTokens(String username) {
    tokens.entrySet()
          .removeIf(e -> e.getValue()
                          .getUsername()
                          .equals(username));
  }
}