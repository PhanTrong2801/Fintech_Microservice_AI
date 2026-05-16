import React from 'react';
import { Sparkles, Layers, History, Settings, LogOut } from 'lucide-react';

interface SidebarProps {
  email: string;
  quota: { plan: string; usedQuota: number; monthlyQuota: number; remainingQuota: number } | null;
  activeTab: string;
  setActiveTab: (tab: string) => void;
  setSelectedProject: (project: any | null) => void;
  onLogout: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ email, quota, activeTab, setActiveTab, setSelectedProject, onLogout }) => {
  return (
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
            onClick={() => {
              setActiveTab(item.id);
              if (item.id !== 'history') setSelectedProject(null);
            }}
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
        <div style={{ fontSize: '0.75rem', color: 'var(--accent-secondary)', fontWeight: 'bold', marginBottom: '0.5rem' }}>{quota?.plan || 'FREE'} PLAN</div>
        {quota && (
          <>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: '0.35rem' }}>
              {quota.remainingQuota} / {quota.monthlyQuota} lượt còn lại
            </div>
            <div style={{ width: '100%', height: '4px', background: 'rgba(255,255,255,0.1)', borderRadius: '2px', overflow: 'hidden' }}>
              <div style={{ width: `${Math.max(0, (quota.remainingQuota / quota.monthlyQuota) * 100)}%`, height: '100%', background: quota.remainingQuota > 0 ? 'var(--accent-primary)' : '#ef4444', borderRadius: '2px', transition: 'width 0.5s ease' }} />
            </div>
          </>
        )}
      </div>

      <button 
        onClick={onLogout}
        style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.75rem 1rem', color: 'var(--text-secondary)' }}
      >
        <LogOut size={20} />
        Sign Out
      </button>
    </aside>
  );
};

export default Sidebar;
