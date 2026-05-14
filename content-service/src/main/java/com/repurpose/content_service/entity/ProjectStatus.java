package com.repurpose.content_service.entity;

/**
 * Trạng thái xử lý của Project
 */
public enum ProjectStatus {
    DRAFT,          // Vừa tạo, chưa xử lý
    PROCESSING,     // Đang được AI xử lý
    COMPLETED,      // Đã hoàn thành tái chế
    FAILED          // Xử lý thất bại
}
