import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus, Mail, Lock, AlertCircle, CheckCircle, GraduationCap, BookOpen, X } from 'lucide-react';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    role: 'STUDENT'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Clear error after 30 seconds (user can also dismiss manually)
  React.useEffect(() => {
    if (error) {
      const timer = setTimeout(() => setError(''), 30000);
      return () => clearTimeout(timer);
    }
  }, [error]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(false);

    // Validation
    if (!formData.email.trim()) {
      setError('Email is required');
      setLoading(false);
      return;
    }
    if (!formData.password.trim()) {
      setError('Password is required');
      setLoading(false);
      return;
    }
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      setLoading(false);
      return;
    }
    if (!formData.role) {
      setError('Please select a role');
      setLoading(false);
      return;
    }

    const result = await register(formData);
    if (result.success) {
      setSuccess(true);
      // Show success message for 2 seconds then redirect to login
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } else {
      setError(result.message || 'Registration failed. Please try again.');
    }
    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 py-12 px-4 sm:px-6 lg:px-8" style={{ fontFamily: 'Inter, sans-serif' }}>
      <div className="max-w-md w-full">
        <div className="bg-white rounded-2xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(15,23,42,1)] p-8 space-y-8">
          <div className="text-center">
            <div className="flex justify-center mb-4">
              <div className="h-16 w-16 bg-slate-900 rounded-xl border-2 border-slate-900 flex items-center justify-center">
                <UserPlus className="h-8 w-8 text-white" />
              </div>
            </div>
            <h2 className="text-4xl font-black text-slate-900 uppercase">Create Account</h2>
            <p className="mt-2 text-sm font-bold text-slate-900">
              Join Askademy today
            </p>
          </div>

          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            {error && (
              <div className="bg-rose-400 border-4 border-slate-900 p-4 rounded-xl">
                <div className="flex items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <AlertCircle className="h-5 w-5 text-slate-900 flex-shrink-0" />
                    <p className="text-sm font-black text-slate-900">{error}</p>
                  </div>
                  <button
                    type="button"
                    onClick={() => setError('')}
                    className="flex-shrink-0 p-1 hover:bg-rose-500 rounded-lg transition-colors"
                    aria-label="Dismiss error"
                  >
                    <X className="h-4 w-4 text-slate-900" />
                  </button>
                </div>
              </div>
            )}
            {success && (
              <div className="bg-emerald-400 border-4 border-slate-900 p-4 rounded-xl">
                <div className="flex items-center gap-2">
                  <CheckCircle className="h-5 w-5 text-slate-900" />
                  <p className="text-sm font-black text-slate-900">Registration successful! Redirecting...</p>
                </div>
              </div>
            )}

            <div className="space-y-4">
              <div>
                <label htmlFor="email" className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">
                  Email Address
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Mail className="h-5 w-5 text-slate-900" />
                  </div>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    className="appearance-none block w-full pl-10 px-4 py-3 border-4 border-slate-900 rounded-xl placeholder-slate-400 text-slate-900 font-bold focus:outline-none focus:ring-4 focus:ring-cyan-400"
                    placeholder="you@example.com"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div>
                <label htmlFor="password" className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">
                  Password
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Lock className="h-5 w-5 text-slate-900" />
                  </div>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="new-password"
                    className="appearance-none block w-full pl-10 px-4 py-3 border-4 border-slate-900 rounded-xl placeholder-slate-400 text-slate-900 font-bold focus:outline-none focus:ring-4 focus:ring-cyan-400"
                    placeholder="••••••••"
                    value={formData.password}
                    onChange={handleChange}
                  />
                </div>
                <p className="mt-2 text-xs font-bold text-slate-600">Must be at least 6 characters</p>
              </div>

              <div>
                <label className="block text-sm font-black text-slate-900 mb-3 uppercase tracking-wide">
                  I am a
                </label>
                <div className="grid grid-cols-2 gap-4">
                  <button
                    type="button"
                    onClick={() => setFormData({ ...formData, role: 'STUDENT' })}
                    className={`flex flex-col items-center gap-3 p-6 rounded-xl border-4 border-slate-900 transition-all ${formData.role === 'STUDENT'
                      ? 'bg-emerald-400 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] translate-x-1 translate-y-1'
                      : 'bg-white hover:bg-slate-50 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)]'
                      }`}
                  >
                    <BookOpen className="w-8 h-8 text-slate-900" />
                    <span className="text-sm font-black text-slate-900 uppercase">Student</span>
                  </button>

                  <button
                    type="button"
                    onClick={() => setFormData({ ...formData, role: 'PROFESSOR' })}
                    className={`flex flex-col items-center gap-3 p-6 rounded-xl border-4 border-slate-900 transition-all ${formData.role === 'PROFESSOR'
                      ? 'bg-rose-400 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] translate-x-1 translate-y-1'
                      : 'bg-white hover:bg-slate-50 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)]'
                      }`}
                  >
                    <GraduationCap className="w-8 h-8 text-slate-900" />
                    <span className="text-sm font-black text-slate-900 uppercase">Professor</span>
                  </button>
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center items-center gap-2 py-4 px-4 border-4 border-slate-900 rounded-xl text-lg font-black text-white bg-slate-900 hover:bg-slate-800 focus:outline-none focus:ring-4 focus:ring-cyan-400 disabled:opacity-50 disabled:cursor-not-allowed transition-all uppercase shadow-[4px_4px_0px_0px_rgba(0,0,0,0.3)]"
            >
              {loading ? (
                <svg className="animate-spin h-6 w-6 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
              ) : (
                <>
                  <UserPlus className="w-5 h-5" />
                  Create Account
                </>
              )}
            </button>

            <div className="text-center">
              <p className="text-sm font-bold text-slate-900">
                Already have an account?{' '}
                <Link to="/login" className="font-black text-cyan-600 hover:text-cyan-700 transition-colors underline">
                  Sign in
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Register;
