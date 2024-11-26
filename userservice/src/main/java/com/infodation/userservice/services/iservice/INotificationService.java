package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.dto.notification.CreateNotificationDTO;
import com.infodation.userservice.models.Notifications;

public interface INotificationService {
    Notifications saveNotification(CreateNotificationDTO noti);

}
