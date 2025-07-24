package com.sprint.mission.discodeit.utility;

import com.sprint.mission.discodeit.exception.ValidationException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

public class InputHandler {
  private static final Scanner sc = new Scanner(System.in);

  private InputHandler() {}

  public static int getMenuInput(int maxNum, String prompt) {
    while (true) {
      try {
        System.out.print(prompt);
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
          System.out.println("y 또는 n을 입력해주세요.");
      }
    }
  }

  public static String getValidEmail(String prompt) {
    while (true) {
      String email = getInputOrBack(prompt);
      if (email == null) {
        return null;
      }

      try {
        return Validators.validateEmail(email);
      } catch (ValidationException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public static String getValidPassword(String prompt) {
    while (true) {
      String password = getInputOrBack(prompt);
      if (password == null) {
        return null;
      }

      try {
        return Validators.validatePassword(password);
      } catch (ValidationException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public static LocalDate getValidDate(String prompt) {
    while (true) {
      try {
        System.out.println(prompt);
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
