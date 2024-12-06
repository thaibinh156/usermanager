package com.infodation.userservice.services;

import com.infodation.userservice.mapper.UserMapper;
import com.infodation.userservice.models.Role;
import com.infodation.userservice.models.TaskDTO.TaskDTO;
import com.infodation.userservice.models.TaskDTO.TaskUserResponseDTO;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.UserDTO;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.IRoleService;
import com.infodation.userservice.services.iservice.IUserService;
import com.opencsv.CSVReader;
import java.util.ArrayList;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final IRoleService roleService;
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RestTemplate restTemplate;

    public UserServiceImpl(UserRepository userRepository, IRoleService roleService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.restTemplate = restTemplate;
    }
    @Value("${task.service.baseUrl}")
    private String taskServiceBaseUrl;

    @Override
    public TaskUserResponseDTO getUserWithTasks(String userId) {
        try {
            // Find the user in the database based on user_id
            User user = userRepository.findByUserId(userId).orElse(null);
            checkUser(userId);
            // Use UserMapper to convert User to UserDTO
            UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(user);

            ResponseEntity<List<TaskDTO>> taskResponse = restTemplate.exchange(
                    taskServiceBaseUrl + "/api/tasks/user/" + user.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TaskDTO>>() {}
            );
            List<TaskDTO> tasks = taskResponse.getBody();

            // Create TaskUserResponseDTO and return it
            TaskUserResponseDTO taskUserResponse = new TaskUserResponseDTO();
            taskUserResponse.setUser(userDTO);
            taskUserResponse.setTasks(tasks); // Set the list of tasks from taskResponse

            return taskUserResponse;

        } catch (Exception e) {
            logger.error("Error while getting user tasks for userId: " + userId, e);
            throw new IllegalArgumentException("Error while retrieving user tasks", e);
        }
    }

    @Value("${user.batch.size}") //value is injected from the application.properties file
    private int batchSize;

    @Async  // Annotation for asynchronous execution
    public CompletableFuture<Void> importUsersFromCsvAsync(MultipartFile file) throws IOException {
        logger.info("Starting CSV import process");
        List<User> usersToSave = new ArrayList<>();
        // Retrieve all existing userIds from the database (for comparison)
        Set<String> userIdsInDbSet = userRepository.findAllUserIds();

        // Read the CSV file
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())))) {
            String[] nextLine;
            boolean isHeader = true;

            Set<Role> roles = new HashSet<>();
            roles.add(roleService.getRole(Role.USER));
            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {  // Skip the header row
                    isHeader = false;
                    continue;
                }
                try {
                    String userId = nextLine[1];  // column 1 is userId

                    // Check if userId already exists in the database
                    if (userIdsInDbSet.contains(userId)) {
                        logger.debug("User with ID {} already exists, skipping.", userId);
                        continue; // Skip if userId already exists in the database
                    }
                    UserDTO userDTO = new UserDTO(
                            userId,
                            nextLine[2], // firstName
                            nextLine[3], // lastName
                            nextLine[4], // sex
                            nextLine[5]  // email
                    );
                    User user = UserMapper.INSTANCE.userDTOToUser(userDTO);

                    String username = userId+1;
                    user.setPassword(passwordEncoder.encode(username));
                    user.setUsername(username);
                    usersToSave.add(user);

                    // Save users when the list size reaches the batch size defined in application.properties
                    if (usersToSave.size() == batchSize) {
                        logger.info("Saving {} users", usersToSave.size());
                        userRepository.saveAll(usersToSave);
                        usersToSave.clear(); // Clear the list after saving
                    }
                } catch (Exception e) {
                    logger.error("Error processing line, skipping. Error: {}", e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            logger.error("Error reading CSV file", e);
            throw new IOException("Error reading CSV file", e);
        }

        if (!usersToSave.isEmpty()) {
            logger.info("Saving {} users", usersToSave.size());
            userRepository.saveAll(usersToSave);
        }

        logger.info("CSV import process completed.");
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> bulkEditUsersAsync(List<UpdateUserDTO> usersDTO) {
        logger.info("Bulk edit started for {} users", usersDTO.size());
        List<User> usersToUpdate = new ArrayList<>();
        for (UpdateUserDTO userDTO : usersDTO) {
            User userToUpdate = userRepository.findByUserId(userDTO.getUserId()).orElse(null);
            if (userToUpdate != null) {
                UserMapper.INSTANCE.updateUserDTOToUser(userDTO, userToUpdate);
                usersToUpdate.add(userToUpdate);
            }
        }

        userRepository.saveAll(usersToUpdate);
        logger.info("Bulk edit completed, updated {} users", usersToUpdate.size());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Page<User> getAll(Pageable pageable, String name) {
        String query = Optional.ofNullable(name).orElse("");
        return userRepository.findByName(query, pageable);
    }

    @Override
    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public User save(CreateUserDTO userDTO) {
        User newUser = UserMapper.INSTANCE.createUserDTOToUser(userDTO);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRoles(getRole("ROLE_"+Role.USER));
        return userRepository.save(newUser);
    }

    @Override
    @CachePut(value = "users", key = "#userId")
    public User update(String userId, UpdateUserDTO user) {
        User userToUpdate = userRepository.findByUserId(userId).orElse(null);
        if (userToUpdate == null) {
            return null;
        }
        // Only update the necessary fields, keeping the userId and other fields from the existing entity
        UserMapper.INSTANCE.updateUserDTOToUser(user, userToUpdate);

        return userRepository.save(userToUpdate);
    }


    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void delete(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Override
    public void createDefaultUsers() {
        if (userRepository.existsUserByUsername("admin"))
            logger.error("User 'admin' already exists.");
        else {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("binhdiep15963@gmail.com");
            user.setUserId("12312314");
            user.setFirstName("binh");
            user.setLastName("diep");

            Set<Role> roles = new HashSet<>();
            roles.add(roleService.getRole("ROLE_ADMIN"));

            user.setRoles(roles);

            try {
                userRepository.save(user);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

    }
    private Set<Role> getRole(String roleName){
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRole(roleName));
        return roles;
    }

    public void checkUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.info("User not found for user_id: " + userId);
                    return new IllegalArgumentException("User not found for user_id: " + userId);
                });
    }

}