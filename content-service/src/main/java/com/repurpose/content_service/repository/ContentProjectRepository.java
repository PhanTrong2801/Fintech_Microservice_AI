package com.repurpose.content_service.repository;

import com.repurpose.content_service.entity.ContentProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentProjectRepository extends JpaRepository<ContentProject, Long> {

    List<ContentProject> findByEmailOrderByCreatedAtDesc(String email);

    long countByEmail(String email);
}
