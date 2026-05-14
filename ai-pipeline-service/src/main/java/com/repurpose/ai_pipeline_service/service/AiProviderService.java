package com.repurpose.ai_pipeline_service.service;

import com.repurpose.ai_pipeline_service.dto.gemini.GeminiRequest;
import com.repurpose.ai_pipeline_service.dto.gemini.GeminiResponse;
import com.repurpose.ai_pipeline_service.dto.openai.OpenAiImageRequest;
import com.repurpose.ai_pipeline_service.dto.openai.OpenAiImageResponse;
import com.repurpose.ai_pipeline_service.template.OutputFormat;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Service gọi AI Provider để generate nội dung.
 * Hiện tại hỗ trợ MOCK và Google GEMINI.
 */
@Service
@Getter
public class AiProviderService {

    @Value("${ai.provider:mock}")
    private String provider;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    private final WebClient webClient;

    public AiProviderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Generate nội dung dựa trên prompt.
     * Mock mode: trả về nội dung mẫu chất lượng cho từng format.
     */
    public String generate(String prompt, OutputFormat format) {
        return switch (provider.toLowerCase()) {
            case "openai" -> callOpenAI(prompt);
            case "gemini" -> callGemini(prompt);
            default -> generateMockContent(format, prompt);
        };
    }

    /**
     * Ước tính tokens sử dụng
     */
    public int estimateTokens(String content) {
        // Rough estimation: ~4 chars per token
        return content.length() / 4;
    }

    // ============== MOCK PROVIDER ==============

