package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {
	private final Map<UUID, Channel> channelMap = new ConcurrentHashMap<>();
	// channelName 으로 UUID를 맵핑 ( Cache )
	private final Map<String, UUID> channelNameToUUID = new ConcurrentHashMap<>();

	@Override
	public boolean createChannel(String channelName) {
		if(channelNameToUUID.containsKey(channelName)) return false;
		Channel channel = new Channel(channelName);

		channelMap.put(channel.getId(), channel);
		channelNameToUUID.put(channelName, channel.getId());

		return true;
	}

	@Override
	public boolean joinChannel(User user, String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		Channel channel = channelMap.get(channelNameToUUID.get(channelName));
		channel.addUser(user.getId());
		channel.addNickname(user.getId(), user.getDefaultNickname());
		return true;
	}

	@Override
	public Channel findChannel(String channelName) {
		return channelMap.get(channelNameToUUID.get(channelName));
	}

	@Override
	public Channel findChannel(UUID channelUUID) {
		return channelMap.get(channelUUID);
	}

	@Override
	public ArrayList<Channel> findChannelAll() {
		return new ArrayList<>(channelMap.values());
	}

	@Override
	public boolean updateChannelName(User user, UUID channelUUID, String channelNewName) {
		// 새로운 이름이 이미 사용 중이거나 업데이트 할 채널이 없을 경우 return false
		if(channelNameToUUID.containsKey(channelNewName) ||
			!channelMap.containsKey(channelUUID)){
			return false;
		}

		Channel channel = channelMap.get(channelUUID);
		channel.updateChannelName(channelNewName);
		channel.updateUpdatedAt();

		deleteChannel(channelUUID);

		channelMap.put(channelUUID, channel);
		channelNameToUUID.put(channelNewName, channel.getId());

		return true;
	}

	@Override
	public boolean updateUserNickname(UUID channelUUID, UUID userUUID, String newNickname) {
		if (newNickname == null || newNickname.isEmpty()) return false;

		Channel channel = findChannel(channelUUID);
		if (channel == null) return false;

		channel.addNickname(userUUID, newNickname);
		channel.updateUpdatedAt();
		return true;
	}

	@Override
	public boolean deleteChannel(UUID channelUUID) {
		if(!channelMap.containsKey(channelUUID)) return false;

		String channelName = channelMap.get(channelUUID).getChannelName();

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		return true;
	}

	@Override
	public boolean deleteChannel(String channelName) {
		if(!channelNameToUUID.containsKey(channelName)) return false;

		UUID channelUUID = channelNameToUUID.get(channelName);

		channelMap.remove(channelUUID);
		channelNameToUUID.remove(channelName);

		return true;
	}

}
