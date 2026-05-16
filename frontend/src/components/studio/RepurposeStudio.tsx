import React, { useState } from 'react';
import { Link2, Loader2, CheckCircle2, Sparkles } from 'lucide-react';
import toast from 'react-hot-toast';
import { api } from '../../api/axios';
import type { OutputFormat, ContentType, RepurposedOutput } from '../../types';
import OutputCard from '../shared/OutputCard';

interface RepurposeStudioProps {
  email: string;
  fetchQuota: () => void;
  // Shared editing state
  editingOutputId: number | null;
  editingContent: string;
  isGeneratingImage: number | null;
  setEditingOutputId: (id: number | null) => void;
  setEditingContent: (content: string) => void;
  setIsGeneratingImage: (id: number | null) => void;
  handleCopy: (text: string) => void;
}

const availableFormats: {id: OutputFormat, label: string}[] = [
  { id: 'TWITTER_THREAD', label: 'Twitter Thread' },
  { id: 'LINKEDIN_POST', label: 'LinkedIn Post' },
  { id: 'FACEBOOK_POST', label: 'Facebook Post' },
  { id: 'INSTAGRAM_CAPTION', label: 'Instagram Caption' },
  { id: 'TIKTOK_SCRIPT', label: 'TikTok Script' },
  { id: 'YOUTUBE_SHORT_SCRIPT', label: 'YouTube Shorts' },
  { id: 'EMAIL_NEWSLETTER', label: 'Email Newsletter' },
  { id: 'BLOG_SUMMARY', label: 'Blog Summary' },
  { id: 'SEO_META_DESCRIPTION', label: 'SEO Meta' },
];

