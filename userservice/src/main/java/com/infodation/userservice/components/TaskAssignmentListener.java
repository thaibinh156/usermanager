package com.infodation.userservice.components;

import com.infodation.userservice.mapper.NotificationMapper;
import com.infodation.userservice.models.TaskDTO.TaskAssignmentDTO;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.notification.CreateNotificationDTO;
import com.infodation.userservice.repositories.NotificationRepository;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.INotificationService;
import com.infodation.userservice.models.Notifications;
import com.mysql.cj.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskAssignmentListener {

    private static final Logger logger = LoggerFactory.getLogger(TaskAssignmentListener.class);

    private final NotificationRepository notiRepository;
    private final UserRepository userRepository;

    public TaskAssignmentListener(NotificationRepository notiRepository, UserRepository userRepository) {
        this.notiRepository = notiRepository;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "sendNotification")
    public void handleNotification(TaskAssignmentDTO taskAssignmentDTO) {
        try {
            // Query user information from the User table
            Optional<User> userOptional = userRepository.findById(Long.valueOf(taskAssignmentDTO.getUserId()));
            User user = userOptional.get();
            logger.info("Found user: {}", user.getUserId());

            // Create CreateNotificationDTO from TaskAssignmentDTO
            CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
            notificationDTO.setUserId(user.getUserId());
            notificationDTO.setMessage("Task " + taskAssignmentDTO.getTaskId() + " has been assigned to you.");

            // Convert CreateNotificationDTO to Notifications (if needed)
            Notifications notification = NotificationMapper.INSTANCE.createNotificationDTOToNotification(notificationDTO);
            notification.setUser(user);

            // Save the notification to the database
            notiRepository.save(notification);
            logger.info("Notification saved successfully for user: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("Error processing notification for task: {}", taskAssignmentDTO.getTaskId(), e);
        }
    }
}
