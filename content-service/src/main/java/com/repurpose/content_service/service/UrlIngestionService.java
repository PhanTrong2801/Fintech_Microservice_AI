package com.repurpose.content_service.service;

import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.TranscriptContent;
import io.github.thoroldvix.api.TranscriptList;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlIngestionService {

    private final YoutubeTranscriptApi youtubeTranscriptApi = TranscriptApiFactory.createDefault();

    /**
     * Trích xuất nội dung từ URL (YouTube hoặc Website)
     */
    public String extractContent(String url) {
        if (isYouTubeUrl(url)) {
            return extractYouTubeTranscript(url);
        } else {
            return extractWebsiteContent(url);
        }
    }

    private boolean isYouTubeUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    /**
     * Lấy transcript từ video YouTube bằng thư viện youtube-transcript-api
     */
    private String extractYouTubeTranscript(String url) {
        String videoId = extractVideoId(url);
        if (videoId == null) {
            throw new RuntimeException("Không thể trích xuất Video ID từ URL YouTube: " + url);
        }
        try {
            TranscriptContent transcriptContent;
            try {
                // Thử lấy sub tiếng Việt trước
                transcriptContent = youtubeTranscriptApi.getTranscript(videoId, "vi");
            } catch (Exception viError) {
                try {
                    // Nếu không có tiếng Việt, thử tiếng Anh
                    transcriptContent = youtubeTranscriptApi.getTranscript(videoId, "en");
                } catch (Exception enError) {
                    // Nếu không có tiếng Anh, lấy bất kỳ ngôn ngữ nào có sẵn
                    TranscriptList transcriptList = youtubeTranscriptApi.listTranscripts(videoId);
                    transcriptContent = transcriptList.iterator().next().fetch();
                }
            }
            return transcriptContent.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy transcript từ YouTube video (" + videoId + "): " + e.getMessage());
        }
    }

    /**
     * Cào dữ liệu text từ website bằng Jsoup
     */
    private String extractWebsiteContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            // Loại bỏ script, style, nav, footer trước khi lấy text
            doc.select("script, style, nav, footer, header, aside").remove();
            return doc.body().text();
        } catch (IOException e) {
            throw new RuntimeException("Không thể cào dữ liệu từ website: " + e.getMessage());
        }
    }

    /**
     * Trích xuất Video ID từ các dạng URL YouTube khác nhau
     */
    private String extractVideoId(String url) {
        String pattern = "(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
