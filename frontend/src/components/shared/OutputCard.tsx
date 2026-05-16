import React from 'react';
import { Loader2, Image as ImageIcon, Edit3, Save, Copy } from 'lucide-react';
import type { RepurposedOutput } from '../../types';

interface OutputCardProps {
  output: RepurposedOutput;
  isEditing: boolean;
  editingContent: string;
  isGeneratingImage: boolean;
  setEditingContent: (content: string) => void;
  onEdit: (output: RepurposedOutput) => void;
  onSaveEdit: (outputId: number) => void;
  onCopy: (content: string) => void;
  onGenerateImage: (outputId: number) => void;
}

const OutputCard: React.FC<OutputCardProps> = ({
  output,
  isEditing,
  editingContent,
  isGeneratingImage,
  setEditingContent,
  onEdit,
  onSaveEdit,
  onCopy,
  onGenerateImage,
}) => {
  return (
    <div className="glass-card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', paddingBottom: '1rem', borderBottom: '1px solid var(--glass-border)', flexWrap: 'wrap', gap: '1rem' }}>
        <span style={{ fontWeight: 'bold', color: 'var(--accent-secondary)' }}>{output.outputFormat.replace(/_/g, ' ')}</span>
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <button 
            onClick={() => onGenerateImage(output.id)} 
            disabled={isGeneratingImage || isEditing}
            style={{ color: 'var(--accent-primary)', fontSize: '0.875rem', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '0.25rem', opacity: isEditing ? 0.5 : 1 }}
          >
            {isGeneratingImage ? <Loader2 className="animate-spin" size={14} /> : <ImageIcon size={14} />}
            {output.imageUrl ? 'Regenerate Image' : 'Generate Image'}
          </button>
          
          {isEditing ? (
            <button onClick={() => onSaveEdit(output.id)} style={{ color: '#10b981', fontSize: '0.875rem', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
              <Save size={14} /> Save
            </button>
          ) : (
            <button onClick={() => onEdit(output)} style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
              <Edit3 size={14} /> Edit
            </button>
          )}

          <button 
            onClick={() => onCopy(output.generatedContent)} 
            style={{ background: 'rgba(255,255,255,0.1)', padding: '0.25rem 0.75rem', borderRadius: '0.25rem', color: 'white', fontSize: '0.875rem', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '0.25rem', transition: 'background 0.2s' }} 
            onMouseOver={e => e.currentTarget.style.background = 'rgba(255,255,255,0.2)'} 
            onMouseOut={e => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
          >
            <Copy size={14} /> Copy
          </button>
        </div>
      </div>
      
      {output.imageUrl && !isEditing && (
        <div style={{ marginBottom: '1.5rem', borderRadius: '0.5rem', overflow: 'hidden', border: '1px solid var(--glass-border)' }}>
          <img 
            src={output.imageUrl} 
            alt="Generated AI Visual" 
            style={{ width: '100%', height: 'auto', display: 'block' }} 
            loading="lazy"
          />
        </div>
      )}

      {isEditing ? (
        <textarea
          value={editingContent}
          onChange={(e) => setEditingContent(e.target.value)}
          style={{ width: '100%', minHeight: '300px', background: 'rgba(0,0,0,0.3)', border: '1px solid var(--accent-primary)', color: 'white', padding: '1rem', fontSize: '1rem', borderRadius: '0.5rem', outline: 'none', resize: 'vertical' }}
          autoFocus
        />
      ) : (
        <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', color: 'var(--text-primary)', background: 'rgba(0,0,0,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
          {output.generatedContent}
        </pre>
      )}
    </div>
  );
};

export default OutputCard;
