package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.ApplicationRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRevisionRepository extends JpaRepository<ApplicationRevision, UUID> {
    List<ApplicationRevision> findByApplicationOrderByRevisionNumberAsc(Application application);
    Optional<ApplicationRevision> findByApplicationAndRevisionNumber(Application application, int revisionNumber);
}
