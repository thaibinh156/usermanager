package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.User;

import java.util.List;

public interface IUserService {
    List<User> getAll();
    User getById(Long id);
    User save(User user);
    User update(User user);
    void delete(Long id);
}
