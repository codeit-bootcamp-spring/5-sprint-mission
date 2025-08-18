package com.sprint.mission.discodeit.support;

import java.util.regex.Pattern;

public final class PhoneNumbers {

  private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{7,14}$");

  public static String normalizeToE164(String input) {
    String v = (input == null) ? null : input.replaceAll("[\\s-]", "");
    if (v == null || !E164.matcher(v).matches()) {
      throw new IllegalArgumentException("전화번호는 E.164(+숫자 8~15자리) 형식이어야 합니다.");
    }
    return v;
  }
}
