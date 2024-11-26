package com.infodation.userservice.mapper;

import com.infodation.userservice.models.dto.notification.CreateNotificationDTO;
import com.infodation.userservice.models.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Notifications createNotificationDTOToNotification(CreateNotificationDTO createNotificationDTO);

}
