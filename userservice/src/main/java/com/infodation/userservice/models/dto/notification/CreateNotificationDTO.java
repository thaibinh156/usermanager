package com.infodation.userservice.models.dto.notification;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {

    @NotNull(message = "User ID is required")
    @NotBlank(message = "User ID is required")
    @JsonAlias("userId")
    private String userId;

    @NotNull(message = "Message is required")
    @NotBlank(message = "Message is required")
    @JsonAlias("message")
    private String message;
}
