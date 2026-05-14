package com.repurpose.content_service.entity;

/**
 * Trạng thái của từng output đã tái chế
 */
public enum OutputStatus {
    PENDING,        // Đang chờ xử lý
    GENERATING,     // AI đang generate
    COMPLETED,      // Đã hoàn thành
    FAILED          // Thất bại
}
