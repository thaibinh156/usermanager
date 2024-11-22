package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.notimodel.CreateNotificationDTO;
import com.infodation.userservice.models.notimodel.Notifications;

public interface INotificationService {
    Notifications saveNoti(CreateNotificationDTO noti);

}
