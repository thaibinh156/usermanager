package com.infodation.userservice.services;

import com.infodation.userservice.exception.UserNotFoundException;
import com.infodation.userservice.mapper.NotificationMapper;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.notification.CreateNotificationDTO;
import com.infodation.userservice.models.Notifications;
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
    public Notifications saveNotification(CreateNotificationDTO notiDTO) {
        try {
            // Find the user by userId
            Optional<User> userOptional = userRepository.findByUserId(notiDTO.getUserId());
            if (userOptional.isEmpty()) {
                throw new UserNotFoundException("No user found with userId: " + notiDTO.getUserId());
            }
            // Get the user from the Optional
            User user = userOptional.get();

            // Use the mapper to convert DTO to entity
            Notifications notification = NotificationMapper.INSTANCE.createNotificationDTOToNotification(notiDTO);

            // Set the user in the notification
            notification.setUser(user);

            // Save the notification
            return notiRepository.save(notification);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}