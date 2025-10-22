package com.sprint.mission.discodeit.stoarge.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Properties;

final class Env {
  private Env() {}

  static Properties loadDotEnv() {
    Properties props = new Properties();
    File envFile = Paths.get(".env").toFile(); // 프로젝트 루트
    if (!envFile.exists()) throw new IllegalStateException(".env 파일을 찾을 수 없습니다(프로젝트 루트).");

    try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String t = line.trim();
        if (t.isEmpty() || t.startsWith("#")) continue;
        int i = t.indexOf('=');
        if (i <= 0) continue;
        String k = t.substring(0, i).trim();
        String v = t.substring(i + 1).trim();
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
          v = v.substring(1, v.length() - 1);
        }
        props.setProperty(k, v);
      }
    } catch (Exception e) {
      throw new RuntimeException(".env 로드 중 오류", e);
    }
    return props;
  }

  static void require(Properties p, String... keys) {
    for (String k : keys) {
      String v = p.getProperty(k);
      if (v == null || v.isBlank()) {
        throw new IllegalStateException("필수 환경변수 누락: " + k);
      }
    }
  }
}
