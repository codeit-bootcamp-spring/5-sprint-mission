package com.sprint.mission.discodeit.utility;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

public class InputHandler {
  private static final Scanner sc = new Scanner(System.in);

  public static int getMenuInput(int maxNum) {
    while (true) {
      try {
        int menuNum = Integer.parseInt(sc.nextLine().strip());
        if (menuNum >= 1 && menuNum <= maxNum) {
          return menuNum;
        }
        System.out.println("올바른 메뉴 번호를 입력해주세요.");
      } catch (NumberFormatException e) {
        System.out.println("숫자를 입력해주세요.");
      }
    }
  }

  public static String getInputOrBack(String prompt) {
    System.out.print(prompt);
    String input = sc.nextLine().strip();
    return input.equalsIgnoreCase("x") ? null : input;
  }

  public static Boolean getYesOrNo(String prompt) {
    while (true) {
      String answer = getInputOrBack(prompt + "(y/n) : ");
      if (answer == null) {
        return null;
      }
      switch (answer.strip().toLowerCase()) {
        case "y":
          return true;
        case "n":
          return false;
        default:
          System.out.println("y 또는 n을 입력해주세요.\n");
      }
    }
  }

  public static String getValidEmail(String prompt) {
    String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    while (true) {
      String email = getInputOrBack(prompt);
      if (email == null) {
        return null;
      }
      if (email.matches(emailPattern)) {
        return email.toLowerCase();
      }
      System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
    }
  }

  public static String getValidPassword(String prompt) {
    String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+]{8,}$";
    while (true) {
      String password = getInputOrBack(prompt);
      if (password == null) {
        return null;
      }
      if (password.matches(passwordPattern)) {
        return password;
      }
      System.out.println("비밀번호는 영문, 숫자, 특수 문자 조합 8자 이상을 입력해 주세요.");
    }
  }

  public static LocalDate getValidDate(String message) {
    while (true) {
      try {
        System.out.println(message);
        String yearStr = getInputOrBack("년 : ");
        if (yearStr == null) {
          return null;
        }
        String monthStr = getInputOrBack("월 : ");
        if (monthStr == null) {
          return null;
        }
        String dayStr = getInputOrBack("일 : ");
        if (dayStr == null) {
          return null;
        }
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);
        return LocalDate.of(year, month, day);
      } catch (NumberFormatException e) {
        System.out.println("숫자 형식이 올바르지 않습니다. 다시 입력해주세요.");
      } catch (DateTimeException e) {
        System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.");
      }
    }
  }

  public static void close() {
    sc.close();
  }
}
