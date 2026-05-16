import React, { useState } from 'react';
import { Sparkles, Loader2 } from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';
import { api } from '../../api/axios';

interface LoginViewProps {
  onLoginSuccess: (email: string, token: string) => void;
}

const LoginView: React.FC<LoginViewProps> = ({ onLoginSuccess }) => {
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [isAuthLoading, setIsAuthLoading] = useState(false);

  const handleAuth = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password || (authMode === 'register' && !fullName)) {
      toast.error('Please fill in all fields');
      return;
    }

    setIsAuthLoading(true);
    try {
      if (authMode === 'register') {
        const res = await api.post('/identity/auth/register', { 
          email, 
          password, 
          fullName 
        });
        toast.success('Registration successful! Workspace created.');
        onLoginSuccess(email, res.data.token);
      } else {
        const res = await api.post('/identity/auth/login', { email, password });
        toast.success('Logged in successfully!');
        onLoginSuccess(email, res.data.token);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Authentication failed. Please check your credentials.');
    } finally {
      setIsAuthLoading(false);
    }
  };

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
  );
};

export default LoginView;
