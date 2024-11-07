package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<User> getAll();
    User getById(UUID id);
    User save(User user);
    User update(User user);
    void delete(String id);
}
