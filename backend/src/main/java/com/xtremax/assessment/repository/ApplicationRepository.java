package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Optional<Application> findByReferenceNumber(String referenceNumber);
}
