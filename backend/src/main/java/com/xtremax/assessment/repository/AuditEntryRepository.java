package com.xtremax.assessment.repository;

import com.xtremax.assessment.domain.Application;
import com.xtremax.assessment.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, UUID> {
    List<AuditEntry> findByApplicationOrderByTimestampAsc(Application application);
}
