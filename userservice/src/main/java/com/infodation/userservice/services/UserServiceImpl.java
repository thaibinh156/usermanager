package com.infodation.userservice.services;

import com.infodation.userservice.models.Sex;
import com.infodation.userservice.models.User;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import com.infodation.userservice.repositories.UserRepository;
import com.infodation.userservice.services.iservice.IUserService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Async  // Annotation cho bất đồng bộ
    public CompletableFuture<Void> importUsersFromCsv(MultipartFile file) throws IOException {
        List<User> usersToSave = new ArrayList<>();

        // Đọc file CSV
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            boolean isHeader = true;

            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {  // Skip dòng tiêu đề
                    isHeader = false;
                    continue;
                }
                try {
                    // Tạo đối tượng User, chỉ lấy các cột cần thiết
                    User user = new User();
                    user.setUserId(nextLine[1]); // Giả định cột 1 là userId
                    user.setFirstName(nextLine[2]); // Giả định cột 2 là firstName
                    user.setLastName(nextLine[3]); // Giả định cột 3 là lastName
                    user.setEmail(nextLine[5]); // Giả định cột 5 là email
                    user.setSex(Sex.valueOf(nextLine[4].toUpperCase())); // MALE/FEMALE
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    usersToSave.add(user);
                    // Khi danh sách đủ 10.000 người dùng thì lưu
                    if (usersToSave.size() == 5000) {
                        userRepository.saveAll(usersToSave);
                        usersToSave.clear(); // Xóa danh sách sau khi lưu
                    }
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    // Bỏ qua dòng có lỗi hoặc không đầy đủ
                    System.out.println("Invalid data in CSV line, skipping this row.");
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error reading CSV file", e);
        }

        // Lưu các bản ghi còn lại
        if (!usersToSave.isEmpty()) {
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
