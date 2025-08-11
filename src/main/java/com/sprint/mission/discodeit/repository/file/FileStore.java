package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.util.*;

/**
 * FileStore는 파일을 통해 Map<UUID, T> 형태의 데이터를 직렬화/역직렬화하여 저장/불러오는 기능을 제공
 * <p>
 * 도메인별 파일 기반 저장소(FileUserRepository 등)의 공통 부모 클래스
 * @param <T> 저장할 도메인 타입 (예: User, Channel, Message 등)
 */
public abstract class FileStore<T> {

    // 저장할 파일의 경로 (예: "data/user.store")
    private final String filePath;

    protected FileStore(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 파일에서 데이터를 읽어 Map<UUID, T> 형태로 반환
     * <p>
     * 파일이 없거나 문제가 발생하면 null 반환
     * @return 파일에서 읽은 Map 데이터 또는 비어 있는 Map
     */
    @SuppressWarnings("unchecked")
    protected Map<UUID, T> loadFromFile() {
        File file = new File(filePath);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("디렉토리 생성 실패: " + file.getParentFile().getAbsolutePath());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<UUID, T>) obj;
            }
        } catch (InvalidClassException e) {
            System.err.println("[직렬화 실패] 클래스 버전 불일치: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[파일 로딩 오류] (" + file.getPath() + ") " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * 전달받은 Map<UUID, T> 데이터를 파일에 직렬화하여 저장합니다.
     * @param map 저장할 데이터
     */
    protected void saveToFile(Map<UUID, T> map) {
        try {
            // 파일 경로에 디렉토리가 없을 수 있으므로 디렉토리 생성
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // "data" 디렉토리 생성

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
