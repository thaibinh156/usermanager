package com.infodation.userservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String sex;
    private String email;

}
