package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByApplicationOrderBySentAtAsc(Application application);
}
