package com.infodation.userservice.models.notimodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {
    @NotNull(message = "User ID is required")
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Message is required")
    @NotBlank(message = "Message is required")
    private String message;
}
