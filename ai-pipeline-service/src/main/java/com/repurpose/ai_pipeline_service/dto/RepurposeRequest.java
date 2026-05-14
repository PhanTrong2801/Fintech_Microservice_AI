package com.repurpose.ai_pipeline_service.dto;

import com.repurpose.ai_pipeline_service.template.OutputFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepurposeRequest {
    private Long projectId;
    private String originalContent;
    private String contentType;
    private List<OutputFormat> outputFormats;
}
