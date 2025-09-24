package com.sprint.mission.discodeit.log;

public final class LogUtils {

  private LogUtils() {
  }

  public static String maskEmail(String email) {
    if (email == null) {
      return null;
    }
    int at = email.indexOf('@');
    if (at <= 1) {
      return "***";
    }
    return email.substring(0, 2) + "***" + email.substring(at);
  }
}
