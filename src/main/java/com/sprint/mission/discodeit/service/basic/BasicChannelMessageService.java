package com.sprint.mission.discodeit.service.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.UserService;

public class BasicChannelMessageService implements ChannelMessageService{
	private final MessageRepository messageRepository;
	private final UserService userService;
	// BaicChannelService인 구현체로 선언한 이유는 안에
	// saveChannel이라는 Basic에서만 사용되는 메서드가 있기 때문에
	private final BasicChannelService channelService;

	public BasicChannelMessageService(MessageRepository messageRepository, UserService userService
		, BasicChannelService channelService) {
		this.messageRepository = messageRepository;
		this.userService = userService;
		this.channelService = channelService;
	}

	@Override
	public void createMessage(UUID authorUUID, UUID channelUUID, String text) {
		User user = userService.getUserById(authorUUID);
		Channel channel = channelService.getChannelByUUID(channelUUID);

		if (!channel.getChannelUsersUUID().contains(authorUUID)) {
			throw new NotChannelMemberException();
		}

		Message newMessage = new Message(authorUUID, channelUUID, text);
		messageRepository.save(newMessage);

		channel.addMessage(newMessage.getId());
		channel.updateUpdatedAt();
		channelService.saveChannel(channel);
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		Channel channel = channelService.getChannelByUUID(channelUUID);

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
		Message message = messageRepository.findById(messageUUID);
		if (message == null) {
			throw new MessageNotFoundException(messageUUID);
		}

		if (!message.getAuthorUUID().equals(authorUUID)) {
			throw new UnauthorizedMessageAccessException();
		}

		messageRepository.deleteById(messageUUID);

		Channel channel = channelService.getChannelByUUID(message.getChannelUUID());
		channel.removeMessage(messageUUID);
		channel.updateUpdatedAt();
		channelService.saveChannel(channel);
	}

	@Override
	public void deleteChannelWithMessages(UUID channelUUID) {
		Channel channel = channelService.getChannelByUUID(channelUUID);

		List<Message> channelMessages = messageRepository.findByChannelId(channelUUID);
		for (Message message : channelMessages) {
			messageRepository.deleteById(message.getId());
		}

		channelService.deleteChannel(channelUUID);
	}
}
