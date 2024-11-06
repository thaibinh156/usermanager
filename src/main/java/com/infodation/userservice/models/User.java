package com.infodation.userservice.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @JsonAlias("id")
    private UUID id;

    @Column(name = "user_id")
    @JsonAlias("userId")
    private String userId;

    @Column(name = "first_name")
    @JsonAlias("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonAlias("lastName")
    private String lastName;

    @Column(name = "email")
    @JsonAlias("email")
    private String email;
}
