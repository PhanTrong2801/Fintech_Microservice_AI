package com.repurpose.content_service.entity;

/**
 * Loại nội dung gốc mà người dùng nhập vào
 */
public enum ContentType {
    BLOG_POST,          // Bài blog
    VIDEO_TRANSCRIPT,   // Bản phiên âm video
    PODCAST,            // Podcast transcript
    SOCIAL_POST,        // Bài đăng mạng xã hội
    ARTICLE,            // Bài báo
    OTHER               // Khác
}