const RepurposeStudio: React.FC<RepurposeStudioProps> = ({
  email, fetchQuota,
  editingOutputId, editingContent, isGeneratingImage,
  setEditingOutputId, setEditingContent, setIsGeneratingImage, handleCopy
}) => {
  const [ingestUrl, setIngestUrl] = useState('');
  const [isIngesting, setIsIngesting] = useState(false);
  const [title, setTitle] = useState('');
  const [inputText, setInputText] = useState('');
  const [contentType, setContentType] = useState<ContentType>('BLOG_POST');
  const [selectedFormats, setSelectedFormats] = useState<OutputFormat[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const [results, setResults] = useState<RepurposedOutput[]>([]);

  const toggleFormat = (format: OutputFormat) => {
    if (selectedFormats.includes(format)) {
      setSelectedFormats(selectedFormats.filter(f => f !== format));
    } else {
      setSelectedFormats([...selectedFormats, format]);
    }
  };

  const handleGenerate = async () => {
    if (!title.trim() || !inputText.trim()) {
      toast.error('Please provide a title and original content');
      return;
    }
    if (selectedFormats.length === 0) {
      toast.error('Please select at least one output format');
      return;
    }

    setIsGenerating(true);
    setResults([]); 
    const loadingToast = toast.loading('Step 1/2: Saving content project...');
    
    try {
      const projectRes = await api.post(`/content/projects?email=${email}`, {
        title,
        originalContent: inputText,
        contentType
      });
      const projectId = projectRes.data.id;

      toast.loading('Step 2/2: AI is generating content... (This might take a few seconds)', { id: loadingToast });
      
      const repurposeRes = await api.post(`/content/projects/${projectId}/repurpose`, {
        formats: selectedFormats
      });

      setResults(repurposeRes.data);
      toast.success('Content generated successfully!', { id: loadingToast });
      fetchQuota();
      
    } catch (error: any) {
      const errorMsg = error.response?.data?.message || error.response?.data?.error || 'Failed to generate content';
      if (errorMsg.includes('quota') || errorMsg.includes('Bạn đã hết') || error.response?.status === 500) {
        toast.error('🚫 Bạn đã hết quota! Vui lòng nâng cấp tài khoản để tiếp tục.', { id: loadingToast, duration: 5000 });
        fetchQuota();
      } else {
        toast.error(errorMsg, { id: loadingToast });
      }
    } finally {
      setIsGenerating(false);
    }
  };

  const handleEdit = (output: RepurposedOutput) => {
    setEditingOutputId(output.id);
    setEditingContent(output.generatedContent);
  };

  const handleSaveEdit = async (outputId: number) => {
    const loadingToast = toast.loading('Saving changes...');
    try {
      const res = await api.put(`/content/outputs/${outputId}`, {
        content: editingContent
      });
      
      setResults(prev => prev.map(item => 
        item.id === outputId ? { ...item, generatedContent: res.data.generatedContent } : item
      ));

      setEditingOutputId(null);
      toast.success('Changes saved!', { id: loadingToast });
    } catch (error) {
      toast.error('Failed to save changes', { id: loadingToast });
    }
  };

  const handleGenerateImage = async (outputId: number) => {
    setIsGeneratingImage(outputId);
    const loadingToast = toast.loading('AI is imagining your visual...');
    try {
      const res = await api.post(`/content/outputs/${outputId}/generate-image`);
      setResults(prev => prev.map(item => 
        item.id === outputId ? { ...item, imageUrl: res.data.imageUrl } : item
      ));
      toast.success('Visual generated successfully!', { id: loadingToast });
    } catch (error) {
      toast.error('Failed to generate image', { id: loadingToast });
    } finally {
      setIsGeneratingImage(null);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
      <h1 className="mb-1" style={{ fontSize: '2.5rem' }}>Repurpose Studio</h1>
      <p className="text-muted mb-4">Paste your original content or import from a URL.</p>

      {/* URL Ingestion Section */}
      <div className="glass-card mb-4" style={{ padding: '1.25rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem' }}>
          <Link2 size={18} color="var(--accent-primary)" />
          <span style={{ fontWeight: 'bold', fontSize: '0.95rem' }}>Import from URL</span>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>(YouTube or Website)</span>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <input
            type="url"
            value={ingestUrl}
            onChange={(e) => setIngestUrl(e.target.value)}
            placeholder="Paste a YouTube or Website URL here..."
            style={{ flex: 1, padding: '0.75rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white', borderRadius: '0.375rem' }}
          />
          <button
            onClick={async () => {
              if (!ingestUrl.trim()) { toast.error('Please enter a URL'); return; }
              setIsIngesting(true);
              const loadingToast = toast.loading('Extracting content from URL...');
              try {
                const res = await api.post(`/content/projects/ingest?email=${email}&url=${encodeURIComponent(ingestUrl)}`);
                setTitle(res.data.title || 'Project from URL');
                setInputText(res.data.originalContent);
                setContentType(res.data.contentType || 'OTHER');
                setIngestUrl('');
                toast.success('Content extracted successfully! You can now select formats and generate.', { id: loadingToast });
              } catch (error: any) {
                toast.error(error.response?.data?.message || 'Failed to extract content from URL', { id: loadingToast });
              } finally {
                setIsIngesting(false);
              }
            }}
            disabled={isIngesting}
            className="btn btn-primary"
            style={{ whiteSpace: 'nowrap', opacity: isIngesting ? 0.7 : 1 }}
          >
            {isIngesting ? <Loader2 className="animate-spin" size={18} /> : <Link2 size={18} />}
            {isIngesting ? 'Extracting...' : 'Import'}
          </button>
        </div>
      </div>

      <div className="glass-card mb-4" style={{ padding: '0', overflow: 'hidden' }}>
        <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--glass-border)' }}>
          <input 
            type="text" 
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Project Title (e.g. My Next Blog Post)"
            style={{ width: '100%', padding: '0.75rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white', borderRadius: '0.375rem', marginBottom: '1rem' }}
          />
          <select 
            value={contentType}
            onChange={(e) => setContentType(e.target.value as ContentType)}
            style={{ width: '100%', padding: '0.75rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white', borderRadius: '0.375rem' }}
          >
            <option value="BLOG_POST">Blog Post</option>
            <option value="VIDEO_TRANSCRIPT">Video Transcript</option>
            <option value="PODCAST">Podcast Transcript</option>
            <option value="ARTICLE">Article</option>
            <option value="OTHER">Other / General Text</option>
          </select>
        </div>

        <textarea 
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Paste your original content here..."
          style={{ 
            width: '100%', minHeight: '200px', background: 'transparent', border: 'none', 
            color: 'white', padding: '1.5rem', fontSize: '1rem', resize: 'vertical',
            outline: 'none'
          }}
        />
        
        <div style={{ borderTop: '1px solid var(--glass-border)', padding: '1rem 1.5rem', background: 'rgba(0,0,0,0.2)' }}>
          <div style={{ marginBottom: '1rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Select Output Formats:</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '1.5rem' }}>
            {availableFormats.map(format => (
              <button
                key={format.id}
                onClick={() => toggleFormat(format.id)}
                style={{ 
                  fontSize: '0.875rem', padding: '0.5rem 1rem', borderRadius: '1.5rem',
                  background: selectedFormats.includes(format.id) ? 'var(--accent-primary)' : 'rgba(255,255,255,0.05)',
                  border: `1px solid ${selectedFormats.includes(format.id) ? 'var(--accent-primary)' : 'var(--glass-border)'}`,
                  color: 'white', transition: 'all 0.2s', display: 'flex', alignItems: 'center', gap: '0.5rem'
                }}
              >
                {selectedFormats.includes(format.id) && <CheckCircle2 size={14} />}
                {format.label}
              </button>
            ))}
          </div>
          
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button 
              onClick={handleGenerate}
              disabled={isGenerating}
              className="btn btn-primary"
              style={{ opacity: isGenerating ? 0.7 : 1 }}
            >
              {isGenerating ? <Loader2 className="animate-spin" size={20} /> : <Sparkles size={20} />}
              {isGenerating ? 'Generating Content...' : 'Repurpose Now'}
            </button>
          </div>
        </div>
      </div>

      {results.length > 0 && (
        <div>
          <h3 className="mb-3">Generated Results</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {results.map(res => (
              <OutputCard 
                key={res.id} 
                output={res}
                isEditing={editingOutputId === res.id}
                editingContent={editingContent}
                isGeneratingImage={isGeneratingImage === res.id}
                setEditingContent={setEditingContent}
                onEdit={handleEdit}
                onSaveEdit={handleSaveEdit}
                onCopy={handleCopy}
                onGenerateImage={handleGenerateImage}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default RepurposeStudio;
