package com.repurpose.content_service.controller;

import com.repurpose.content_service.dto.CreateProjectRequest;
import com.repurpose.content_service.entity.ContentProject;
import com.repurpose.content_service.entity.OutputFormat;
import com.repurpose.content_service.entity.RepurposedOutput;
import com.repurpose.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.repurpose.content_service.service.UrlIngestionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final UrlIngestionService urlIngestionService;

    /**
     * Tạo workspace rỗng khi user đăng ký (gọi nội bộ từ Identity Service)
     */
    @PostMapping("/workspace/create")
    public ResponseEntity<ContentProject> createWorkspace(@RequestParam String email) {
        return ResponseEntity.ok(contentService.createWorkspace(email));
    }

    /**
     * Tạo project mới
     */
    @PostMapping("/projects")
    public ResponseEntity<ContentProject> createProject(
            @RequestParam String email,
            @RequestBody CreateProjectRequest request) {
        return ResponseEntity.ok(contentService.createProject(email, request));
    }

    /**
     * Tạo project từ URL (YouTube hoặc Website)
     */
    @PostMapping("/projects/ingest")
    public ResponseEntity<ContentProject> ingestProject(
            @RequestParam String email,
            @RequestParam String url) {
        String content = urlIngestionService.extractContent(url);
        
        CreateProjectRequest request = CreateProjectRequest.builder()
                .title("Project from URL")
                .originalContent(content)
                .contentType(com.repurpose.content_service.entity.ContentType.OTHER)
                .build();
                
        return ResponseEntity.ok(contentService.createProject(email, request));
    }

    /**
     * Lấy danh sách projects của user
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ContentProject>> getProjects(@RequestParam String email) {
        return ResponseEntity.ok(contentService.getProjectsByEmail(email));
    }

    /**
     * Lấy chi tiết 1 project
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<ContentProject> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getProjectById(id));
    }

    /**
     * Trigger AI repurpose cho 1 project
     * Body: { "formats": ["TWITTER_THREAD", "LINKEDIN_POST", "BLOG_SUMMARY"] }
     */
    @PostMapping("/projects/{id}/repurpose")
    public ResponseEntity<List<RepurposedOutput>> repurpose(
            @PathVariable Long id,
            @RequestBody Map<String, List<OutputFormat>> body,
            @RequestHeader("Authorization") String token) {
        List<OutputFormat> formats = body.get("formats");
        if (formats == null || formats.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(contentService.repurposeContent(id, formats, token));
    }

    /**
     * Lấy tất cả outputs đã generate cho 1 project
     */
    @GetMapping("/projects/{id}/outputs")
    public ResponseEntity<List<RepurposedOutput>> getOutputs(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getOutputsByProjectId(id));
    }

    /**
     * Xóa project
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        contentService.deleteProject(id);
        return ResponseEntity.ok("Đã xóa project thành công");
    }

    /**
     * Lấy danh sách tất cả output formats có sẵn
     */
    @GetMapping("/formats")
    public ResponseEntity<OutputFormat[]> getAvailableFormats() {
        return ResponseEntity.ok(OutputFormat.values());
    }

    /**
     * Sinh ảnh minh họa cho 1 output cụ thể
     */
    @PostMapping("/outputs/{outputId}/generate-image")
    public ResponseEntity<RepurposedOutput> generateImage(@PathVariable Long outputId) {
        return ResponseEntity.ok(contentService.generateImageForOutput(outputId));
    }

    /**
     * Cập nhật nội dung của một output
     */
    @PutMapping("/outputs/{outputId}")
    public ResponseEntity<RepurposedOutput> updateOutputContent(
            @PathVariable Long outputId,
            @RequestBody Map<String, String> body) {
        String newContent = body.get("content");
        if (newContent == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(contentService.updateOutputContent(outputId, newContent));
    }
}
