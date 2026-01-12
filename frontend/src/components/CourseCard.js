import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, Trash2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const CourseCard = ({ course, selectable = false, onSelect, onDelete }) => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleViewCourse = (e) => {
    e.stopPropagation();
    navigate(`/courses/${course.id}`);
  };

  const handleDelete = (e) => {
    e.stopPropagation();
    if (window.confirm(`Are you sure you want to delete "${course.name}"? This will also delete all questions, answers, and announcements.`)) {
      onDelete && onDelete(course.id);
    }
  };

  // Professors on dashboard only see their own courses, so just check role
  const isOwner = user?.role === 'PROFESSOR';

  return (
    <div
      className="bg-white rounded-xl border-2 border-slate-900 p-6 hover:bg-slate-50 transition-colors group flex flex-col h-full relative"
      style={{ fontFamily: 'Inter, sans-serif' }}
    >
      {/* Delete button for course owner */}
      {isOwner && onDelete && (
        <button
          onClick={handleDelete}
          className="absolute top-3 right-3 p-2 rounded-lg transition-colors hover:bg-red-50 text-slate-400 hover:text-red-500"
          title="Delete course"
        >
          <Trash2 className="w-5 h-5" />
        </button>
      )}
      <div className="flex-1">
        <h3 className="text-xl font-black text-slate-900 mb-3 pr-8">
          {course.name}
        </h3>
        <p className="text-slate-900 font-medium line-clamp-3 mb-6">
          {course.description || "No description available for this course."}
        </p>
      </div>
      {selectable && (
        <button
          onClick={(e) => { e.stopPropagation(); onSelect && onSelect(course.id); }}
          className="w-full mb-3 py-2 border-2 border-slate-900 text-slate-900 font-bold rounded-lg hover:bg-slate-100 transition-colors"
        >
          Select
        </button>
      )}

      <button
        onClick={handleViewCourse}
        className="w-full mt-auto flex items-center justify-center gap-2 bg-slate-900 text-white font-bold py-3 rounded-lg hover:bg-slate-800 transition-all border-2 border-transparent hover:border-slate-900"
      >
        Enter Course
        <ArrowRight className="w-4 h-4" />
      </button>
    </div>
  );
};

export default CourseCard;
