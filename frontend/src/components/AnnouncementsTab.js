import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { announcementService } from '../services/services';
import { Plus, Bell, User, Calendar, Trash2 } from 'lucide-react';

const AnnouncementsTab = ({ courseId }) => {
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const [announcements, setAnnouncements] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newAnnouncement, setNewAnnouncement] = useState({ title: '', content: '' });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchAnnouncements();
    // eslint-disable-next-line
  }, [courseId]);

  const fetchAnnouncements = async () => {
    try {
      const response = await announcementService.getAnnouncements(courseId);
      setAnnouncements(response.data);
    } catch (error) {
      console.error('Error fetching announcements:', error);
    }
  };

  const handleCreateAnnouncement = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await announcementService.createAnnouncement(courseId, newAnnouncement);
      setNewAnnouncement({ title: '', content: '' });
      setShowModal(false);
      fetchAnnouncements();
    } catch (error) {
      console.error('Error creating announcement:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAnnouncement = async (announcementId) => {
    if (!window.confirm('Are you sure you want to delete this announcement?')) {
      return;
    }
    try {
      await announcementService.deleteAnnouncement(courseId, announcementId);
      fetchAnnouncements();
    } catch (error) {
      console.error('Error deleting announcement:', error);
    }
  };

  return (
    <div className={`p-8 ${darkMode ? 'bg-slate-800' : 'bg-white'}`} style={{ fontFamily: 'Inter, sans-serif' }}>
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <h2 className={`text-3xl font-black uppercase flex items-center gap-3 ${darkMode ? 'text-white' : 'text-slate-900'}`}>
          <Bell className="w-8 h-8" />
          ANNOUNCEMENTS
        </h2>
        {user?.role === 'PROFESSOR' && (
          <button
            onClick={() => setShowModal(true)}
            className={`flex items-center gap-2 px-6 py-3 rounded-xl font-black hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] border-2 transition-all ${darkMode ? 'bg-rose-400 text-slate-900 border-white' : 'bg-rose-400 text-slate-900 border-slate-900'}`}
          >
            <Plus className="w-5 h-5" />
            CREATE ANNOUNCEMENT
          </button>
        )}
      </div>

      {/* Announcements List */}
      <div className="space-y-6">
        {announcements.length === 0 ? (
          <div className={`text-center py-16 rounded-2xl border-4 shadow-[8px_8px_0px_0px_rgba(15,23,42,1)] ${darkMode ? 'bg-slate-700 border-white' : 'bg-white border-slate-900'}`}>
            <Bell className={`w-16 h-16 opacity-30 mx-auto mb-4 ${darkMode ? 'text-white' : 'text-slate-900'}`} />
            <h3 className={`text-2xl font-black ${darkMode ? 'text-white' : 'text-slate-900'}`}>NO ANNOUNCEMENTS YET</h3>
            <p className={`font-bold mt-2 ${darkMode ? 'text-slate-300' : 'text-slate-900'}`}>Check back later for updates</p>
          </div>
        ) : (
          announcements.map((announcement) => (
            <div
              key={announcement.id}
              className={`rounded-2xl p-6 border-4 shadow-[4px_4px_0px_0px_rgba(15,23,42,1)] hover:translate-x-1 hover:translate-y-1 hover:shadow-none transition-all ${darkMode ? 'bg-slate-700 border-white' : 'bg-white border-slate-900'}`}
            >
              <div className="flex items-start gap-4">
                <div className={`w-12 h-12 rounded-full border-2 flex items-center justify-center flex-shrink-0 ${darkMode ? 'bg-rose-400 border-white' : 'bg-rose-400 border-slate-900'}`}>
                  <Bell className="w-6 h-6 text-slate-900" />
                </div>
                <div className="flex-1">
                  <div className="flex items-start justify-between">
                    <h3 className={`font-black text-xl mb-2 ${darkMode ? 'text-white' : 'text-slate-900'}`}>{announcement.title}</h3>
                    {user?.role === 'PROFESSOR' && (
                      <button
                        onClick={() => handleDeleteAnnouncement(announcement.id)}
                        className={`p-2 rounded-lg transition-colors ${darkMode
                          ? 'hover:bg-red-500/20 text-slate-400 hover:text-red-400'
                          : 'hover:bg-red-50 text-slate-400 hover:text-red-500'}`}
                        title="Delete announcement"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
                    )}
                  </div>
                  <p className={`font-medium mb-4 whitespace-pre-wrap ${darkMode ? 'text-slate-300' : 'text-slate-900'}`}>{announcement.content}</p>
                  <div className={`flex items-center gap-4 text-sm font-bold ${darkMode ? 'text-slate-400' : 'text-slate-900'}`}>
                    <div className="flex items-center gap-1">
                      <Calendar className="w-4 h-4" />
                      {new Date(announcement.createdAt).toLocaleDateString()}
                    </div>
                    <div className="flex items-center gap-1">
                      <User className="w-4 h-4" />
                      Professor
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Create Announcement Modal - Neo Brutalist */}
      {showModal && (
        <div className="fixed inset-0 bg-slate-900/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl border-4 border-slate-900 shadow-[8px_8px_0px_0px_rgba(255,255,255,1)] p-8 max-w-lg w-full">
            <h2 className="text-3xl font-black text-slate-900 mb-6 uppercase">CREATE ANNOUNCEMENT</h2>
            <form onSubmit={handleCreateAnnouncement}>
              <div className="mb-5">
                <label className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">
                  TITLE
                </label>
                <input
                  type="text"
                  required
                  className="w-full px-4 py-3 border-4 border-slate-900 rounded-xl focus:outline-none focus:ring-4 focus:ring-rose-400 font-bold text-slate-900"
                  placeholder="Announcement title..."
                  value={newAnnouncement.title}
                  onChange={(e) => setNewAnnouncement({ ...newAnnouncement, title: e.target.value })}
                />
              </div>
              <div className="mb-6">
                <label className="block text-sm font-black text-slate-900 mb-2 uppercase tracking-wide">
                  CONTENT
                </label>
                <textarea
                  required
                  rows="4"
                  className="w-full px-4 py-3 border-4 border-slate-900 rounded-xl focus:outline-none focus:ring-4 focus:ring-rose-400 resize-none font-medium text-slate-900"
                  placeholder="Announcement details..."
                  value={newAnnouncement.content}
                  onChange={(e) => setNewAnnouncement({ ...newAnnouncement, content: e.target.value })}
                />
              </div>
              <div className="flex gap-4">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 px-5 py-3 border-4 border-slate-900 text-slate-900 rounded-xl font-black hover:bg-slate-100 transition-all uppercase"
                >
                  CANCEL
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-5 py-3 bg-slate-900 text-white border-4 border-slate-900 rounded-xl font-black hover:bg-slate-800 transition-all disabled:opacity-50 uppercase"
                >
                  {loading ? 'CREATING...' : 'CREATE'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AnnouncementsTab;
