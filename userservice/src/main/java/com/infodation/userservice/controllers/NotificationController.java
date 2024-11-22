package com.infodation.userservice.controllers;

import com.infodation.userservice.models.notimodel.CreateNotificationDTO;
import com.infodation.userservice.models.notimodel.Notifications;
import com.infodation.userservice.services.iservice.INotificationService;
import com.infodation.userservice.utils.ApiResponse;
import com.infodation.userservice.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Notifications>> create(@Valid @RequestBody CreateNotificationDTO notificationDTO) {
        logger.info("Creating notification for user with ID: {}", notificationDTO.getUserId());
        Notifications notification = notificationService.saveNoti(notificationDTO);
        HttpStatus status = HttpStatus.CREATED;
        String message = "The notification has been created";
        logger.info("User with ID {} received this notification", notificationDTO.getUserId());

        // Log the raw response to check the structure
        ApiResponse<Notifications> response = ApiResponseUtil.buildApiResponse(notification, status, message, null);
        logger.info("Response to return: {}", response);  // Debugging the response

        return new ResponseEntity<>(response, status);
    }

}
