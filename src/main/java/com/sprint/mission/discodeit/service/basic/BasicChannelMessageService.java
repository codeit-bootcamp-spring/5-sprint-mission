package com.sprint.mission.discodeit.service.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelMessageService implements ChannelMessageService{
	private final MessageRepository messageRepository;
	private final UserService userService;
	private final BasicChannelService channelService;



	@Override
	public void createMessage(UUID authorUUID, UUID channelUUID, String text) {
		// User user = userService.getUserById(new GetUserByIdRequest(authorUUID));
		Channel channel = channelService.getChannelByUUID(channelUUID);

		if (!channel.getMemberIds().contains(authorUUID)) {
			throw new NotChannelMemberException();
		}

		Message newMessage = new Message(authorUUID, channelUUID, text);
		messageRepository.save(newMessage);

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
			if (authorUUID.equals(message.getAuthorId())) {
				result.add(message);
			}
		}

		return result;
	}

	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		Message message = messageRepository.findById(messageUUID)
			.orElseThrow(() -> new MessageNotFoundException(messageUUID));

		if (!message.getAuthorId().equals(authorUUID)) {
			throw new UnauthorizedMessageAccessException();
		}

		messageRepository.deleteById(messageUUID);

		Channel channel = channelService.getChannelByUUID(message.getChannelId());
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
