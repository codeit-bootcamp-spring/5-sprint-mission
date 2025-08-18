package com.sprint.mission.discodeit.config;

public record AppStorageProperties(Type type, String rootDir) {

  public enum Type {jcf, file, jpa}
}
