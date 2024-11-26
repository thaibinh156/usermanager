package com.infodation.userservice.services.iservice;

import com.infodation.userservice.models.TaskDTO.TaskUserResponseDTO;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IUserService {
    TaskUserResponseDTO getUserWithTasks(String userId);
    Page<User> getAll(Pageable pageable, String name);
    User getByUserId(String userId);
    User save(CreateUserDTO user);
    User update(String userId,UpdateUserDTO user);
    void delete(String userId);
    CompletableFuture<Void> bulkEditUsersAsync(List<UpdateUserDTO> usersDTO);
    CompletableFuture<Void> importUsersFromCsvAsync(MultipartFile file) throws IOException;
}
