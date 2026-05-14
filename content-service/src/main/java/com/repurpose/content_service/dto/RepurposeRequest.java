package com.repurpose.content_service.dto;

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
public class RepurposeRequest {
    private Long projectId;
    private String originalContent;
    private String contentType;
    private List<OutputFormat> outputFormats;
}
