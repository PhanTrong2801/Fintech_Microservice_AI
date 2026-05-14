package com.repurpose.content_service.repository;

import com.repurpose.content_service.entity.OutputFormat;
import com.repurpose.content_service.entity.RepurposedOutput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepurposedOutputRepository extends JpaRepository<RepurposedOutput, Long> {

    List<RepurposedOutput> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<RepurposedOutput> findByProjectEmailOrderByCreatedAtDesc(String email);

    long countByProjectEmail(String email);

    boolean existsByProjectIdAndOutputFormat(Long projectId, OutputFormat format);
}
