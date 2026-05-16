import React from 'react';
import { ChevronLeft } from 'lucide-react';
import type { ContentProject, RepurposedOutput } from '../../types';
import OutputCard from '../shared/OutputCard';
import toast from 'react-hot-toast';
import { api } from '../../api/axios';

interface ProjectsHistoryProps {
  projects: ContentProject[];
  selectedProject: ContentProject | null;
  setSelectedProject: (project: ContentProject | null) => void;
  // Shared editing state
  editingOutputId: number | null;
  editingContent: string;
  isGeneratingImage: number | null;
  setEditingOutputId: (id: number | null) => void;
  setEditingContent: (content: string) => void;
  setIsGeneratingImage: (id: number | null) => void;
  handleCopy: (text: string) => void;
}

const ProjectsHistory: React.FC<ProjectsHistoryProps> = ({
  projects, selectedProject, setSelectedProject,
  editingOutputId, editingContent, isGeneratingImage,
  setEditingOutputId, setEditingContent, setIsGeneratingImage, handleCopy
}) => {

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
      
      if (selectedProject) {
        setSelectedProject({
          ...selectedProject,
          outputs: selectedProject.outputs?.map(item => 
            item.id === outputId ? { ...item, generatedContent: res.data.generatedContent } : item
          )
        });
      }

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
      if (selectedProject) {
        setSelectedProject({
          ...selectedProject,
          outputs: selectedProject.outputs?.map(item => 
            item.id === outputId ? { ...item, imageUrl: res.data.imageUrl } : item
          )
        });
      }
      toast.success('Visual generated successfully!', { id: loadingToast });
    } catch (error) {
      toast.error('Failed to generate image', { id: loadingToast });
    } finally {
      setIsGeneratingImage(null);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
      {selectedProject ? (
        // Project Details View
        <div className="fade-in">
          <button 
            onClick={() => setSelectedProject(null)}
            style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', marginBottom: '1.5rem', transition: 'color 0.2s' }}
            onMouseOver={e => e.currentTarget.style.color = 'white'}
            onMouseOut={e => e.currentTarget.style.color = 'var(--text-secondary)'}
          >
            <ChevronLeft size={20} /> Back to History
          </button>
          <div style={{ marginBottom: '2rem' }}>
            <h1 className="mb-2" style={{ fontSize: '2.5rem' }}>{selectedProject.title}</h1>
            <p className="text-muted">Generated on {new Date(selectedProject.createdAt).toLocaleDateString()}</p>
          </div>
          
          <h3 className="mb-3">Original Content</h3>
          <div className="glass-card mb-4">
            <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', color: 'var(--text-secondary)', fontSize: '0.875rem', maxHeight: '150px', overflowY: 'auto' }}>
              {selectedProject.originalContent}
            </pre>
          </div>

          <h3 className="mb-3">Generated Outputs ({selectedProject.outputs?.length || 0})</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {selectedProject.outputs?.map(res => (
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
            {(!selectedProject.outputs || selectedProject.outputs.length === 0) && (
              <div className="glass-card text-muted text-center">No outputs found for this project.</div>
            )}
          </div>
        </div>
      ) : (
        // Projects List View
        <>
          <h1 className="mb-4" style={{ fontSize: '2.5rem' }}>Projects History</h1>
          {projects.length === 0 ? (
            <div className="text-muted">No projects found. Create one in the Repurpose Studio!</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {projects.map(proj => (
                <div 
                  key={proj.id} 
                  className="glass-card" 
                  style={{ padding: '1.5rem', cursor: 'pointer', transition: 'transform 0.2s, border-color 0.2s' }}
                  onClick={async () => {
                    const loadingToast = toast.loading('Loading outputs...');
                    try {
                      const res = await api.get(`/content/projects/${proj.id}/outputs`);
                      setSelectedProject({ ...proj, outputs: res.data });
                      toast.dismiss(loadingToast);
                    } catch (error) {
                      toast.error('Failed to load project outputs', { id: loadingToast });
                    }
                  }}
                  onMouseOver={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.borderColor = 'var(--accent-primary)'; }}
                  onMouseOut={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.borderColor = 'var(--glass-border)'; }}
                >
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
                    <strong style={{ color: 'var(--accent-secondary)' }}>{proj.outputs?.length || 0}</strong> formats generated.
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ProjectsHistory;
