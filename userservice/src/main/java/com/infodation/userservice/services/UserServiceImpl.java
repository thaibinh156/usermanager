package com.infodation.userservice.services;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    public CompletableFuture<Void> bulkEditUsers(List<UpdateUserDTO> usersDTO) {
        for (UpdateUserDTO userDTO : usersDTO) {
            User userToUpdate = userRepository.findByUserId(userDTO.getUserId()).orElse(null);
            if (userToUpdate != null) {
                userToUpdate.setFirstName(userDTO.getFirstName());
                userToUpdate.setLastName(userDTO.getLastName());
                userToUpdate.setEmail(userDTO.getEmail());
                userToUpdate.setSex(userDTO.getSex());
                userToUpdate.setUpdatedAt(LocalDateTime.now());
                userRepository.save(userToUpdate);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Page<User> getAll(Pageable pageable, String name) {
        String query = Optional.ofNullable(name).orElse("");
        return userRepository.findByName(query, pageable);
    }

    @Override
    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public User save(CreateUserDTO user) {
        User newUser = new User();
        newUser.setUserId(user.getUserId());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setSex(user.getSex());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    @Override
    public User update(String useId, UpdateUserDTO user) {
        User userToUpdate = userRepository.findByUserId(useId).orElse(null);

        if (userToUpdate == null) {
            return null;
        }

        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setSex(user.getSex());
        userToUpdate.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(userToUpdate);
    }

    @Override
    public void delete(String userId) {
        userRepository.deleteByUserId(userId);
    }
}
