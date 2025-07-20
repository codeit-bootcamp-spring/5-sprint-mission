package com.sprint.mission.discodeit.utility;

public final class StringUtil {
  private StringUtil() {}

  public static String normalizeString(String string) {
    return string == null ? "" : string.strip();
  }
}
