package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static com.sprint.mission.discodeit.entity.enums.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.enums.ChannelType.PUBLIC;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) { }
}