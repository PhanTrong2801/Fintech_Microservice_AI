package com.repurpose.content_service.dto;

import com.repurpose.content_service.entity.ContentType;
import com.repurpose.content_service.entity.OutputFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {
    private String title;
    private String originalContent;
    private ContentType contentType;
    private List<OutputFormat> outputFormats; // Danh sách format muốn tái chế
}
