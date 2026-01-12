import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { courseService } from '../services/services';
import { Edit, Save, X, FileText } from 'lucide-react';

const GuideTab = ({ courseId, course }) => {
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const [gradingInfo, setGradingInfo] = useState(course?.gradingInfo || '');
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setGradingInfo(course?.gradingInfo || '');
  }, [course]);

  const handleSave = async () => {
    setLoading(true);
    try {
      await courseService.updateGradingInfo(courseId, gradingInfo);
      setIsEditing(false);
    } catch (error) {
      console.error('Error updating grading info:', error);
      alert('Failed to update grading information');
    } finally {
      setLoading(false);
    }
  };

  const isProfessor = user?.role === 'PROFESSOR';
  const hasGradingInfo = gradingInfo && gradingInfo.trim() !== '';

  return (
    <div className={`p-8 ${darkMode ? 'bg-slate-800' : 'bg-white'}`} style={{ fontFamily: 'Inter, sans-serif' }}>
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <h2 className={`text-3xl font-black uppercase flex items-center gap-3 ${darkMode ? 'text-white' : 'text-slate-900'}`}>
          <FileText className="w-8 h-8" />
          GRADING GUIDE
        </h2>
        {isProfessor && !isEditing && (
          <button
            onClick={() => setIsEditing(true)}
            className={`flex items-center gap-2 px-6 py-3 rounded-xl font-black hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] border-2 transition-all uppercase tracking-wide ${
              darkMode
                ? 'bg-cyan-400 text-slate-900 border-white'
                : 'bg-indigo-600 text-white border-slate-900'
            }`}
          >
            <Edit className="w-5 h-5" />
            {hasGradingInfo ? 'Edit' : 'Add Guide'}
          </button>
        )}
      </div>

      {isEditing ? (
        <div className={`rounded-2xl border-4 p-6 ${darkMode ? 'bg-slate-700 border-white' : 'bg-white border-slate-900'}`}>
          <label className={`block text-sm font-black mb-3 uppercase tracking-wide ${darkMode ? 'text-white' : 'text-slate-900'}`}>
            Grading Information
          </label>
          <textarea
            rows="12"
            className={`w-full px-4 py-3 border-4 rounded-xl focus:outline-none focus:ring-4 mb-4 font-medium resize-none ${
              darkMode
                ? 'bg-slate-600 border-slate-900 text-white focus:ring-cyan-400 placeholder-slate-400'
                : 'bg-white border-slate-900 text-slate-900 focus:ring-cyan-400 placeholder-slate-400'
            }`}
            placeholder="Enter grading criteria, exam schedule, project requirements, etc..."
            value={gradingInfo}
            onChange={(e) => setGradingInfo(e.target.value)}
          />
          <div className="flex gap-4">
            <button
              onClick={() => {
                setGradingInfo(course?.gradingInfo || '');
                setIsEditing(false);
              }}
              className={`flex-1 flex items-center justify-center gap-2 px-5 py-3 border-4 rounded-xl font-black hover:bg-opacity-80 transition-all uppercase tracking-wide ${
                darkMode
                  ? 'border-slate-900 text-white bg-slate-600'
                  : 'border-slate-900 text-slate-900 bg-white hover:bg-slate-100'
              }`}
            >
              <X className="w-5 h-5" />
              Cancel
            </button>
            <button
              onClick={handleSave}
              disabled={loading}
              className={`flex-1 flex items-center justify-center gap-2 px-5 py-3 border-4 rounded-xl font-black transition-all disabled:opacity-50 uppercase tracking-wide ${
                darkMode
                  ? 'bg-cyan-400 text-slate-900 border-white'
                  : 'bg-slate-900 text-white border-slate-900 hover:bg-slate-800'
              }`}
            >
              <Save className="w-5 h-5" />
              {loading ? 'Saving...' : 'Save'}
            </button>
          </div>
        </div>
      ) : (
        <div className={`rounded-2xl border-4 p-8 ${darkMode ? 'bg-slate-700 border-white' : 'bg-white border-slate-900'}`}>
          {hasGradingInfo ? (
            <div className="prose max-w-none">
              <pre className={`whitespace-pre-wrap font-sans text-base leading-relaxed ${darkMode ? 'text-slate-200' : 'text-slate-900'}`}>
                {gradingInfo}
              </pre>
            </div>
          ) : (
            <div className="text-center py-16">
              <FileText className={`mx-auto h-16 w-16 mb-4 opacity-30 ${darkMode ? 'text-white' : 'text-slate-900'}`} />
              <h3 className={`text-2xl font-black mb-2 ${darkMode ? 'text-white' : 'text-slate-900'}`}>
                NO GRADING INFORMATION YET
              </h3>
              <p className={`font-bold ${darkMode ? 'text-slate-400' : 'text-slate-600'}`}>
                {isProfessor 
                  ? 'Click "Add Guide" to add grading criteria' 
                  : 'Check back later for grading information'}
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default GuideTab;
