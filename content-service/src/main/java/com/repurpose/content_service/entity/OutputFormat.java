package com.repurpose.content_service.entity;

/**
 * Các định dạng output mà AI có thể tái chế nội dung sang
 */
public enum OutputFormat {
    TWITTER_THREAD,         // Chuỗi tweet
    LINKEDIN_POST,          // Bài LinkedIn
    INSTAGRAM_CAPTION,      // Caption Instagram
    EMAIL_NEWSLETTER,       // Email newsletter
    YOUTUBE_SHORT_SCRIPT,   // Script YouTube Shorts
    FACEBOOK_POST,          // Bài Facebook
    TIKTOK_SCRIPT,          // Script TikTok
    BLOG_SUMMARY,           // Tóm tắt blog
    SEO_META_DESCRIPTION,   // SEO meta description
    THREAD_UNROLLER         // Thread dạng dài
}
