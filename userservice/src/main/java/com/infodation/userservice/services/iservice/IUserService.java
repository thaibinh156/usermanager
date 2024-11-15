package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IUserService {

    Page<User> getAll(Pageable pageable, String name);
    User getByUserId(String userId);
    User save(CreateUserDTO user);
    User update(String userId,UpdateUserDTO user);
    void delete(String userId);
    CompletableFuture<Void> bulkEditUsers(List<UpdateUserDTO> usersDTO);
}
