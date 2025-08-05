package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileUtil {
    private final static String basePath = System.getProperty("user.dir")+"/src/main/java/com/sprint/mission/discodeit/repository/file/data";
    private final static String extension = ".ser";
    private FileUtil(){}

    public static String getBasePath() {
        return basePath;
    }

    public static String getExtension() {
        return extension;
    }

    public static <T extends BaseEntity> void saveEntity(Path path , T entity){
        try{
            Files.createDirectories(path.getParent());

            FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(entity);
        }catch(IOException e){
            System.out.println("FileUtil.saveEntity Error: " + e.getMessage());
        }
    }

    public static <T> Optional<T> loadEntity(Path path, Class<T> type){
        try{
            FileInputStream fileInputStream = new FileInputStream(path.toFile());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object readObject = objectInputStream.readObject();
            if(type.isInstance(readObject)){
                return Optional.of(type.cast(readObject));
            }

            return Optional.empty();
        }catch(IOException | ClassNotFoundException e ){
            System.out.println("FileUtil.loadEntity Error : " + e.getMessage());
        }

        throw new IllegalArgumentException("FileUtil.loadEntity Error");
    }

}
