package com.infodation.userservice.services;

import com.infodation.userservice.models.Sex;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.IUserService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Value("${user.batch.size}") //value is injected from the application.properties file
    private int batchSize;
    @Async  // Annotation for asynchronous execution
    public CompletableFuture<Void> importUsersFromCsvAsync(MultipartFile file) throws IOException {
        List<User> usersToSave = new ArrayList<>();
        // Retrieve all existing userIds from the database (for comparison)
        Set<String> userIdsInDbSet = userRepository.findAllUserIds();

        // Read the CSV file
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())))) {
            String[] nextLine;
            boolean isHeader = true;

            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {  // Skip the header row
                    isHeader = false;
                    continue;
                }
                try {
                    String userId = nextLine[1];  // column 1 is userId

                    // Check if userId already exists in the database
                    if (userIdsInDbSet.contains(userId)) {
                        System.out.println("User with ID " + userId + " already exists, skipping.");
                        continue; // Skip if userId already exists in the database
                    }
                    // Map the CSV data to a User object
                    User user = new User();
                    user.setUserId(userId); // Column 1 is userId
                    user.setFirstName(nextLine[2]); // Column 2 is firstName
                    user.setLastName(nextLine[3]); // Column 3 is lastName
                    user.setEmail(nextLine[5]); // Column 5 is email
                    user.setSex(Sex.valueOf(nextLine[4].toUpperCase())); // Column 4 is MALE/FEMALE
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    // Add user to the list if it doesn't exist in the database
                    usersToSave.add(user);
                    // Save users when the list size reaches the batch size defined in application.properties
                    if (usersToSave.size() == batchSize) {
                        System.out.println("Saving " + usersToSave.size() + " users.");
                        userRepository.saveAll(usersToSave);
                        usersToSave.clear(); // Clear the list after saving
                    }
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    System.out.println("Invalid data in CSV line, skipping this row. Error: " + e.getMessage());
                } catch (Exception ex) {
                    System.out.println("Invalid data in CSV line, skipping this row. Error: " + ex.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error reading CSV file", e);
        }
        // Save remaining records after reading all lines
        if (!usersToSave.isEmpty()) {
            System.out.println("Saving " + usersToSave.size() + " users.");
            userRepository.saveAll(usersToSave);
        }
        return CompletableFuture.completedFuture(null);
    }
    @Async
    public CompletableFuture<Void> bulkEditUsersAsync(List<UpdateUserDTO> usersDTO) {
        List<User> usersToUpdate = new ArrayList<>();
        for (UpdateUserDTO userDTO : usersDTO) {
            User userToUpdate = userRepository.findByUserId(userDTO.getUserId()).orElse(null);
            if (userToUpdate != null) {
                userToUpdate.setFirstName(userDTO.getFirstName());
                userToUpdate.setLastName(userDTO.getLastName());
                userToUpdate.setEmail(userDTO.getEmail());
                userToUpdate.setSex(userDTO.getSex());
                userToUpdate.setUpdatedAt(LocalDateTime.now());
                usersToUpdate.add(userToUpdate);
            }
        } userRepository.saveAll(usersToUpdate);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Page<User> getAll(Pageable pageable, String name) {
        String query = Optional.ofNullable(name).orElse("");
        return userRepository.findByName(query, pageable);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
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
    @CachePut(value = "users", key = "#userId")
    public User update(String userId, UpdateUserDTO user) {
        User userToUpdate = userRepository.findByUserId(userId).orElse(null);

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
    @CacheEvict(value = "users", key = "#userId")
    public void delete(String userId) {
        userRepository.deleteByUserId(userId);
    }
}
