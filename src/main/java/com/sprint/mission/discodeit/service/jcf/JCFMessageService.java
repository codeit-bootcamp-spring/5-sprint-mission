package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
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

		User user = userService.getUser(authorUUID);
		Channel channel = channelService.findChannel(channelUUID);
		if(user == null || channel == null) return false;

		if(!channel.getChannelUsersUUID().contains(authorUUID)) {
			return false;
		}

		Message message = new Message(authorUUID,channelUUID, text);

		messageMap.put(message.getId(), message);
		channel.addMessage(message.getId());

		return true;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return messageMap.get(messageUUID);
	}

	@Override
	public List<Message> getMessageAll() {
		return new ArrayList<Message>(messageMap.values());
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		Channel channel = channelService.findChannel(channelUUID);
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
			if (message.getChannelUUID().equals(channelUUID) &&
				message.getAuthorUUID().equals(targetUserUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public List<Message> getMessageByChannel(UUID channelUUID) {
		List <Message> messageList = new ArrayList<>();

		for (Message message : messageMap.values()) {
			if (message.getChannelUUID().equals(channelUUID)) {
				messageList.add(message);
			}
		}

		return messageList;
	}

	@Override
	public boolean updateMessage(UUID messageUUID, UUID authorUUID, String text) {
		if (messageUUID == null || authorUUID == null || text == null) return false;

		Message message = messageMap.get(messageUUID);
		if (!message.getAuthorUUID().equals(authorUUID)) return false;

		message.updateText(text);
		message.updateUpdatedAt();

		return true;
	}



	@Override
	public boolean deleteMessage(UUID messageUUID, UUID authorUUID) {
		if (messageUUID == null || authorUUID == null) return false;

		Message message = messageMap.get(messageUUID);
		if (message == null || !message.getAuthorUUID().equals(authorUUID)) return false;

		messageMap.remove(messageUUID);

		Channel channel = channelService.findChannel(message.getChannelUUID());
		if (channel != null) {
			channel.removeMessage(messageUUID);
		}

		return true;
	}


}
