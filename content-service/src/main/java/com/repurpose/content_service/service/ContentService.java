package com.repurpose.content_service.service;

import com.repurpose.content_service.dto.*;
import com.repurpose.content_service.entity.*;
import com.repurpose.content_service.httpclient.AiPipelineClient;
import com.repurpose.content_service.repository.ContentProjectRepository;
import com.repurpose.content_service.repository.RepurposedOutputRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentProjectRepository projectRepository;
    private final RepurposedOutputRepository outputRepository;
    private final AiPipelineClient aiPipelineClient;

    /**
     * Tạo project mới (tương đương createWallet trong Fintech)
     */
    public ContentProject createProject(String email, CreateProjectRequest request) {
        ContentProject project = ContentProject.builder()
                .email(email)
                .title(request.getTitle())
                .originalContent(request.getOriginalContent())
                .contentType(request.getContentType())
                .status(ProjectStatus.DRAFT)
                .build();
        return projectRepository.save(project);
    }

    /**
     * Tạo workspace rỗng khi user đăng ký (được gọi từ Identity Service qua Feign)
     */
    public ContentProject createWorkspace(String email) {
        ContentProject workspace = ContentProject.builder()
                .email(email)
                .title("My First Project")
                .originalContent("Welcome to AI Content Repurposer! Paste your content here to get started.")
                .contentType(ContentType.OTHER)
                .status(ProjectStatus.DRAFT)
                .build();
        return projectRepository.save(workspace);
    }

    /**
     * Lấy danh sách projects theo email (tương đương getWalletInfo)
     */
    public List<ContentProject> getProjectsByEmail(String email) {
        return projectRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Lấy chi tiết project
     */
    public ContentProject getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy project với ID: " + id));
    }

    /**
     * Trigger AI repurpose - logic chính (tương đương transfer trong Fintech)
     */
    @Transactional
    public List<RepurposedOutput> repurposeContent(Long projectId, List<OutputFormat> formats) {
        ContentProject project = getProjectById(projectId);

        // Cập nhật trạng thái project
        project.setStatus(ProjectStatus.PROCESSING);
        projectRepository.save(project);

        List<RepurposedOutput> outputs = new ArrayList<>();

        try {
            // Gọi AI Pipeline Service qua Feign (tương tự pattern gọi WalletClient)
            RepurposeRequest request = RepurposeRequest.builder()
                    .projectId(projectId)
                    .originalContent(project.getOriginalContent())
                    .contentType(project.getContentType().name())
                    .outputFormats(formats)
                    .build();

            var response = aiPipelineClient.repurpose(request);

            if (response.getBody() != null && response.getBody().isSuccess()) {
                // Lưu từng output vào DB
                for (RepurposeResult result : response.getBody().getResults()) {
                    RepurposedOutput output = RepurposedOutput.builder()
                            .project(project)
                            .outputFormat(result.getOutputFormat())
                            .generatedContent(result.getGeneratedContent())
                            .tokensUsed(result.getTokensUsed())
                            .status(OutputStatus.COMPLETED)
                            .build();
                    outputs.add(outputRepository.save(output));
                }
                project.setStatus(ProjectStatus.COMPLETED);
            } else {
                project.setStatus(ProjectStatus.FAILED);
            }

        } catch (Exception e) {
            System.err.println("Lỗi giao tiếp với AI Pipeline Service: " + e.getMessage());
            project.setStatus(ProjectStatus.FAILED);

            // Tạo output với trạng thái FAILED cho mỗi format
            for (OutputFormat format : formats) {
                RepurposedOutput failedOutput = RepurposedOutput.builder()
                        .project(project)
                        .outputFormat(format)
                        .generatedContent("Generation failed: " + e.getMessage())
                        .status(OutputStatus.FAILED)
                        .build();
                outputs.add(outputRepository.save(failedOutput));
            }
        }

        projectRepository.save(project);
        return outputs;
    }

    /**
     * Lấy tất cả outputs của 1 project
     */
    public List<RepurposedOutput> getOutputsByProjectId(Long projectId) {
        return outputRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    /**
     * Xóa project và tất cả outputs liên quan
     */
    @Transactional
    public void deleteProject(Long projectId) {
        // Xóa outputs trước (do foreign key constraint)
        List<RepurposedOutput> outputs = outputRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        outputRepository.deleteAll(outputs);
        projectRepository.deleteById(projectId);
    }

    /**
     * Đếm tổng số lần repurpose đã dùng (cho quota tracking)
     */
    public long countRepurposesByEmail(String email) {
        return outputRepository.countByProjectEmail(email);
    }

    /**
     * Sinh ảnh minh họa cho 1 output cụ thể
     */
    @Transactional
    public RepurposedOutput generateImageForOutput(Long outputId) {
        System.out.println("🔍 [DEBUG] Starting generateImageForOutput for ID: " + outputId);
        
        RepurposedOutput output = outputRepository.findById(outputId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy output với ID: " + outputId));

        System.out.println("🔍 [DEBUG] Found output: " + output.getOutputFormat());

        try {
            // Chuẩn bị prompt ngắn gọn từ nội dung đã generate
            String content = output.getGeneratedContent();
            if (content == null || content.isBlank()) {
                content = "A professional social media visual";
            }
            String shortPrompt = content.length() > 500 ? content.substring(0, 500) : content;
            System.out.println("🔍 [DEBUG] Prompt prepared: " + (shortPrompt.length() > 20 ? shortPrompt.substring(0, 20) + "..." : shortPrompt));

            // Gọi AI Pipeline Service để sinh ảnh
            java.util.Map<String, String> request = new java.util.HashMap<>();
            request.put("prompt", shortPrompt);
            
            System.out.println("🔍 [DEBUG] Calling AiPipelineClient...");
            java.util.Map<String, String> response = aiPipelineClient.generateImage(request);
            System.out.println("🔍 [DEBUG] AiPipelineClient response: " + response);

            if (response != null && response.containsKey("imageUrl")) {
                String imageUrl = response.get("imageUrl");
                System.out.println("🔍 [DEBUG] Image URL received: " + imageUrl);
                output.setImageUrl(imageUrl);
                RepurposedOutput saved = outputRepository.save(output);
                System.out.println("🔍 [DEBUG] Output saved successfully with image URL");
                return saved;
            } else {
                System.out.println("🔍 [DEBUG] Response was null or missing imageUrl key");
            }
        } catch (Exception e) {
            System.err.println("❌ [DEBUG] Lỗi sinh ảnh AI: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🔍 [DEBUG] Returning original output (fallback)");
        return output;
    }
}
