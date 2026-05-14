package com.repurpose.ai_pipeline_service.service;

import com.repurpose.ai_pipeline_service.template.OutputFormat;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Quản lý Prompt Templates cho từng output format.
 * Mỗi format có 1 prompt template riêng để hướng dẫn AI tạo nội dung phù hợp.
 */
@Service
public class PromptTemplateService {

    private static final Map<OutputFormat, String> TEMPLATES = Map.ofEntries(
            Map.entry(OutputFormat.TWITTER_THREAD,
                    """
                    Convert the following content into a Twitter/X thread (5-8 tweets).
                    Rules:
                    - Each tweet must be under 280 characters
                    - Start with a hook tweet that grabs attention
                    - Use numbered format (1/, 2/, etc.)
                    - End with a call-to-action tweet
                    - Use relevant emojis sparingly
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.LINKEDIN_POST,
                    """
                    Convert the following content into a professional LinkedIn post.
                    Rules:
                    - Start with a compelling hook (first 2 lines are crucial)
                    - Use short paragraphs (1-2 sentences each)
                    - Include relevant insights and takeaways
                    - Add 3-5 relevant hashtags at the end
                    - Professional but engaging tone
                    - 1300 characters maximum
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.INSTAGRAM_CAPTION,
                    """
                    Convert the following content into an Instagram caption.
                    Rules:
                    - Start with a catchy first line
                    - Use storytelling approach
                    - Include a call-to-action (save, share, comment)
                    - Add 20-30 relevant hashtags at the end
                    - Use emojis to break up text
                    - Keep it under 2200 characters
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.EMAIL_NEWSLETTER,
                    """
                    Convert the following content into an email newsletter.
                    Rules:
                    - Write a compelling subject line
                    - Start with a personal greeting
                    - Break content into scannable sections with headers
                    - Include key takeaways or bullet points
                    - End with a clear call-to-action
                    - Professional yet conversational tone
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.YOUTUBE_SHORT_SCRIPT,
                    """
                    Convert the following content into a YouTube Shorts script (under 60 seconds).
                    Rules:
                    - Start with a hook in the first 3 seconds
                    - Include [VISUAL] cues for what to show on screen
                    - Keep sentences short and punchy
                    - End with a subscribe CTA
                    - Format: NARRATOR: [text] / [VISUAL: description]
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.FACEBOOK_POST,
                    """
                    Convert the following content into a Facebook post.
                    Rules:
                    - Engaging and conversational tone
                    - Include a question to encourage comments
                    - Use line breaks for readability
                    - Keep under 500 words
                    - Add relevant emojis
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.TIKTOK_SCRIPT,
                    """
                    Convert the following content into a TikTok script (15-60 seconds).
                    Rules:
                    - Hook in the first 2 seconds ("Did you know..." or "Stop scrolling if...")
                    - Fast-paced, energetic delivery
                    - Include [ACTION] cues
                    - Trendy, Gen-Z friendly language
                    - End with "Follow for more"
                    
                    Content to convert:
                    %s
                    """),

            Map.entry(OutputFormat.BLOG_SUMMARY,
                    """
                    Create a concise blog summary of the following content.
                    Rules:
                    - Write a compelling title
                    - 3-5 key bullet points
                    - Include a TL;DR section
                    - SEO-friendly language
                    - Under 200 words
                    
                    Content to summarize:
                    %s
                    """),

            Map.entry(OutputFormat.SEO_META_DESCRIPTION,
                    """
                    Create an SEO meta description for the following content.
                    Rules:
                    - Between 150-160 characters
                    - Include primary keyword naturally
                    - Compelling and click-worthy
                    - Include a value proposition
                    - Action-oriented language
                    
                    Content:
                    %s
                    """),

            Map.entry(OutputFormat.THREAD_UNROLLER,
                    """
                    Convert the following content into a long-form thread (10-15 posts).
                    Rules:
                    - Each post is a complete thought
                    - Use storytelling with a narrative arc
                    - Include data points and examples
                    - Number each post
                    - End with a summary and CTA
                    
                    Content to convert:
                    %s
                    """)
    );

    /**
     * Lấy prompt template cho format cụ thể, chèn nội dung gốc vào
     */
    public String getPrompt(OutputFormat format, String originalContent) {
        String template = TEMPLATES.getOrDefault(format,
                "Convert the following content into %s format:\n%s"
                        .formatted(format.name(), "%s"));
        return template.formatted(originalContent);
    }

    /**
     * Lấy template gốc (chưa chèn content)
     */
    public String getTemplate(OutputFormat format) {
        return TEMPLATES.getOrDefault(format, "No template available for " + format.name());
    }
}
