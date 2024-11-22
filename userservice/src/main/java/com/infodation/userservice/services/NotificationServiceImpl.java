package com.infodation.userservice.services;

import com.infodation.userservice.mapper.NotificationMapper;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.notimodel.CreateNotificationDTO;
import com.infodation.userservice.models.notimodel.Notifications;
import com.infodation.userservice.repositories.NotificationRepository;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.INotificationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final NotificationRepository notiRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notiRepository, UserRepository userRepository) {
        this.notiRepository = notiRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notifications saveNoti(CreateNotificationDTO notiDTO) {
        // Find the user by userId
        Optional<User> userOptional = userRepository.findByUserId(notiDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Get the user from the Optional
        User user = userOptional.get();

        // Use the mapper to convert DTO to entity
        Notifications notification = NotificationMapper.INSTANCE.createNotificationDTOToNotification(notiDTO);

        // Set the user in the notification
        notification.setUser(user);

        // Save the notification
        return notiRepository.save(notification);
    }

}