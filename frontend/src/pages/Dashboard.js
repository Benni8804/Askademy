import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { courseService, questionService, answerService } from '../services/services';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { PlusCircle, Search, CheckCircle, AlertCircle } from 'lucide-react';
import CourseCard from '../components/CourseCard';
import { parseErrorMessage } from '../utils/errorMessages';

const Dashboard = () => {
  const [courses, setCourses] = useState([]);
  const [allCourses, setAllCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEnrollModal, setShowEnrollModal] = useState(false);
  const [enrollCode, setEnrollCode] = useState('');
  const [enrollError, setEnrollError] = useState('');
  const [createCourseError, setCreateCourseError] = useState('');
  const [courseForm, setCourseForm] = useState({ name: '', description: '' });
  const [selectedCourseId, setSelectedCourseId] = useState(null);
  const [pendingCount, setPendingCount] = useState(0);
  const [verifiedCount, setVerifiedCount] = useState(0);
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const navigate = useNavigate();

  const isProfessor = user?.role === 'PROFESSOR';
  const isStudent = user?.role === 'STUDENT';

  const isAdmin = user?.role === 'ADMIN';

  // Ensure courses load after user context is ready
  useEffect(() => {
    // Redirect admin to admin dashboard immediately
    if (isAdmin) {
      navigate('/admin', { replace: true });
      return;
    }

    const load = async () => {
      setLoading(true);
      await fetchCourses();
      if (isStudent) {
        await fetchAllCourses();
      }
      setLoading(false);
    };
    if (user) {
      load();
    }
    // eslint-disable-next-line
  }, [user, isAdmin]);

  const fetchCourses = async () => {
    try {
      const endpoint = isProfessor
        ? courseService.getProfessorCourses
        : courseService.getStudentCourses;
      const response = await endpoint();
      setCourses(response.data);
    } catch (error) {
      // Silently fail - user will see "No courses" message
      // Log for debugging in development
      if (process.env.NODE_ENV === 'development') {
        console.warn('Failed to fetch courses:', parseErrorMessage(error));
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchAllCourses = async () => {
    try {
      const response = await courseService.getAllCourses();
      setAllCourses(response.data);
    } catch (error) {
      if (process.env.NODE_ENV === 'development') {
        console.warn('Failed to fetch available courses:', parseErrorMessage(error));
      }
    }
  };

  // Fetch counts for the selected course
  useEffect(() => {
    const fetchCounts = async () => {
      if (!selectedCourseId) {
        // Reset counts when deselected
        setPendingCount(0);
        setVerifiedCount(0);
        return;
      }
      try {
        // Pending = unanswered questions
        const unansweredRes = await questionService.getQuestions(selectedCourseId, 'unanswered');
        const pending = Array.isArray(unansweredRes.data) ? unansweredRes.data.length : (unansweredRes.data?.length || 0);

        // Verified = answers with verified=true across all questions for course
        const allQuestionsRes = await questionService.getQuestions(selectedCourseId);
        const questions = Array.isArray(allQuestionsRes.data) ? allQuestionsRes.data : (allQuestionsRes.data || []);
        const answersArrays = await Promise.all(
          questions.map(q => answerService.getAnswers(q.id).then(r => r.data).catch(() => []))
        );
        const verified = answersArrays.reduce((acc, arr) => acc + arr.filter(a => a.verified === true).length, 0);

        setPendingCount(pending);
        setVerifiedCount(verified);
      } catch (error) {
        setPendingCount(0);
        setVerifiedCount(0);
        if (process.env.NODE_ENV === 'development') {
          console.warn('Failed to fetch course statistics:', parseErrorMessage(error));
        }
      }
    };
    fetchCounts();
  }, [selectedCourseId]);

  const handleCreateCourse = async (event) => {
    event.preventDefault();
    setCreateCourseError('');
    try {
      await courseService.createCourse(courseForm);
      setShowCreateModal(false);
      setCourseForm({ name: '', description: '' });
      await fetchCourses();
      // Success feedback handled by UI
    } catch (error) {
      const errorMessage = parseErrorMessage(error);
      setCreateCourseError(errorMessage);
    }
  };

  const handleEnroll = async (courseId) => {
    try {
      await courseService.enrollInCourse(courseId);
      navigate(`/courses/${courseId}`);
    } catch (error) {
      console.error('Failed to enroll', error);
      // const errorMessage = parseErrorMessage(error); // Removed as createCourseError is removed
      // setCreateCourseError(errorMessage); // Removed as createCourseError is removed
    }
  };

  const handleEnrollByCode = async (event) => {
    event.preventDefault();
    setEnrollError('');

    if (!enrollCode || enrollCode.length !== 8) {
      setEnrollError('Course codes must be exactly 8 characters long.');
      return;
    }

    try {
      await courseService.enrollByCourseCode(enrollCode);
      setShowEnrollModal(false);
      setEnrollCode('');
      await fetchCourses();
    } catch (error) {
      const errorMessage = parseErrorMessage(error);
      setEnrollError(errorMessage);
    }
  };

  const handleDeleteCourse = async (courseId) => {
    try {
      await courseService.deleteCourse(courseId);
      await fetchCourses();
      setSelectedCourseId(null);
    } catch (error) {
      console.error('Failed to delete course:', error);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-slate-900 mx-auto"></div>
          <p className="mt-4 text-slate-900 font-bold" style={{ fontFamily: 'Inter, sans-serif' }}>Loading...</p>
        </div>
      </div>
    );
  }

  const enrolledCourseIds = courses.map(c => c.id);
  const displayedCourses = courses;

  return (
    <div
      className={`min-h-screen ${darkMode ? 'bg-slate-900' : 'bg-white'}`}
      style={{ fontFamily: 'Inter, sans-serif' }}
      onClick={() => setSelectedCourseId(null)}
    >
      {/* Thin separator under navbar in dark mode */}
      {darkMode && <div className="border-t border-white h-px" />}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 p-8 max-w-[1600px] mx-auto">

        {/* Column 1: Course Hub (Span 3) - Neo Brutalist Panel */}
        <div className={`lg:col-span-3 rounded-3xl border-4 shadow-[8px_8px_0px_0px_rgba(15,23,42,1)] p-8 min-h-[80vh] ${darkMode ? 'bg-indigo-900 border-white' : 'bg-indigo-900 border-slate-900'}`}>
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-4xl font-black text-white tracking-tight">
              My Courses
            </h2>
          </div>

          {displayedCourses.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-96 text-center">
              <div className="text-8xl mb-6 opacity-20 grayscale">📚</div>
              <h3 className={`text-2xl font-bold mb-2 ${darkMode ? 'text-cyan-400' : 'text-indigo-200'}`}>
                No courses found
              </h3>
              <p className={`font-medium ${darkMode ? 'text-slate-400' : 'text-indigo-300'}`}>
                {isProfessor
                  ? "Create a course to get started."
                  : "Enter a course code to enroll."}
              </p>
            </div>
          ) : (
            <div className="max-h-[calc(80vh-120px)] overflow-y-auto px-1 py-1" onClick={(e) => e.stopPropagation()}>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pb-2">
                {displayedCourses.map(course => (
                  <div key={course.id} className={selectedCourseId === course.id ? 'ring-4 ring-cyan-400 rounded-xl' : ''}>
                    <div
                      className="cursor-pointer"
                      onClick={() => setSelectedCourseId(course.id)}
                    >
                      <CourseCard course={course} onDelete={handleDeleteCourse} />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Column 2: Action Stack (Span 1) */}
        <div className="lg:col-span-1 flex flex-col gap-6">

          {/* Card 1: Primary Action (Cyan) */}
          <div
            onClick={() => isProfessor ? setShowCreateModal(true) : setShowEnrollModal(true)}
            className={`bg-cyan-400 rounded-2xl p-8 border-4 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] cursor-pointer hover:translate-x-1 hover:translate-y-1 hover:shadow-none transition-all group ${darkMode ? 'border-white' : 'border-slate-900'}`}
          >
            <div className="flex items-center justify-between text-slate-900 mb-4">
              <span className="font-black uppercase tracking-wider text-sm border-b-2 border-slate-900 pb-1">
                {isProfessor ? 'Management' : 'Enroll'}
              </span>
              {isProfessor ? <PlusCircle className="w-8 h-8" /> : <Search className="w-8 h-8" />}
            </div>
            <h3 className="text-3xl font-black text-slate-900 leading-tight">
              {isProfessor ? 'Create New Course' : 'Enter Course Code'}
            </h3>
          </div>

          {/* Card 2: Status (Green) */}
          <div className={`bg-emerald-400 rounded-2xl p-8 border-4 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] hover:translate-x-1 hover:translate-y-1 hover:shadow-none transition-all ${darkMode ? 'border-white' : 'border-slate-900'}`}>
            <div className="flex items-center justify-between text-slate-900 mb-2">
              <span className="font-black uppercase tracking-wider text-sm border-b-2 border-slate-900 pb-1">Verified Answers</span>
              <CheckCircle className="w-8 h-8" />
            </div>
            <div className="text-6xl font-black text-slate-900 mt-4">{verifiedCount}</div>
          </div>

          {/* Card 3: Alerts (Red) */}
          <div className={`bg-rose-400 rounded-2xl p-8 border-4 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] hover:translate-x-1 hover:translate-y-1 hover:shadow-none transition-all ${darkMode ? 'border-white' : 'border-slate-900'}`}>
            <div className="flex items-center justify-between text-slate-900 mb-2">
              <span className="font-black uppercase tracking-wider text-sm border-b-2 border-slate-900 pb-1">Pending Questions</span>
              <AlertCircle className="w-8 h-8" />
            </div>
            <div className="text-6xl font-black text-slate-900 mt-4">{pendingCount}</div>
          </div>

        </div>
      </div>

      {/* Create Course Modal - Neo Brutalist */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(255,255,255,1)] p-8 max-w-lg w-full animate-in fade-in zoom-in duration-200">
            <h2 className="text-3xl font-black text-slate-900 mb-6">Create New Course</h2>
            <form onSubmit={handleCreateCourse}>
              <div className="mb-5">
                <label className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">Course Name</label>
                <input
                  type="text"
                  required
                  className="w-full px-4 py-3 border-4 border-slate-900 rounded-xl focus:outline-none focus:ring-4 focus:ring-cyan-400 font-bold text-slate-900 placeholder:text-slate-400"
                  placeholder="e.g., Advanced React Patterns"
                  value={courseForm.name}
                  onChange={(e) => setCourseForm({ ...courseForm, name: e.target.value })}
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">Description</label>
                <textarea
                  required
                  value={courseForm.description}
                  onChange={(e) => setCourseForm({ ...courseForm, description: e.target.value })}
                  className="w-full px-4 py-3 border-4 border-slate-900 rounded-xl focus:outline-none focus:ring-4 focus:ring-cyan-400 font-bold text-slate-900 placeholder:text-slate-400 resize-none"
                  rows="4"
                  placeholder="What will students learn?"
                />
              </div>
              <div className="flex gap-4">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="flex-1 px-5 py-3 border-4 border-slate-900 text-slate-900 rounded-xl font-black hover:bg-slate-100 transition-all uppercase tracking-wide"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-5 py-3 bg-slate-900 text-white border-4 border-slate-900 rounded-xl font-black hover:bg-slate-800 transition-all uppercase tracking-wide"
                >
                  Create
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Enroll by Code Modal - Neo Brutalist */}
      {showEnrollModal && (
        <div className="fixed inset-0 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(255,255,255,1)] p-8 max-w-lg w-full animate-in fade-in zoom-in duration-200">
            <h2 className="text-3xl font-black text-slate-900 mb-6">Enroll with Course Code</h2>
            <form onSubmit={handleEnrollByCode}>
              {enrollError && (
                <div className="bg-rose-400 border-4 border-slate-900 p-4 rounded-xl mb-5">
                  <p className="text-sm font-black text-slate-900">{enrollError}</p>
                </div>
              )}
              <div className="mb-6">
                <label className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">Course Code</label>
                <input
                  type="text"
                  required
                  className="w-full px-4 py-3 border-4 border-slate-900 rounded-xl focus:outline-none focus:ring-4 focus:ring-cyan-400 font-bold text-slate-900 placeholder:text-slate-400 uppercase"
                  placeholder="e.g., ABC12345"
                  value={enrollCode}
                  onChange={(e) => setEnrollCode(e.target.value.toUpperCase())}
                  maxLength={8}
                />
              </div>
              <div className="flex gap-4">
                <button
                  type="button"
                  onClick={() => { setShowEnrollModal(false); setEnrollCode(''); setEnrollError(''); }}
                  className="flex-1 px-5 py-3 border-4 border-slate-900 text-slate-900 rounded-xl font-black hover:bg-slate-100 transition-all uppercase tracking-wide"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 px-5 py-3 bg-slate-900 text-white border-4 border-slate-900 rounded-xl font-black hover:bg-slate-800 transition-all uppercase tracking-wide"
                >
                  Enroll
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
