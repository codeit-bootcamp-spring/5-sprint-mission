package com.sprint.mission.discodeit.support;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

  public static String requireNonBlank(String s, String msg) {
    if (s == null || s.isBlank()) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }

  public static String nullOrStripAndLowerCase(String s) {
    return (s == null) ? null : s.strip().toLowerCase(Locale.ROOT);
  }

  public static String nullOrStrip(String s) {
    return (s == null) ? null : s.strip();
  }

  public static String blankOrStrip(String s) {
    return (s == null) ? "" : s.strip();
  }
}
