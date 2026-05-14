// ============================================
// Định nghĩa Types/Interfaces cho Frontend
// ============================================

export interface AuthResponse {
  token: string;
  message: string;
}

export interface User {
  email: string;
}

export type ContentType = 
  | 'BLOG_POST' 
  | 'VIDEO_TRANSCRIPT' 
  | 'PODCAST' 
  | 'SOCIAL_POST' 
  | 'ARTICLE' 
  | 'OTHER';

export type OutputFormat = 
  | 'TWITTER_THREAD'
  | 'LINKEDIN_POST'
  | 'INSTAGRAM_CAPTION'
  | 'EMAIL_NEWSLETTER'
  | 'YOUTUBE_SHORT_SCRIPT'
  | 'FACEBOOK_POST'
  | 'TIKTOK_SCRIPT'
  | 'BLOG_SUMMARY'
  | 'SEO_META_DESCRIPTION'
  | 'THREAD_UNROLLER';

export type OutputStatus = 'PENDING' | 'GENERATING' | 'COMPLETED' | 'FAILED';

export interface RepurposedOutput {
  id: number;
  projectId: number;
  outputFormat: OutputFormat;
  generatedContent: string;
  status: OutputStatus;
  errorMessage?: string;
  imageUrl?: string;
  createdAt: string;
}

export interface ContentProject {
  id: number;
  email: string;
  title: string;
  originalContent: string;
  contentType: ContentType;
  status: string;
  createdAt: string;
  updatedAt: string;
  outputs: RepurposedOutput[];
}

export interface CreateProjectRequest {
  title: string;
  originalContent: string;
  contentType: ContentType;
  outputFormats?: OutputFormat[]; // Tùy chọn, để xử lý gọn gàng flow 1 bước
}
