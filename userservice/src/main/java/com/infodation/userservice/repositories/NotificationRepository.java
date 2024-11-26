package com.infodation.userservice.repositories;

import com.infodation.userservice.models.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications,Long> {

}
