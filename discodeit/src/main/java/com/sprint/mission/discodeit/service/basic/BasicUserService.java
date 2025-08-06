package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.dto.response.UserUpdateResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

//    public BasicUserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public User create(String username, String email, String password) {
        List<User> users=userRepository.findAll();
        if(!users.isEmpty()){
            for(User user:users){
                if(user.getUsername().equals(username) || user.getEmail().equals(email)){
                    System.out.println("username 또는 email이 중복됩니다.");
                    return new User(null,null,null);
                }
            }
        }

        User user = new User(username, email, password);
        userStatusRepository.save(new UserStatus(user.getId()));
        userRepository.save(user);
        return user;
    }

//    @Override
//    public UserCreateResponse create(String username, String email, String password) {
//        List<User> users=userRepository.findAll();
//        if(!users.isEmpty()){
//            for(User user:users){
//                if(user.getUsername().equals(username) || user.getEmail().equals(email)){
//                    System.out.println("username 또는 email이 중복됩니다.");
//                    return new UserCreateResponse(null,null,null);
//                }
//            }
//        }
//
//        User user = new User(username, email, password);
//        userStatusRepository.save(new UserStatus(user.getId()));
//        userRepository.save(user);
//        return new UserCreateResponse(user.getId(),user.getUsername(),user.getEmail());
//    }

//    @Override
//    public UserCreateResponse create(UserCreateRequest userCreateRequest) {
//        List<User> users=userRepository.findAll();
//        if(!users.isEmpty()){
//            for(User user:users){
//                if(user.getUsername().equals(userCreateRequest.username()) || user.getEmail().equals(userCreateRequest.email())){
//                    System.out.println("username 또는 email이 중복됩니다.");
//                    return new UserCreateResponse(null,null,null);
//                }
//            }
//        }
//
//        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
//        userStatusRepository.save(new UserStatus(user.getId()));
//        userRepository.save(user);
//        return new UserCreateResponse(user.getId(),user.getUsername(),user.getEmail());
//    }
//
//    @Override
//    public UserCreateResponse create(UserCreateRequest userCreateRequest, String fileName) {
//        List<User> users=userRepository.findAll();
//        if(!users.isEmpty()){
//            for(User user:users){
//                if(user.getUsername().equals(userCreateRequest.username()) || user.getEmail().equals(userCreateRequest.email())){
//                    System.out.println("username 또는 email이 중복됩니다.");
//                    return new UserCreateResponse(null,null,null);
//                }
//            }
//        }
//        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
//
//        byte[] bytes;
//        try{
//            Path imagePath=Path.of(System.getProperty("user.dir"),fileName);
//            System.out.println(imagePath.toAbsolutePath());
//            bytes= Files.readAllBytes(imagePath);
//            String extension=getFileExtension(fileName);
//            BinaryContent content = new BinaryContent(fileName,extension, (long)bytes.length, bytes );
//            binaryContentRepository.save(content,user);
//        } catch (Exception e) {
//            throw new RuntimeException("파일 읽기 오류: "+fileName,e);
//        }
//
//
//        userStatusRepository.save(new UserStatus(user.getId()));
//        userRepository.save(user);
//        return new UserCreateResponse(user.getId(),user.getUsername(),user.getEmail());
//    }

    @Override
    public User create(UserCreateRequest userCreateRequest) {
        List<User> users=userRepository.findAll();
        if(!users.isEmpty()){
            for(User user:users){
                if(user.getUsername().equals(userCreateRequest.username()) || user.getEmail().equals(userCreateRequest.email())){
                    System.out.println("username 또는 email이 중복됩니다.");
                    return new User(null,null,null);
                }
            }
        }

        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());
        userStatusRepository.save(new UserStatus(user.getId()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User create(UserCreateRequest userCreateRequest, String fileName) {
        List<User> users=userRepository.findAll();
        if(!users.isEmpty()){
            for(User user:users){
                if(user.getUsername().equals(userCreateRequest.username()) || user.getEmail().equals(userCreateRequest.email())){
                    System.out.println("username 또는 email이 중복됩니다.");
                    return new User(null,null,null);
                }
            }
        }
        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());

        byte[] bytes;
        try{
            Path imagePath=Path.of(System.getProperty("user.dir"),fileName);
            System.out.println(imagePath.toAbsolutePath());
            bytes= Files.readAllBytes(imagePath);
            String extension=getFileExtension(fileName);
            BinaryContent content = new BinaryContent(fileName,extension, (long)bytes.length, bytes );
            binaryContentRepository.save(content,user);
        } catch (Exception e) {
            throw new RuntimeException("파일 읽기 오류: "+fileName,e);
        }


        userStatusRepository.save(new UserStatus(user.getId()));
        userRepository.save(user);
        return user;
    }

    @Override
    public UserCreateResponse create(String username, String email, String password,String fileName) {
        List<User> users=userRepository.findAll();
        if(!users.isEmpty()){
            for(User user:users){
                if(user.getId().equals(username) || user.getId().equals(email)){
                    System.out.println("username 또는 email이 중복됩니다.");
                    return new UserCreateResponse(null,null,null);
                }
            }
        }
        User user = new User(username, email, password);

        byte[] bytes;
        try{
            Path imagePath=Path.of(System.getProperty("user.dir"),fileName);
            System.out.println(imagePath.toAbsolutePath());
            bytes= Files.readAllBytes(imagePath);
            String extension=getFileExtension(fileName);
            BinaryContent content = new BinaryContent(fileName,extension, (long)bytes.length, bytes );
            binaryContentRepository.save(content,user);
        } catch (Exception e) {
            throw new RuntimeException("파일 읽기 오류: "+fileName,e);
        }

        userStatusRepository.save(new UserStatus(user.getId()));
        userRepository.save(user);
        return new UserCreateResponse(user.getId(),user.getUsername(),user.getEmail());
    }

    @Override
    public Optional<UserFindResponse> find(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> new UserFindResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        userStatusRepository.findById(user.getId())
                ));
    }

    @Override
    public List<UserFindResponse> findAll() {
        List<User> users=userRepository.findAll();
        List<UserFindResponse> findUsers=new ArrayList<>();
        for(User user:users){
            findUsers.add(new UserFindResponse(user.getId(),user.getUsername(),user.getEmail(),userStatusRepository.findById(user.getId())));
        }
        if(findUsers.size()>0){
            return findUsers;
        }else{
            return new ArrayList<UserFindResponse>();
        }

    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public UserUpdateResponse update(UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userUpdateRequest.Id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userUpdateRequest.Id() + " not found"));
        user.update(userUpdateRequest.username(), userUpdateRequest.email(),userUpdateRequest.password());
        userRepository.save(user);
        return new UserUpdateResponse(userUpdateRequest.Id(),userUpdateRequest.username(), userUpdateRequest.email(),userUpdateRequest.password());
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        binaryContentRepository.deleteById(userId);
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return ""; // 확장자가 없는 경우
    }
}
