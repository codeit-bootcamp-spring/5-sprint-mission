package com.sprint.mission.discodeit.util;

import java.io.*;
import java.nio.file.Path;

public class FileUtils {

    public static void writeObjectToFile(Object obj, Path path) {
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + path, e);
        }
    }
}

