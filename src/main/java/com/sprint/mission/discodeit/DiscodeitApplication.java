package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

	static User setupUser(UserService userService) {
		User user = userService.create(new UserDto.CreateUser("woody", "woody@codeit.com", "woody1234"));
		System.out.println("유저 생성 :" + user.getUsername());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel publicChannel = channelService.create(ChannelType.PUBLIC, "공지방", "공지 채널입니다.");
		Channel privateChannel = channelService.create(ChannelType.PRIVATE, "비공개방", "비공개 채널입니다.");
		System.out.println("채널 생성 완료: " + publicChannel.getName() + ", 채널 설명 : " +publicChannel.getDescription());
		System.out.println("채널 생성 완료: " + privateChannel.getName() + ", 채널 설명 : " +privateChannel.getDescription());
		return publicChannel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getContent());
	}

	static void repositoryTest(BinaryContentRepository repository) {
		byte[] bytes;
		try {
			Path imagePath = Path.of(System.getProperty("user.dir"), "image.jpeg");
			System.out.println(imagePath.toAbsolutePath());
			bytes = Files.readAllBytes(imagePath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// String fileName, String contentType, Long size, byte[] bytes
		BinaryContent content = new BinaryContent("image.jpeg","jpeg",(long)bytes.length, bytes);

		BinaryContent savedContent = repository.save(content);

		System.out.println(savedContent.getId() + " : " + savedContent.getContentType() + " : " + savedContent.getSize());
		Optional<BinaryContent> savedContent2 =  repository.findById(savedContent.getId());
		System.out.println(savedContent2.isPresent()); // true?
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 리포지토리 테스트
		BinaryContentRepository repository = context.getBean(BinaryContentRepository.class);
		repositoryTest(repository);


		// 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		// 테스트
		messageCreateTest(messageService, channel, user);
	}
}
