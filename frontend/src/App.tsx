import { useState, useEffect } from 'react'
import { Toaster } from 'react-hot-toast'
import { api } from './api/axios'
import type { ContentProject } from './types'

// Components
import LoginView from './components/auth/LoginView'
import Sidebar from './components/layout/Sidebar'
import RepurposeStudio from './components/studio/RepurposeStudio'
import ProjectsHistory from './components/history/ProjectsHistory'
import SettingsView from './components/settings/SettingsView'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [email, setEmail] = useState('')
  const [activeTab, setActiveTab] = useState('repurpose')
  const [projects, setProjects] = useState<ContentProject[]>([])
  
  // Quota State
  const [quota, setQuota] = useState<{plan: string, usedQuota: number, monthlyQuota: number, remainingQuota: number} | null>(null)

  // Project Details & Editing State (Shared across tabs)
  const [selectedProject, setSelectedProject] = useState<ContentProject | null>(null)
  const [editingOutputId, setEditingOutputId] = useState<number | null>(null)
  const [editingContent, setEditingContent] = useState('')
  const [isGeneratingImage, setIsGeneratingImage] = useState<number | null>(null)

  const fetchQuota = async () => {
    if (!email) return;
    try {
      const res = await api.get(`/identity/users/me/quota?email=${email}`)
      setQuota(res.data)
    } catch (error) {
      console.error("Failed to fetch quota", error)
    }
  }

  const fetchProjects = async () => {
    if (!email) return;
    try {
      const res = await api.get(`/content/projects?email=${email}`)
      setProjects(res.data)
    } catch (error) {
      console.error("Failed to fetch projects", error)
    }
  }

  // Effect runs when email changes (after login/mount)
  useEffect(() => {
    if (email) {
      fetchQuota()
      fetchProjects()
    }
  }, [email])

  // Fetch projects when switching to history tab
  useEffect(() => {
    if (activeTab === 'history') {
      fetchProjects()
    }
  }, [activeTab])

  // Auto-login on mount if token exists
  useEffect(() => {
    const token = localStorage.getItem('token')
    const savedEmail = localStorage.getItem('email')
    if (token && savedEmail) {
      setIsAuthenticated(true)
      setEmail(savedEmail)
    }

    const handleUnauthorized = () => {
      setIsAuthenticated(false)
      localStorage.removeItem('token')
      localStorage.removeItem('email')
    }
    window.addEventListener('unauthorized', handleUnauthorized)
    return () => window.removeEventListener('unauthorized', handleUnauthorized)
  }, [])

  const handleLoginSuccess = (userEmail: string, token: string) => {
    localStorage.setItem('token', token)
    localStorage.setItem('email', userEmail)
    setEmail(userEmail)
    setIsAuthenticated(true)
    setActiveTab('repurpose')
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    setIsAuthenticated(false)
    setEmail('')
    setQuota(null)
    setProjects([])
    setSelectedProject(null)
  }

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text)
    import('react-hot-toast').then(({ default: toast }) => toast.success('Copied to clipboard!'))
  }

  if (!isAuthenticated) {
    return <LoginView onLoginSuccess={handleLoginSuccess} />
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: 'var(--bg-primary)' }}>
      <Toaster position="top-center" />
      
      <Sidebar 
        email={email}
        quota={quota}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        setSelectedProject={setSelectedProject}
        onLogout={handleLogout}
      />

      <main style={{ flex: 1, padding: '2rem', overflowY: 'auto' }}>
        {activeTab === 'repurpose' && (
          <RepurposeStudio 
            email={email}
            fetchQuota={fetchQuota}
            editingOutputId={editingOutputId}
            editingContent={editingContent}
            isGeneratingImage={isGeneratingImage}
            setEditingOutputId={setEditingOutputId}
            setEditingContent={setEditingContent}
            setIsGeneratingImage={setIsGeneratingImage}
            handleCopy={handleCopy}
          />
        )}

        {activeTab === 'history' && (
          <ProjectsHistory 
            projects={projects}
            selectedProject={selectedProject}
            setSelectedProject={setSelectedProject}
            editingOutputId={editingOutputId}
            editingContent={editingContent}
            isGeneratingImage={isGeneratingImage}
            setEditingOutputId={setEditingOutputId}
            setEditingContent={setEditingContent}
            setIsGeneratingImage={setIsGeneratingImage}
            handleCopy={handleCopy}
          />
        )}

        {activeTab === 'settings' && (
          <SettingsView 
            email={email}
            quota={quota}
          />
        )}
      </main>
    </div>
  )
}

export default App
