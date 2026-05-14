import { useState, useEffect } from 'react'
import { Sparkles, Layers, History, Settings, LogOut, Loader2, CheckCircle2, Image as ImageIcon } from 'lucide-react'
import toast, { Toaster } from 'react-hot-toast'
import { api } from './api/axios'
import type { OutputFormat, ContentType, RepurposedOutput, ContentProject } from './types/index'
import './index.css'

function App() {
  // Auth State
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false)
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [isAuthLoading, setIsAuthLoading] = useState(false)

  // App State
  const [activeTab, setActiveTab] = useState('repurpose')
  const [projects, setProjects] = useState<ContentProject[]>([])
  
  // Repurpose Studio State
  const [title, setTitle] = useState('')
  const [inputText, setInputText] = useState('')
  const [contentType, setContentType] = useState<ContentType>('BLOG_POST')
  const [selectedFormats, setSelectedFormats] = useState<OutputFormat[]>([])
  const [isGenerating, setIsGenerating] = useState(false)
  const [results, setResults] = useState<RepurposedOutput[]>([])
  const [isGeneratingImage, setIsGeneratingImage] = useState<number | null>(null) // ID of output being generated


  // Available formats list
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

  // Auto-login on mount if token exists
  useEffect(() => {
    const token = localStorage.getItem('token')
    const savedEmail = localStorage.getItem('email')
    if (token && savedEmail) {
      setIsAuthenticated(true)
      setEmail(savedEmail)
    }

    // Handle global 401
    const handleUnauthorized = () => {
      setIsAuthenticated(false)
      toast.error('Session expired. Please login again.')
    }
    window.addEventListener('unauthorized', handleUnauthorized)
    return () => window.removeEventListener('unauthorized', handleUnauthorized)
  }, [])

  // Fetch projects when switching to history tab
  useEffect(() => {
    if (isAuthenticated && activeTab === 'history') {
      fetchProjects()
    }
  }, [isAuthenticated, activeTab])

  const fetchProjects = async () => {
    try {
      const res = await api.get(`/content/projects?email=${email}`)
      setProjects(res.data)
    } catch (error) {
      toast.error('Failed to load projects history')
    }
  }

  // Handle Authentication (Login & Register)
  const handleAuth = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email || !password || (authMode === 'register' && !fullName)) {
      toast.error('Please fill in all fields')
      return
    }

    setIsAuthLoading(true)
    try {
      if (authMode === 'register') {
        const res = await api.post('/identity/auth/register', { 
          email, 
          password, 
          fullName 
        })
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('email', email)
        setIsAuthenticated(true)
        toast.success('Registration successful! Workspace created.')
      } else {
        const res = await api.post('/identity/auth/login', { email, password })
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('email', email)
        setIsAuthenticated(true)
        toast.success('Logged in successfully!')
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Authentication failed. Please check your credentials.')
    } finally {
      setIsAuthLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    setIsAuthenticated(false)
    setResults([])
    setTitle('')
    setInputText('')
    toast.success('Logged out')
  }

  const toggleFormat = (format: OutputFormat) => {
    if (selectedFormats.includes(format)) {
      setSelectedFormats(selectedFormats.filter(f => f !== format))
    } else {
      setSelectedFormats([...selectedFormats, format])
    }
  }

  // Core Handle Generate
  const handleGenerate = async () => {
    if (!title.trim() || !inputText.trim()) {
      toast.error('Please provide a title and original content')
      return
    }
    if (selectedFormats.length === 0) {
      toast.error('Please select at least one output format')
      return
    }

    setIsGenerating(true)
    setResults([]) // clear old results
    const loadingToast = toast.loading('Step 1/2: Saving content project...')
    
    try {
      // 1. Create Project
      const projectRes = await api.post(`/content/projects?email=${email}`, {
        title,
        originalContent: inputText,
        contentType
      })
      const projectId = projectRes.data.id

      // 2. Trigger Repurpose
      toast.loading('Step 2/2: AI is generating content... (This might take a few seconds)', { id: loadingToast })
      
      const repurposeRes = await api.post(`/content/projects/${projectId}/repurpose`, {
        formats: selectedFormats
      })

      // Update UI with results
      setResults(repurposeRes.data)
      toast.success('Content generated successfully!', { id: loadingToast })
      
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to generate content', { id: loadingToast })
    } finally {
      setIsGenerating(false)
    }
  }

  const handleGenerateImage = async (outputId: number) => {
    setIsGeneratingImage(outputId)
    const loadingToast = toast.loading('AI is imagining your visual...')
    try {
      const res = await api.post(`/content/outputs/${outputId}/generate-image`)
      
      // Update the specific result with the new imageUrl
      setResults(prev => prev.map(item => 
        item.id === outputId ? { ...item, imageUrl: res.data.imageUrl } : item
      ))
      
      toast.success('Visual generated successfully!', { id: loadingToast })
    } catch (error) {
      toast.error('Failed to generate image', { id: loadingToast })
    } finally {
      setIsGeneratingImage(null)
    }
  }

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text)
    toast.success('Copied to clipboard!')
  }

  if (!isAuthenticated) {
    return (
      <div className="login-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', padding: '1rem' }}>
        <Toaster position="top-center" />
        <div className="glass-card" style={{ maxWidth: '400px', width: '100%' }}>
          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
            <Sparkles size={48} color="var(--accent-primary)" style={{ marginBottom: '1rem' }} />
            <h1 className="text-gradient mb-1">Repurpose AI</h1>
            <p className="text-muted">Turn 1 piece of content into 10 formats instantly.</p>
          </div>
          
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
            <button 
              onClick={() => setAuthMode('login')}
              style={{ flex: 1, padding: '0.5rem', borderBottom: authMode === 'login' ? '2px solid var(--accent-primary)' : '2px solid transparent', color: authMode === 'login' ? 'white' : 'var(--text-muted)' }}
            >
              Sign In
            </button>
            <button 
              onClick={() => setAuthMode('register')}
              style={{ flex: 1, padding: '0.5rem', borderBottom: authMode === 'register' ? '2px solid var(--accent-primary)' : '2px solid transparent', color: authMode === 'register' ? 'white' : 'var(--text-muted)' }}
            >
              Sign Up
            </button>
          </div>

          <form onSubmit={handleAuth} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {authMode === 'register' && (
              <div>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Full Name</label>
                <input 
                  type="text" 
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="John Doe"
                  style={{ width: '100%', padding: '0.75rem', borderRadius: '0.375rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white' }}
                />
              </div>
            )}
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Email</label>
              <input 
                type="email" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@domain.com"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '0.375rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Password</label>
              <input 
                type="password" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '0.375rem', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--glass-border)', color: 'white' }}
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }} disabled={isAuthLoading}>
              {isAuthLoading ? <Loader2 className="animate-spin" size={20} /> : null}
              {authMode === 'login' ? 'Sign In to Workspace' : 'Create Free Account'}
            </button>
          </form>
        </div>
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <Toaster position="top-right" />
      {/* Sidebar */}
      <aside style={{ width: '260px', background: 'var(--bg-secondary)', borderRight: '1px solid var(--glass-border)', padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '3rem' }}>
          <Sparkles color="var(--accent-primary)" />
          <h2 style={{ fontSize: '1.25rem', fontWeight: 'bold' }}>Repurpose AI</h2>
        </div>

        <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', flex: 1 }}>
          {[
            { id: 'repurpose', icon: Layers, label: 'Repurpose Studio' },
            { id: 'history', icon: History, label: 'Projects History' },
            { id: 'settings', icon: Settings, label: 'Settings & Quota' },
          ].map(item => (
            <button 
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              style={{ 
                display: 'flex', alignItems: 'center', gap: '0.75rem', 
                padding: '0.75rem 1rem', borderRadius: '0.375rem',
                color: activeTab === item.id ? 'white' : 'var(--text-secondary)',
                background: activeTab === item.id ? 'rgba(99, 102, 241, 0.15)' : 'transparent',
                textAlign: 'left'
              }}
            >
              <item.icon size={20} color={activeTab === item.id ? 'var(--accent-primary)' : 'currentColor'} />
              {item.label}
            </button>
          ))}
        </nav>

        <div style={{ padding: '1rem', background: 'var(--glass-bg)', borderRadius: '0.5rem', marginBottom: '1rem' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginBottom: '0.5rem', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {email}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--accent-secondary)', fontWeight: 'bold', marginBottom: '0.5rem' }}>FREE PLAN</div>
        </div>

        <button 
          onClick={handleLogout}
          style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.75rem 1rem', color: 'var(--text-secondary)' }}
        >
          <LogOut size={20} />
          Sign Out
        </button>
      </aside>

      {/* Main Content */}
      <main style={{ flex: 1, padding: '2rem 3rem', overflowY: 'auto' }}>
        {activeTab === 'repurpose' && (
          <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
            <h1 className="mb-1" style={{ fontSize: '2.5rem' }}>Repurpose Studio</h1>
            <p className="text-muted mb-4">Paste your original content and let AI transform it instantly.</p>

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
                  {results.map((res, i) => (
                    <div key={i} className="glass-card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem', paddingBottom: '1rem', borderBottom: '1px solid var(--glass-border)' }}>
                        <span style={{ fontWeight: 'bold', color: 'var(--accent-secondary)' }}>{res.outputFormat.replace(/_/g, ' ')}</span>
                        <div style={{ display: 'flex', gap: '1rem' }}>
                          <button 
                            onClick={() => handleGenerateImage(res.id)} 
                            disabled={isGeneratingImage !== null}
                            style={{ color: 'var(--accent-primary)', fontSize: '0.875rem', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
                          >
                            {isGeneratingImage === res.id ? <Loader2 className="animate-spin" size={14} /> : <ImageIcon size={14} />}
                            {res.imageUrl ? 'Regenerate Image' : 'Generate Image'}
                          </button>
                          <button onClick={() => handleCopy(res.generatedContent)} style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', fontWeight: '500' }}>
                            Copy Text
                          </button>
                        </div>
                      </div>
                      
                      {res.imageUrl && (
                        <div style={{ marginBottom: '1.5rem', borderRadius: '0.5rem', overflow: 'hidden', border: '1px solid var(--glass-border)', animation: 'scaleUp 0.5s ease-out' }}>
                          <img 
                            src={res.imageUrl} 
                            alt="Generated AI Visual" 
                            style={{ width: '100%', height: 'auto', display: 'block' }} 
                            loading="lazy"
                          />
                        </div>
                      )}

                      <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', color: 'var(--text-primary)', background: 'rgba(0,0,0,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
                        {res.generatedContent}
                      </pre>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'history' && (
          <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
            <h1 className="mb-4" style={{ fontSize: '2.5rem' }}>Projects History</h1>
            {projects.length === 0 ? (
              <div className="text-muted">No projects found. Create one in the Repurpose Studio!</div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {projects.map(proj => (
                  <div key={proj.id} className="glass-card" style={{ padding: '1.5rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                      <h3 style={{ fontSize: '1.25rem', margin: 0 }}>{proj.title}</h3>
                      <span style={{ fontSize: '0.75rem', background: 'rgba(255,255,255,0.1)', padding: '0.25rem 0.5rem', borderRadius: '1rem' }}>
                        {proj.contentType}
                      </span>
                    </div>
                    <div style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginBottom: '1rem' }}>
                      Created on: {new Date(proj.createdAt).toLocaleDateString()}
                    </div>
                    <div style={{ fontSize: '0.875rem' }}>
                      <strong>{proj.outputs?.length || 0}</strong> formats generated.
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'settings' && (
          <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
            <h1 className="mb-4" style={{ fontSize: '2.5rem' }}>Settings</h1>
            <div className="glass-card">
              <h3 className="mb-2">Account details</h3>
              <p className="text-muted mb-4">Email: {email}</p>
              
              <h3 className="mb-2">Subscription</h3>
              <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1.5rem', borderRadius: '0.5rem', border: '1px solid var(--accent-primary)' }}>
                <h4 style={{ color: 'var(--accent-primary)', marginBottom: '0.5rem' }}>FREE PLAN</h4>
                <p className="text-muted mb-2">You can generate up to 5 projects per month.</p>
                <button className="btn btn-primary" style={{ marginTop: '1rem' }}>Upgrade to PRO</button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
