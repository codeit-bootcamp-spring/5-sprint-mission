package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.DeleteMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByAuthorRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.message.DeleteMessageResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
	// MessagelUUID // Message
	private final Map<UUID, Message> messageMap;
	// 참조
	private final UserService userService;
	private final ChannelService channelService;

	public JCFMessageService(UserService userService, ChannelService channelService) {
		messageMap = new ConcurrentHashMap<>();
		this.userService = userService;
		this.channelService = channelService;
	}

	@Override
	public boolean createMessage(UUID authorUUID, UUID channelUUID, String text) {
		if(authorUUID == null || channelUUID == null || text == null) return false;

		// User user = userService.getUserById(authorUUID);
		Channel channel = channelService.getChannelByUUID(channelUUID);
		// if(user == null || channel == null) return false;

		if(!channel.getMemberIds().contains(authorUUID)) {
			return false;
		}

		Message message = new Message(authorUUID,channelUUID, text);

		messageMap.put(message.getId(), message);

		return true;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return messageMap.get(messageUUID);
	}

	@Override
	public List<Message> getAllMessages() {
		return new ArrayList<Message>(messageMap.values());
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		Channel channel = channelService.getChannelByUUID(channelUUID);
		if(channel == null) return null;

		List <Message> messageList = new ArrayList<>();

		// nickName으로 User UUID 구하기
		UUID targetUserUUID = null;
		for (Map.Entry<UUID, String> entry : channel.getUserNicknames().entrySet()) {
			if (targetAuthor.equals(entry.getValue())) {
				targetUserUUID = entry.getKey();
				break;
			}
		}
		if(targetUserUUID == null) return new ArrayList<>();

		// UUID로 비교해서 메시지가 있으면 list에 추가
		for (Message message : messageMap.values()) {
			if (message.getChannelId().equals(channelUUID) &&
				message.getAuthorId().equals(targetUserUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public List<Message> getMessageByChannel(UUID channelUUID) {
		List <Message> messageList = new ArrayList<>();

		for (Message message : messageMap.values()) {
			if (message.getChannelId().equals(channelUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public boolean updateMessage(UUID messageUUID, UUID authorUUID, String text) {
		if (messageUUID == null || authorUUID == null || text == null) return false;

		Message message = messageMap.get(messageUUID);
		if (!message.getAuthorId().equals(authorUUID)) return false;

		message.updateText(text);
		message.updateUpdatedAt();

		return true;
	}

	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		if (messageUUID == null || authorUUID == null) return;

		Message message = messageMap.get(messageUUID);
		if (message == null || !message.getAuthorId().equals(authorUUID)) return;

		messageMap.remove(messageUUID);

		Channel channel = channelService.getChannelByUUID(message.getChannelId());
	}

	@Override
	public MessageResponse createMessage(CreateMessageRequest request) {
		return null;
	}

	@Override
	public MessageResponse getMessage(GetMessageRequest request) {
		return null;
	}

	@Override
	public List<MessageResponse> getAllByChannelId(GetMessagesByChannelIdRequest request) {
		return List.of();
	}

	@Override
	public List<MessageResponse> getMessageByAuthor(GetMessagesByAuthorRequest request) {
		return List.of();
	}

	@Override
	public MessageResponse updateMessage(UpdateMessageRequest request) {
		return null;
	}

	@Override
	public DeleteMessageResponse deleteMessage(DeleteMessageRequest request) {
		return null;
	}
}