    private String generateMockContent(OutputFormat format, String prompt) {
        // Trích xuất 50 ký tự đầu từ content gốc để làm context
        String contentPreview = prompt.length() > 100
                ? prompt.substring(prompt.length() - 100).trim()
                : prompt;

        return switch (format) {
            case TWITTER_THREAD -> """
                    🧵 1/ Here's what you need to know about this topic...
                    
                    2/ The key insight is that content repurposing saves 80%% of your time while reaching 3x more audience.
                    
                    3/ Most creators make the mistake of creating from scratch every time. Instead, take ONE great piece and transform it.
                    
                    4/ The formula is simple:
                    📝 Blog → 🐦 Thread → 📱 Reels → 📧 Newsletter
                    
                    5/ Start with your best-performing content. It's already validated by your audience.
                    
                    6/ Tools like AI Content Repurposer can automate this entire process in seconds.
                    
                    7/ Your action step: Take your top blog post and turn it into 3 different formats TODAY.
                    
                    Like & RT if this was helpful! 🔄
                    """;

            case LINKEDIN_POST -> """
                    I used to spend 10 hours creating content every week.
                    
                    Now I spend 2 hours.
                    
                    Here's what changed:
                    
                    I stopped creating from scratch.
                    Instead, I started repurposing.
                    
                    One blog post becomes:
                    → A Twitter thread
                    → A LinkedIn carousel
                    → An email newsletter
                    → 3 Instagram stories
                    
                    The result?
                    ✅ 3x more content output
                    ✅ 80%% less time spent
                    ✅ Consistent presence across all platforms
                    
                    The secret isn't working harder.
                    It's working smarter with AI-powered repurposing.
                    
                    What's your biggest content creation challenge? 👇
                    
                    #ContentCreation #AI #Productivity #SaaS #ContentMarketing
                    """;

            case INSTAGRAM_CAPTION -> """
                    🚀 Content creation doesn't have to be overwhelming.
                    
                    Here's the truth nobody tells you about growing on multiple platforms:
                    
                    You DON'T need to create unique content for each one.
                    
                    ✨ Write ONE great piece
                    ✨ Let AI transform it for every platform
                    ✨ Post everywhere consistently
                    
                    This is how top creators manage 5+ platforms without burning out.
                    
                    💡 Save this for later & share with a creator friend who needs this!
                    
                    .
                    .
                    .
                    #contentcreation #aitools #socialmediamarketing #contentrepurposing #digitalmarketing #creatortips #productivity #saas #contentmarketing #growthhacking
                    """;

            case EMAIL_NEWSLETTER -> """
                    Subject: How to Create 10x More Content Without 10x the Effort
                    
                    Hey there! 👋
                    
                    Have you ever felt like the content creation hamster wheel never stops?
                    
                    You're not alone. Most creators spend 15+ hours per week just creating content.
                    
                    ## The Content Repurposing Framework
                    
                    Here's what I've learned:
                    
                    **Step 1:** Create ONE pillar piece of content (blog, video, podcast)
                    **Step 2:** Break it down into atomic units
                    **Step 3:** Transform each unit for different platforms
                    **Step 4:** Schedule and distribute
                    
                    ### Key Takeaways:
                    - 🎯 Focus on quality over quantity for your source content
                    - 🔄 One blog post = 8+ pieces of content
                    - ⏰ Save 10+ hours per week
                    - 📈 Reach audiences on every platform
                    
                    The best part? AI can now handle Step 2 and 3 automatically.
                    
                    **[Try AI Content Repurposer →]**
                    
                    Until next time,
                    The Repurpose AI Team
                    """;

            case YOUTUBE_SHORT_SCRIPT -> """
                    NARRATOR: "Stop creating content the hard way."
                    [VISUAL: Person stressed at computer with multiple social media tabs open]
                    
                    NARRATOR: "Here's the secret top creators don't want you to know..."
                    [VISUAL: Zoom into screen showing one blog post]
                    
                    NARRATOR: "Write ONE great piece of content..."
                    [VISUAL: Blog post transforms into multiple formats with animation]
                    
                    NARRATOR: "And let AI turn it into content for EVERY platform."
                    [VISUAL: Split screen showing Twitter, LinkedIn, Instagram, Email]
                    
                    NARRATOR: "Twitter threads, LinkedIn posts, Instagram captions, email newsletters..."
                    [VISUAL: Quick montage of each platform with content]
                    
                    NARRATOR: "All from that ONE original piece."
                    [VISUAL: Mind-blown emoji animation]
                    
                    NARRATOR: "Try it free - link in bio. Subscribe for more creator hacks!"
                    [VISUAL: Subscribe button animation]
                    """;

            case FACEBOOK_POST -> """
                    📣 Game-changer alert for all content creators!
                    
                    I just discovered something that completely changed how I create content...
                    
                    Instead of spending hours writing separate posts for every platform, I now:
                    
                    1️⃣ Write ONE piece of content
                    2️⃣ Use AI to repurpose it into 8+ different formats
                    3️⃣ Post everywhere in minutes
                    
                    The result? More content, more reach, less stress. 🎉
                    
                    Who else is tired of the content creation grind? Drop a 🙋 if you want to learn more about content repurposing!
                    """;

            case TIKTOK_SCRIPT -> """
                    [HOOK - 2 seconds]
                    "Stop scrolling if you create content for a living"
                    
                    [ACTION: Point at camera, dramatic pause]
                    
                    "You're probably spending HOURS creating separate content for each platform"
                    
                    [ACTION: Show phone with multiple apps]
                    
                    "But what if I told you... you only need to create ONE piece?"
                    
                    [ACTION: Hold up one finger]
                    
                    "AI can now take your blog post and turn it into Twitter threads, LinkedIn posts, Instagram captions, and email newsletters"
                    
                    [ACTION: Show each platform popping up on screen]
                    
                    "All in under 30 seconds"
                    
                    [ACTION: Timer countdown]
                    
                    "Follow for more creator hacks! 🔥"
                    """;

            case BLOG_SUMMARY -> """
                    ## TL;DR: Content Repurposing with AI
                    
                    **Key Points:**
                    • Content repurposing multiplies your reach without multiplying your effort
                    • AI-powered tools can automatically transform one piece of content into 8+ formats
                    • Top creators use repurposing as their primary content strategy
                    • The ROI of repurposing: 80%% less time, 3x more content output
                    • Start with your best-performing content for maximum impact
                    
                    **Bottom Line:** Stop creating from scratch. Start repurposing with AI.
                    """;

            case SEO_META_DESCRIPTION -> """
                    Transform any content into 10+ formats instantly with AI. Save 80%% of your content creation time. Try our free AI Content Repurposing tool today.
                    """;

            case THREAD_UNROLLER -> """
                    📖 DEEP DIVE: The Complete Guide to Content Repurposing with AI
                    
                    1/ Let me tell you about the strategy that saved me 10 hours every week and tripled my online presence...
                    
                    2/ It all started when I realized I was creating content the WRONG way. I was writing unique posts for every single platform. Twitter, LinkedIn, Instagram, Email...
                    
                    3/ Then I discovered content repurposing. The idea is simple: create ONE high-quality piece and transform it into multiple formats.
                    
                    4/ But manual repurposing still takes time. That's where AI comes in. AI can analyze your content and automatically generate platform-specific versions.
                    
                    5/ Here's how it works:
                    📝 You write a blog post
                    🤖 AI reads and understands the core message
                    🔄 It generates versions for each platform
                    ✅ You review and publish
                    
                    6/ The key is that each platform has its own format and tone. Twitter needs threads. LinkedIn needs professional insights. Instagram needs visual captions.
                    
                    7/ AI handles these differences automatically, adapting tone, length, and format for each platform.
                    
                    8/ The results speak for themselves:
                    - 80%% less time creating content
                    - 3x more content published
                    - Consistent presence everywhere
                    - Better engagement (right format for right platform)
                    
                    9/ Common mistakes to avoid:
                    ❌ Copy-pasting the same text everywhere
                    ❌ Ignoring platform-specific best practices
                    ❌ Not reviewing AI-generated content
                    ❌ Starting with low-quality source content
                    
                    10/ Pro tips for best results:
                    ✅ Start with your best-performing content
                    ✅ Always review and add your personal touch
                    ✅ Test different formats to see what resonates
                    ✅ Build a repurposing workflow into your routine
                    
                    11/ The future of content creation is NOT about producing more. It's about being smarter with what you already have.
                    
                    12/ If you found this valuable, bookmark it and share it with a creator friend who needs this strategy! 🔖
                    """;
        };
    }

