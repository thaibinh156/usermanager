package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    Page<User> getAll(Pageable pageable);
    User getByUserId(String userId);
    User save(CreateUserDTO user);
    User update(String userId,UpdateUserDTO user);
    void delete(String userId);
}
