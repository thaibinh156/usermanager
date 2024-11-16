package com.infodation.userservice.models.dto.user;

import com.infodation.userservice.models.Sex;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    @Valid
    @NotNull(message = "UserId is required")
    @NotBlank(message = "UserId Name is required")
    private String userId;
    @NotNull(message = "First name is required")
    @NotBlank(message = "first Name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Sex is required")
    private Sex sex;
}
