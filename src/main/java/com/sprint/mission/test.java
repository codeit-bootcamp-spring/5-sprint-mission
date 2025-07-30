package com.sprint.mission;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;

public class test {
    public static void main(String[] args) {
        FileUserRepository fileUserRepository = new FileUserRepository();
        fileUserRepository.deleteAll();
    }
}
