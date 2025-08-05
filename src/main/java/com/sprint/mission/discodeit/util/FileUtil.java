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

    /**
     * IOExcetion 혹은 ClassNotFoundException 외 Exception 발생시
     *  IllegalArgumentException 발생으로 처리하려는것으로 보여요
     * 다만 실제로 try 내부에서 두 Exception외 다른 Exception이 발생하면
     *  아래에 있는 IllegalArgumentException 부분으로 내려오는게 아닌 외부로 바로 Exception을 던지기때문에 현재 구조를 적합하지 않아요
     *
     * 아래와 같은 구조의 코드로 고민해보면 어떨까요
     */
    public static <T> Optional<T> loadEntity(Path path, Class<T> type){
        try{
            FileInputStream fileInputStream = new FileInputStream(path.toFile());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            Object readObject = objectInputStream.readObject();

            if(type.isInstance(readObject)){
                return Optional.of(type.cast(readObject));
            }
        }catch(Exception e ){
            System.out.println("FileUtil.loadEntity Error : " + e.getMessage());

            throw new IllegalArgumentException("FileUtil.loadEntity Error");
        }

        return Optional.empty();

    }

}
