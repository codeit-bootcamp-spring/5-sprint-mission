package com.sprint.mission.discodeit.service.alarm;

import java.util.List;

import com.sprint.mission.discodeit.domain.dto.AlarmDTO;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.service.jsf.ChanneluserService;

public class MessageAlarmService {

	static final String sampleAlarmType = "MESSAGE_ALARM";

	private final ChanneluserService channeluserService;
	private final AlarmService alarmService;

	/**
	 * 생성자
	 * @param channeluserService 채널 사용자 서비스
	 * @param alarmService 알람 서비스
	 */
	public MessageAlarmService(ChanneluserService channeluserService, AlarmService alarmService) {
		this.channeluserService = channeluserService;
		this.alarmService = alarmService;
	}

	/**
	 * 새로운 메시지가 도착했을 때 호출되는 메소드
	 * 현재 채널에 비활성화된 사용자들에게 메시지 알림을 전송합니다.
	 * @param message 메시지
	 */
	public void sendMessageAlarm(Message message) {
		// 메시지 알림 전송 로직 구현이 힘들어 콘솔 출력으로 대체
		List<AlarmDTO> alarmList = channeluserService.getChannelUsersByChannel(message.getChannel().getId())
		  .stream()
		  .filter(channelUser -> !channelUser.isActive()) // 비활성화된 사용자 필터링
		  .map(channelUser -> new AlarmDTO("test Alarm message", sampleAlarmType, channelUser.getUserId(),
			message.getChannel().getId())).toList();

		// 알람 전송 메서도 호출
		alarmList.forEach(alarmService::sendAlarm);

	}
}
