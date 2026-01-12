import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { GraduationCap, LogOut, LayoutDashboard, User, Mail, Moon, Sun, Bell } from 'lucide-react';
import { courseService, questionService, announcementService } from '../services/services';

const Navbar = () => {
  const { user, logout } = useAuth();
  const { darkMode, toggleDarkMode } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [loadingNotifications, setLoadingNotifications] = useState(false);
  const profileRef = useRef(null);
  const notifRef = useRef(null);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setShowProfileMenu(false);
  };

  const isProfessor = user?.role === 'PROFESSOR';
  const isAdmin = user?.role === 'ADMIN';

  const getInitials = () => {
    if (!user) return '';
    const firstname = user.firstname || '';
    const lastname = user.lastname || '';

    if (firstname && lastname) {
      return `${firstname[0]}${lastname[0]}`.toUpperCase();
    }
    return user.email?.[0]?.toUpperCase() || 'U';
  };

  const getRoleLabel = () => {
    if (isAdmin) return 'Admin';
    if (isProfessor) return 'Professor';
    return 'Student';
  };

  const getRoleColorClass = () => {
    if (isAdmin) return 'bg-purple-600 text-white';
    if (isProfessor) return 'bg-rose-600 text-white';
    return 'bg-emerald-600 text-white';
  };

  const getHeaderColorClass = () => {
    if (isAdmin) return 'bg-purple-400';
    if (isProfessor) return 'bg-rose-400';
    return 'bg-emerald-400';
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Fetch notifications when dropdown opens
  useEffect(() => {
    if (showNotifications && !loadingNotifications) {
      fetchNotifications();
    }
    // eslint-disable-next-line
  }, [showNotifications]);

  const fetchNotifications = async () => {
    setLoadingNotifications(true);
    try {
      const endpoint = isProfessor ? courseService.getProfessorCourses : courseService.getStudentCourses;
      const coursesRes = await endpoint();
      const courses = coursesRes.data || [];

      const allNotifications = [];

      // Fetch recent questions and announcements from all courses
      await Promise.all(courses.map(async (course) => {
        try {
          const [questionsRes, announcementsRes] = await Promise.all([
            questionService.getQuestions(course.id),
            announcementService.getAnnouncements(course.id)
          ]);

          const questions = (questionsRes.data || []).slice(0, 3).map(q => ({
            type: 'question',
            courseId: course.id,
            courseName: course.name,
            title: q.title,
            createdAt: q.createdAt,
            id: q.id
          }));

          const announcements = (announcementsRes.data || []).slice(0, 2).map(a => ({
            type: 'announcement',
            courseId: course.id,
            courseName: course.name,
            title: a.title,
            createdAt: a.createdAt,
            id: a.id
          }));

          allNotifications.push(...questions, ...announcements);
        } catch (err) {
          console.error(`Failed to fetch notifications for course ${course.id}`, err);
        }
      }));

      // Sort by date and take most recent 10
      allNotifications.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      setNotifications(allNotifications.slice(0, 10));
    } catch (error) {
      console.error('Failed to fetch notifications', error);
    } finally {
      setLoadingNotifications(false);
    }
  };

  const formatTimeAgo = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);

    if (diffInSeconds < 60) return 'just now';
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
    if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;
    return date.toLocaleDateString();
  };

  const handleNotificationClick = (notification) => {
    const tab = notification.type === 'question' ? 'questions' : 'announcements';
    navigate(`/courses/${notification.courseId}?tab=${tab}`);
    setShowNotifications(false);
  };

  return (
    <nav className={`sticky top-0 z-50 border-b-4 border-slate-900 ${darkMode ? 'bg-slate-900' : 'bg-white'}`} style={{ fontFamily: 'Inter, sans-serif' }}>
      <div className="container mx-auto px-6">
        <div className="flex items-center justify-between h-20">
          {/* Logo Section */}
          <Link
            to="/dashboard"
            className="flex items-center gap-3 group"
          >
            <div className={`p-2 rounded-lg border-2 border-slate-900 group-hover:-translate-y-1 transition-transform ${darkMode ? 'bg-indigo-500 text-white' : 'bg-slate-900 text-white'}`}>
              <GraduationCap className="w-8 h-8" />
            </div>
            <span className={`text-2xl font-extrabold tracking-tight ${darkMode ? 'text-white' : 'text-slate-900'}`}>Askademy</span>
          </Link>

          {/* Center Navigation - Solid Pill */}
          {/* Center Navigation - Solid Pill */}
          {!isAdmin && (
            <div className="hidden md:flex items-center">
              <Link
                to="/dashboard"
                className={`flex items-center gap-2 px-6 py-2 rounded-full font-bold transition-all border-2 border-slate-900 ${location.pathname === '/dashboard'
                  ? darkMode
                    ? 'bg-indigo-500 text-white shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]'
                    : 'bg-slate-900 text-white shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]'
                  : darkMode
                    ? 'bg-slate-800 text-white hover:bg-slate-700'
                    : 'bg-white text-slate-900 hover:bg-slate-100'
                  }`}
              >
                <LayoutDashboard className="w-5 h-5" />
                Dashboard
              </Link>
            </div>
          )}

          {isAdmin && (
            <div className="hidden md:flex items-center">
              <Link
                to="/admin"
                className={`flex items-center gap-2 px-6 py-2 rounded-full font-bold transition-all border-2 border-slate-900 ${location.pathname === '/admin'
                  ? darkMode
                    ? 'bg-purple-500 text-white shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]'
                    : 'bg-slate-900 text-white shadow-[4px_4px_0px_0px_rgba(0,0,0,0.2)]'
                  : darkMode
                    ? 'bg-slate-800 text-white hover:bg-slate-700'
                    : 'bg-white text-slate-900 hover:bg-slate-100'
                  }`}
              >
                <LayoutDashboard className="w-5 h-5" />
                Admin Dashboard
              </Link>
            </div>
          )}

          {/* User Section */}
          {user && (
            <div className="flex items-center gap-4">
              {/* Notifications Button */}
              <div ref={notifRef} className="relative">
                <button
                  onClick={() => setShowNotifications(!showNotifications)}
                  className={`p-3 rounded-full border-2 border-slate-900 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-transform hover:-translate-y-1 ${darkMode ? 'bg-slate-800 text-white' : 'bg-white text-slate-900'
                    }`}
                >
                  <Bell className="w-5 h-5" />
                </button>

                {/* Notifications Dropdown */}
                {showNotifications && (
                  <div className={`absolute right-0 mt-4 w-96 rounded-xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(15,23,42,1)] overflow-hidden max-h-[500px] ${darkMode ? 'bg-slate-800' : 'bg-white'}`}>
                    {/* Header */}
                    <div className={`p-4 border-b-2 ${darkMode ? 'bg-slate-700 border-slate-600' : 'bg-slate-100 border-slate-300'}`}>
                      <Link to="/guide" className={`${darkMode ? 'text-slate-300 hover:text-cyan-400' : 'text-slate-600 hover:text-indigo-600'} transition-colors font-medium`}>Guide</Link>
                      {user.role === 'ADMIN' && (
                        <Link to="/admin" className={`${darkMode ? 'text-slate-300 hover:text-cyan-400' : 'text-slate-600 hover:text-indigo-600'} transition-colors font-bold`}>
                          Admin
                        </Link>
                      )}
                      <h3 className={`font-black text-lg ${darkMode ? 'text-white' : 'text-slate-900'}`}>
                        Recent Activity
                      </h3>
                    </div>

                    {/* Notifications List */}
                    <div className="overflow-y-auto max-h-[400px]">
                      {loadingNotifications ? (
                        <div className="p-8 text-center">
                          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-slate-900 mx-auto"></div>
                        </div>
                      ) : notifications.length === 0 ? (
                        <div className="p-8 text-center">
                          <Bell className={`w-12 h-12 mx-auto mb-2 opacity-30 ${darkMode ? 'text-white' : 'text-slate-900'}`} />
                          <p className={`font-bold ${darkMode ? 'text-slate-400' : 'text-slate-600'}`}>
                            No recent activity
                          </p>
                        </div>
                      ) : (
                        <div className="divide-y-2 divide-slate-300">
                          {notifications.map((notif, idx) => (
                            <div
                              key={`${notif.type}-${notif.id}-${idx}`}
                              onClick={() => handleNotificationClick(notif)}
                              className={`p-4 cursor-pointer transition-colors ${darkMode ? 'hover:bg-slate-700' : 'hover:bg-slate-50'
                                }`}
                            >
                              <div className="flex items-start gap-3">
                                <div className={`mt-1 p-2 rounded-lg ${notif.type === 'question'
                                  ? 'bg-cyan-400'
                                  : 'bg-rose-400'
                                  }`}>
                                  {notif.type === 'question' ? (
                                    <svg className="w-4 h-4 text-slate-900" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                                    </svg>
                                  ) : (
                                    <svg className="w-4 h-4 text-slate-900" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z" />
                                    </svg>
                                  )}
                                </div>
                                <div className="flex-1 min-w-0">
                                  <p className={`text-xs font-black uppercase tracking-wide mb-1 ${darkMode ? 'text-cyan-400' : 'text-cyan-600'}`}>
                                    {notif.courseName}
                                  </p>
                                  <p className={`font-bold text-sm mb-1 truncate ${darkMode ? 'text-white' : 'text-slate-900'}`}>
                                    {notif.title}
                                  </p>
                                  <p className={`text-xs font-bold ${darkMode ? 'text-slate-400' : 'text-slate-600'}`}>
                                    {formatTimeAgo(notif.createdAt)}
                                  </p>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>

              {/* User Badge - Clickable for Profile Menu */}
              <div ref={profileRef} className="relative">
                <button
                  onClick={() => setShowProfileMenu(!showProfileMenu)}
                  className={`flex items-center gap-3 px-5 py-2 rounded-full border-2 border-slate-900 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-transform hover:-translate-y-1 ${getRoleColorClass()}`}
                >
                  <div className="w-8 h-8 rounded-full bg-white border-2 border-slate-900 flex items-center justify-center font-bold text-sm text-slate-900">
                    {getInitials()}
                  </div>
                  <div className="flex flex-col leading-tight">
                    <span className="text-sm font-bold">
                      {user.firstname || user.email?.split('@')[0]}
                    </span>
                    <span className="text-[10px] font-black uppercase tracking-wider">
                      {getRoleLabel()}
                    </span>
                  </div>
                </button>

                {/* Profile Dropdown Menu */}
                {showProfileMenu && (
                  <div className={`absolute right-0 mt-4 w-72 rounded-xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(15,23,42,1)] overflow-hidden ${darkMode ? 'bg-slate-800' : 'bg-white'}`}>
                    {/* Profile Header */}
                    <div className={`p-6 ${getHeaderColorClass()}`}>
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 rounded-full bg-white border-2 border-slate-900 flex items-center justify-center font-black text-lg text-slate-900">
                          {getInitials()}
                        </div>
                        <div>
                          <h3 className="font-black text-slate-900">
                            {user.firstname || user.email?.split('@')[0]}
                          </h3>
                          <p className="text-xs font-bold text-slate-900 uppercase tracking-wide">
                            {getRoleLabel()}
                          </p>
                        </div>
                      </div>
                    </div>

                    {/* Profile Info */}
                    <div className="p-4 space-y-3">
                      <div className={`flex items-center gap-3 p-3 rounded-lg border-2 border-slate-900 ${darkMode ? 'bg-slate-700' : 'bg-slate-50'}`}>
                        <Mail className={`w-5 h-5 ${darkMode ? 'text-cyan-400' : 'text-slate-900'}`} />
                        <div>
                          <p className={`text-xs font-bold uppercase tracking-wide ${darkMode ? 'text-slate-400' : 'text-slate-600'}`}>Email</p>
                          <p className={`text-sm font-bold ${darkMode ? 'text-white' : 'text-slate-900'}`}>{user.email}</p>
                        </div>
                      </div>

                      <div className={`flex items-center gap-3 p-3 rounded-lg border-2 border-slate-900 ${darkMode ? 'bg-slate-700' : 'bg-slate-50'}`}>
                        <User className={`w-5 h-5 ${darkMode ? 'text-cyan-400' : 'text-slate-900'}`} />
                        <div>
                          <p className={`text-xs font-bold uppercase tracking-wide ${darkMode ? 'text-slate-400' : 'text-slate-600'}`}>Role</p>
                          <p className={`text-sm font-bold ${darkMode ? 'text-white' : 'text-slate-900'}`}>{getRoleLabel()}</p>
                        </div>
                      </div>

                      {/* Dark Mode Toggle */}
                      <button
                        onClick={toggleDarkMode}
                        className={`w-full flex items-center justify-between p-3 rounded-lg border-2 border-slate-900 font-bold transition-all ${darkMode
                          ? 'bg-cyan-400 text-slate-900 hover:bg-cyan-300'
                          : 'bg-slate-900 text-white hover:bg-slate-800'
                          }`}
                      >
                        <div className="flex items-center gap-3">
                          {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
                          <span className="uppercase tracking-wide">{darkMode ? 'Light Mode' : 'Dark Mode'}</span>
                        </div>
                        <div className={`w-12 h-6 rounded-full border-2 border-slate-900 relative transition-all ${darkMode ? 'bg-white' : 'bg-slate-700'}`}>
                          <div className={`absolute top-0.5 w-4 h-4 rounded-full bg-slate-900 transition-transform ${darkMode ? 'left-6' : 'left-0.5'}`}></div>
                        </div>
                      </button>

                      {/* Logout Button */}
                      <button
                        onClick={handleLogout}
                        className={`w-full flex items-center justify-center gap-2 p-3 rounded-lg border-2 border-slate-900 font-black transition-all uppercase tracking-wide ${darkMode
                          ? 'bg-rose-400 text-slate-900 hover:bg-rose-300'
                          : 'bg-rose-600 text-white hover:bg-rose-700'
                          }`}
                      >
                        <LogOut className="w-5 h-5" />
                        Logout
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
