package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    @Cacheable(value = "notification", key = "'noti'")
    @Override
    public List<NotificationDto> findNotification() {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
            List<Notification> notifications = notificationRepository.findAllByReceiverId(principal.getUserDto().id().toString());

            List<NotificationDto> dtos = new ArrayList<>();
            for (Notification notification : notifications) {
                dtos.add(notificationMapper.toNotificationDto(notification));
            }
            return dtos;
        } catch(Exception e) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

    }

    @CacheEvict(value ="notification",key = "'noti'")
    @Override
    public void deleteNotification(UUID notificationId) {
        Notification noti = notificationRepository.findById(notificationId).orElseThrow(()->
                new DiscodeitException(ErrorCode.NOTIFICAION_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

        if( Objects.equals(principal.getUserDto().id().toString(), noti.getReceiverId()) ){
            notificationRepository.deleteById(notificationId);
        }
    }
}
