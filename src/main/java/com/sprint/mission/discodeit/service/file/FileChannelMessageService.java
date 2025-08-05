package com.sprint.mission.discodeit.service.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.UserService;


public class FileChannelMessageService implements ChannelMessageService {
	private final MessageRepository messageRepository;
	private final UserService userService;
	private final ChannelRepository channelRepository; // Repository 직접 사용

	public FileChannelMessageService(@Qualifier("fileMessageRepository") MessageRepository messageRepository, UserService userService,
									 @Qualifier("fileChannelRepository") ChannelRepository channelRepository) {
		this.messageRepository = messageRepository;
		this.userService = userService;
		this.channelRepository = channelRepository;
	}

	@Override
	public void createMessage(UUID authorUUID, UUID channelUUID, String text) {
		// User user = userService.getUserById(authorUUID);
		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);

		// if (user == null || channelOpt.isEmpty()) {
		// 	return; // 또는 예외 던지기
		// }

		Channel channel = channelOpt.get();
		if (!channel.getMemberIds().contains(authorUUID)) {
			return; // 또는 예외 던지기
		}

		Message newMessage = new Message(authorUUID, channelUUID, text);
		messageRepository.save(newMessage);

		channel.addMessage(newMessage.getId());
		channel.updateUpdatedAt();
		channelRepository.save(channel);
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);
		if (channelOpt.isEmpty()) {
			return new ArrayList<>();
		}

		Channel channel = channelOpt.get();
		UUID authorUUID = null;
		for (Map.Entry<UUID, String> entry : channel.getUserNicknames().entrySet()) {
			if (targetAuthor.equals(entry.getValue())) {
				authorUUID = entry.getKey();
				break;
			}
		}

		if (authorUUID == null) {
			return new ArrayList<>();
		}

		List<Message> channelMessages = messageRepository.findByChannelId(channelUUID);
		List<Message> result = new ArrayList<>();

		for (Message message : channelMessages) {
			if (authorUUID.equals(message.getAuthorUUID())) {
				result.add(message);
			}
		}

		return result;
	}

	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		Optional<Message> messageOpt = messageRepository.findById(messageUUID);
		if (messageOpt.isEmpty()) {
			return; // 또는 예외 던지기
		}

		Message message = messageOpt.get();
		if (!message.getAuthorUUID().equals(authorUUID)) {
			return; // 또는 예외 던지기
		}

		messageRepository.deleteById(messageUUID);

		Optional<Channel> channelOpt = channelRepository.findById(message.getChannelUUID());
		if (channelOpt.isPresent()) {
			Channel channel = channelOpt.get();
			channel.removeMessage(messageUUID);
			channel.updateUpdatedAt();
			channelRepository.save(channel);
		}
	}

	@Override
	public void deleteChannelWithMessages(UUID channelUUID) {
		Optional<Channel> channelOpt = channelRepository.findById(channelUUID);
		if (channelOpt.isEmpty()) {
			return;
		}

		List<Message> channelMessages = messageRepository.findByChannelId(channelUUID);
		for (Message message : channelMessages) {
			messageRepository.deleteById(message.getId());
		}

		channelRepository.deleteById(channelUUID);
	}
}