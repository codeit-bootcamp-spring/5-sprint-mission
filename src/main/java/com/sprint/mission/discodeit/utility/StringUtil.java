package com.sprint.mission.discodeit.utility;

public final class StringUtil {
  private StringUtil() {}

  public static String normalizeString(String string) {
    return string == null ? "" : string.strip();
  }

  public static String extractDigits(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll("\\D", "");
  }
}