    // ============== FREE IMAGE GENERATION (POLLINATIONS.AI) ==============

    public String generateImage(String textPrompt) {
        try {
            // Encode prompt để đưa vào URL
            String encodedPrompt = java.net.URLEncoder.encode(
                "High quality social media post illustration for: " + textPrompt, 
                java.nio.charset.StandardCharsets.UTF_8
            );
            
            // Pollinations.ai cho phép lấy ảnh trực tiếp qua URL cực kỳ đơn giản và MIỄN PHÍ
            // Format: https://image.pollinations.ai/prompt/{prompt}?width=1024&height=1024&nologo=true
            String imageUrl = "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=1024&height=1024&nologo=true&seed=" + System.currentTimeMillis();
            
            System.out.println("🎨 Generated Free Image URL: " + imageUrl);
            return imageUrl;
        } catch (Exception e) {
            System.err.println("Lỗi tạo URL ảnh: " + e.getMessage());
            return "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1000&auto=format&fit=crop";
        }
    }

    // ============== GEMINI PROVIDER ==============

    private String callGemini(String prompt) {
        if (geminiApiKey == null || geminiApiKey.isBlank() || geminiApiKey.equals("placeholder")) {
            return "⚠️ Gemini API Key chưa được cấu hình. Hãy kiểm tra biến môi trường GEMINI_API_KEY.\n\n[MOCK CONTENT]:\n" + generateMockContent(OutputFormat.BLOG_SUMMARY, prompt);
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

        GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(GeminiRequest.Content.builder()
                        .parts(List.of(GeminiRequest.Part.builder()
                                .text(prompt)
                                .build()))
                        .build()))
                .build();

        try {
            GeminiResponse response = webClient.post()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
            return "Lỗi: Không nhận được kết quả từ Gemini API.";
        } catch (Exception e) {
            System.err.println("Lỗi gọi Gemini API: " + e.getMessage());
            return "Lỗi kết nối Gemini: " + e.getMessage();
        }
    }

    // ============== OPENAI PROVIDER (FUTURE) ==============

    private String callOpenAI(String prompt) {
        throw new UnsupportedOperationException("OpenAI provider chưa được implement. Hãy dùng ai.provider=gemini");
    }
}
