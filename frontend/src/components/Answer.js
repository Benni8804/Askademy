import React from 'react';
import { useAuth } from '../context/AuthContext';

const Answer = ({ answer, onDelete, onVerify }) => {
  const { user } = useAuth();

  // Check if current user is the author
  const isAuthor = user && answer.author && user.id === answer.author.id;
  const isProfessor = user && user.role === 'PROFESSOR';

  // Format time ago
  const getTimeAgo = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    if (seconds < 60) return 'just now';
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}d ago`;
    return date.toLocaleDateString();
  };

  // Get author initials
  const getInitials = () => {
    if (!answer.author) return 'U';
    const first = answer.author.firstname?.[0] || '';
    const last = answer.author.lastname?.[0] || '';
    return `${first}${last}`.toUpperCase() || answer.author.email?.[0]?.toUpperCase() || 'U';
  };

  return (
    <div
      className={`rounded-xl border transition-all duration-200 ${
        answer.verified
          ? 'bg-emerald-50 border-emerald-200 shadow-sm'
          : 'bg-white border-slate-100 hover:border-slate-200'
      }`}
      style={{ fontFamily: 'Inter, sans-serif' }}
    >
      <div className="p-6">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-3">
            {/* User Avatar */}
            <div className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold text-sm ${
              answer.verified ? 'bg-emerald-100 text-emerald-700' : 'bg-indigo-100 text-indigo-700'
            }`}>
              {getInitials()}
            </div>

            {/* User Info */}
            <div>
              <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-slate-900">
                  {answer.author?.firstname} {answer.author?.lastname}
                </span>
                {answer.verified && (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 text-xs font-medium">
                    <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    Verified
                  </span>
                )}
              </div>
              <span className="text-xs text-slate-500">
                {getTimeAgo(answer.createdAt)}
              </span>
            </div>
          </div>

          {/* Verified Badge (Top Right) */}
          {answer.verified && (
            <div className="flex-shrink-0">
              <div className="w-8 h-8 rounded-full bg-emerald-500 flex items-center justify-center shadow-sm">
                <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
              </div>
            </div>
          )}
        </div>

        {/* Answer Content */}
        <div className="prose prose-sm max-w-none mb-4">
          <p className="text-slate-700 leading-relaxed whitespace-pre-wrap">
            {answer.content}
          </p>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-3 pt-4 border-t border-slate-100">
          {/* Verify Button (Professor Only) */}
          {isProfessor && onVerify && (
            <button
              onClick={() => onVerify(answer.id)}
              className={`flex items-center gap-2 px-3 py-1.5 text-xs font-medium rounded-lg transition-colors duration-200 ${
                answer.verified
                  ? 'text-emerald-700 bg-emerald-100 hover:bg-emerald-200'
                  : 'text-slate-600 bg-slate-100 hover:bg-slate-200'
              }`}
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {answer.verified ? 'Unverify' : 'Verify Answer'}
            </button>
          )}

          {/* Delete Button (Author or Professor) */}
          {(isAuthor || isProfessor) && onDelete && (
            <button
              onClick={() => onDelete(answer.id)}
              className="flex items-center gap-2 px-3 py-1.5 text-xs font-medium text-slate-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors duration-200"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              Delete
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default Answer;
