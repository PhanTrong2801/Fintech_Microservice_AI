import React from 'react';

interface SettingsViewProps {
  email: string;
  quota: { plan: string; usedQuota: number; monthlyQuota: number; remainingQuota: number } | null;
}

const SettingsView: React.FC<SettingsViewProps> = ({ email, quota }) => {
  return (
    <div className="container" style={{ maxWidth: '900px', margin: '0 auto' }}>
      <h1 className="mb-4" style={{ fontSize: '2.5rem' }}>Settings & Quota</h1>
      <div className="glass-card">
        <h3 className="mb-2">Account Details</h3>
        <p className="text-muted mb-4">Email: {email}</p>
        
        <h3 className="mb-2">Subscription & Quota</h3>
        <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1.5rem', borderRadius: '0.5rem', border: '1px solid var(--accent-primary)' }}>
          <h4 style={{ color: 'var(--accent-primary)', marginBottom: '0.5rem' }}>{quota?.plan || 'FREE'} PLAN</h4>
          {quota ? (
            <>
              <p className="text-muted mb-2">Đã sử dụng: <strong style={{ color: 'white' }}>{quota.usedQuota}</strong> / {quota.monthlyQuota} lượt trong tháng này.</p>
              <p className="text-muted mb-2">Còn lại: <strong style={{ color: quota.remainingQuota > 0 ? 'var(--accent-secondary)' : '#ef4444' }}>{quota.remainingQuota}</strong> lượt.</p>
              <div style={{ width: '100%', height: '8px', background: 'rgba(255,255,255,0.1)', borderRadius: '4px', overflow: 'hidden', marginBottom: '1rem' }}>
                <div style={{ width: `${Math.max(0, (quota.remainingQuota / quota.monthlyQuota) * 100)}%`, height: '100%', background: quota.remainingQuota > 0 ? 'var(--accent-primary)' : '#ef4444', borderRadius: '4px', transition: 'width 0.5s ease' }} />
              </div>
            </>
          ) : (
            <p className="text-muted mb-2">Loading quota...</p>
          )}
          <button className="btn btn-primary" style={{ marginTop: '1rem' }}>Upgrade to PRO</button>
        </div>
      </div>
    </div>
  );
};

export default SettingsView;
