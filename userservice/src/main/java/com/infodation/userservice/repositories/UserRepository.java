package com.infodation.userservice.repositories;

import com.infodation.userservice.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM users u " +
            "WHERE :name = '' OR u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    Page<User> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT u.userId FROM users u")
    List<String> findAllUserIds();

    Optional<User> findByUserId(String userId);
    @Transactional
    void deleteByUserId(String userId);
}
