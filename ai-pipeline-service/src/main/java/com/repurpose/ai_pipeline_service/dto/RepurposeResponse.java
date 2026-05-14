package com.repurpose.ai_pipeline_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepurposeResponse {
    private boolean success;
    private String message;
    private List<RepurposeResult> results;
}
