package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.LoginDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public LoginDto login(String username, String password) {
        User user = userRepository.findByUserName(username).orElseThrow(()
                -> new IllegalArgumentException("Login: User not found"));
        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("Login: Wrong password");
        }

        byte[] binaryContent = null;
        Optional<BinaryContent> byId = binaryContentRepository.findById(user.getProfileId());
        if(byId.isPresent()){
            binaryContent = byId.get().getContent();
        }

        return new LoginDto(
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                binaryContent
        );
    }
}
