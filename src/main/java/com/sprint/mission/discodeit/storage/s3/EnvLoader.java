package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/*.env 읽는 클래스
 * */

public class EnvLoader {

  public static Properties load() {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    } catch (IOException e) {
      throw new RuntimeException(".env 파일을 찾을 수 없음!", e);
    }
    return props;
  }
}