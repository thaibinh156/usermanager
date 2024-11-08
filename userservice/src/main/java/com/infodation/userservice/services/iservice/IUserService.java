package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;

import java.util.List;

public interface IUserService {
    List<User> getAll();
    User getByUserId(String userId);
    User save(CreateUserDTO user);
    User update(String userId,UpdateUserDTO user);
    void delete(String userId);
}
