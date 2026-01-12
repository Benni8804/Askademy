import React, { useState, useEffect } from 'react';
import { adminService, courseService, questionService, answerService } from '../services/services';
import { useTheme } from '../context/ThemeContext';
import { Users, BookOpen, MessageCircle, MessageSquare, X, Trash2 } from 'lucide-react';
import { parseErrorMessage } from '../utils/errorMessages';

const AdminDashboard = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { darkMode } = useTheme();

    // List View State
    const [activeView, setActiveView] = useState(null); // 'users', 'courses', 'questions', 'answers'
    const [listData, setListData] = useState([]);
    const [listLoading, setListLoading] = useState(false);

    const fetchStats = async () => {
        try {
            const response = await adminService.getStats();
            setStats(response.data);
        } catch (err) {
            console.error('Failed to fetch stats', err);
        }
    };

    useEffect(() => {
        const init = async () => {
            try {
                await fetchStats();
            } catch (err) {
                setError(parseErrorMessage(err));
            } finally {
                setLoading(false);
            }
        };
        init();
    }, []);

    const fetchList = async (viewType) => {
        setListLoading(true);
        setListData([]);
        try {
            let response;
            switch (viewType) {
                case 'users': response = await adminService.getAllUsers(); break;
                case 'courses': response = await adminService.getAllCourses(); break;
                case 'questions': response = await adminService.getAllQuestions(); break;
                case 'answers': response = await adminService.getAllAnswers(); break;
                default: return;
            }
            setListData(response.data);
        } catch (err) {
            console.error(err);
            // Optionally show error toast
        } finally {
            setListLoading(false);
        }
    };

    const handleViewChange = (viewType) => {
        if (activeView === viewType) {
            setActiveView(null); // Toggle off
        } else {
            setActiveView(viewType);
            fetchList(viewType);
        }
    };

    const handleDelete = async (id, type) => {
        if (!window.confirm('Are you sure you want to delete this item? This action cannot be undone.')) return;

        try {
            switch (type) {
                case 'courses': await courseService.deleteCourse(id); break;
                case 'questions': await questionService.deleteQuestion(id); break;
                case 'answers': await answerService.deleteAnswer(id); break;
                // Users deletion not implemented/safe to expose easily yet
                default: return;
            }
            // Remove from list
            setListData(prev => prev.filter(item => item.id !== id));
            // Refresh stats
            fetchStats();
        } catch (err) {
            alert('Failed to delete: ' + parseErrorMessage(err));
        }
    };

    if (loading) {
        return (
            <div className={`min-h-screen flex items-center justify-center ${darkMode ? 'bg-slate-900 text-white' : 'bg-white text-slate-900'}`}>
                <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-current"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className={`min-h-screen flex items-center justify-center ${darkMode ? 'bg-slate-900' : 'bg-white'}`}>
                <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded shadow-lg max-w-lg">
                    <p className="font-bold">Error loading stats</p>
                    <p>{error}</p>
                </div>
            </div>
        );
    }

    const StatCard = ({ title, value, icon: Icon, color, viewType }) => (
        <div
            onClick={() => handleViewChange(viewType)}
            className={`${darkMode ? 'bg-slate-800 border-white' : 'bg-white border-slate-900'} border-4 rounded-xl p-6 shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-all hover:-translate-y-1 cursor-pointer ${activeView === viewType ? 'ring-4 ring-offset-4 ring-cyan-400' : ''}`}
        >
            <div className="flex items-center justify-between mb-4">
                <h3 className={`text-xl font-black ${darkMode ? 'text-white' : 'text-slate-900'} uppercase tracking-wide`}>{title}</h3>
                <div className={`p-3 rounded-lg ${color} text-white`}>
                    <Icon size={24} />
                </div>
            </div>
            <p className={`text-4xl font-black ${darkMode ? 'text-cyan-400' : 'text-slate-900'}`}>{value}</p>
            <p className={`text-xs font-bold mt-2 uppercase ${darkMode ? 'text-slate-400' : 'text-slate-500'}`}>
                {activeView === viewType ? 'Click into close' : 'Click to manage'}
            </p>
        </div>
    );

    const DataTable = () => {
        if (!activeView) return null;

        const headers = {
            users: ['ID', 'Name', 'Email', 'Role', 'Actions'],
            courses: ['ID', 'Example Title', 'Professor', 'Actions'],
            questions: ['ID', 'Title', 'Author', 'Date', 'Actions'],
            answers: ['ID', 'Content', 'Author', 'Question', 'Actions']
        }[activeView];

        return (
            <div className={`mt-12 rounded-2xl border-4 p-6 ${darkMode ? 'bg-slate-800 border-white' : 'bg-white border-slate-900'}`}>
                <div className="flex items-center justify-between mb-6">
                    <h2 className={`text-2xl font-black uppercase ${darkMode ? 'text-white' : 'text-slate-900'}`}>
                        Manage {activeView}
                    </h2>
                    <button
                        onClick={() => setActiveView(null)}
                        className={`p-2 rounded-lg hover:bg-slate-100 ${darkMode ? 'hover:bg-slate-700 text-white' : 'text-slate-900'}`}
                    >
                        <X size={24} />
                    </button>
                </div>

                {listLoading ? (
                    <div className="text-center py-12">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-4 border-current mx-auto"></div>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className={`border-b-4 ${darkMode ? 'border-slate-600 text-slate-300' : 'border-slate-200 text-slate-600'}`}>
                                    {headers.map(h => <th key={h} className="p-4 font-black uppercase text-sm">{h}</th>)}
                                </tr>
                            </thead>
                            <tbody className={`font-medium ${darkMode ? 'text-slate-200' : 'text-slate-900'}`}>
                                {listData.map(item => (
                                    <tr key={item.id} className={`border-b-2 hover:bg-opacity-50 ${darkMode ? 'border-slate-700 hover:bg-slate-700' : 'border-slate-100 hover:bg-slate-50'}`}>
                                        <td className="p-4 font-mono text-xs opacity-70">#{item.id}</td>

                                        {/* Render Columns based on View */}
                                        {activeView === 'users' && (
                                            <>
                                                <td className="p-4 font-bold">{item.firstname} {item.lastname}</td>
                                                <td className="p-4">{item.email}</td>
                                                <td className="p-4">
                                                    <span className={`px-2 py-1 rounded text-xs font-black border uppercase ${item.role === 'ADMIN' ? 'bg-purple-100 text-purple-700 border-purple-300' :
                                                        item.role === 'PROFESSOR' ? 'bg-rose-100 text-rose-700 border-rose-300' :
                                                            'bg-cyan-100 text-cyan-700 border-cyan-300'
                                                        }`}>
                                                        {item.role}
                                                    </span>
                                                </td>
                                                <td className="p-4 text-xs opacity-50 italic">read-only</td>
                                            </>
                                        )}

                                        {activeView === 'courses' && (
                                            <>
                                                <td className="p-4 font-bold">{item.name || 'Untitled Course'}</td>
                                                <td className="p-4">{item.professor ? `${item.professor.firstname} ${item.professor.lastname}` : 'Unknown'}</td>
                                                <td className="p-4">
                                                    <button
                                                        onClick={() => handleDelete(item.id, 'courses')}
                                                        className="p-2 bg-red-100 text-red-600 rounded hover:bg-red-200 transition-colors"
                                                        title="Delete Course"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                </td>
                                            </>
                                        )}

                                        {activeView === 'questions' && (
                                            <>
                                                <td className="p-4 font-bold max-w-xs truncate">{item.title}</td>
                                                <td className="p-4">{item.author ? `${item.author.firstname} ${item.author.lastname}` : 'Anonymous'}</td>
                                                <td className="p-4 text-xs">{new Date(item.createdAt).toLocaleDateString()}</td>
                                                <td className="p-4">
                                                    <button
                                                        onClick={() => handleDelete(item.id, 'questions')}
                                                        className="p-2 bg-red-100 text-red-600 rounded hover:bg-red-200 transition-colors"
                                                        title="Delete Question"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                </td>
                                            </>
                                        )}

                                        {activeView === 'answers' && (
                                            <>
                                                <td className="p-4 max-w-xs truncate">{item.content}</td>
                                                <td className="p-4">{item.author ? `${item.author.firstname} ${item.author.lastname}` : 'Anonymous'}</td>
                                                <td className="p-4 max-w-xs truncate">{item.question ? item.question.title : 'Deleted Question'}</td>
                                                <td className="p-4">
                                                    <button
                                                        onClick={() => handleDelete(item.id, 'answers')}
                                                        className="p-2 bg-red-100 text-red-600 rounded hover:bg-red-200 transition-colors"
                                                        title="Delete Answer"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                </td>
                                            </>
                                        )}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {listData.length === 0 && (
                            <div className="p-8 text-center opacity-50 font-bold">No data found.</div>
                        )}
                    </div>
                )}
            </div>
        );
    };

    return (
        <div className={`min-h-screen p-8 ${darkMode ? 'bg-slate-900' : 'bg-slate-50'}`} style={{ fontFamily: 'Inter, sans-serif' }}>
            <div className="max-w-6xl mx-auto">
                <h1 className={`text-4xl font-black mb-8 ${darkMode ? 'text-white' : 'text-slate-900'}`}>
                    Admin Dashboard
                </h1>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                    <StatCard
                        title="Total Users"
                        value={stats ? stats.totalUsers : 0}
                        icon={Users}
                        color="bg-blue-600"
                        viewType="users"
                    />
                    <StatCard
                        title="Total Courses"
                        value={stats ? stats.totalCourses : 0}
                        icon={BookOpen}
                        color="bg-emerald-500"
                        viewType="courses"
                    />
                    <StatCard
                        title="Total Questions"
                        value={stats ? stats.totalQuestions : 0}
                        icon={MessageCircle}
                        color="bg-amber-500"
                        viewType="questions"
                    />
                    <StatCard
                        title="Total Answers"
                        value={stats ? stats.totalAnswers : 0}
                        icon={MessageSquare}
                        color="bg-purple-500"
                        viewType="answers"
                    />
                </div>

                <DataTable />
            </div>
        </div>
    );
};

export default AdminDashboard;

