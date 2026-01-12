import api from './api';

export const authService = {
  register: (userData) => api.post('/auth/register', userData),
  login: (credentials) => api.post('/auth/login', credentials),
};

export const courseService = {
  getAllCourses: () => api.get('/courses'),
  getCourseById: (courseId) => api.get(`/courses/${courseId}`),
  getProfessorCourses: () => api.get('/courses/professor'),
  getStudentCourses: () => api.get('/courses/student'),
  createCourse: (courseData) => api.post('/courses', courseData),
  enrollInCourse: (courseId) => api.post(`/courses/${courseId}/enroll`),
  enrollByCourseCode: (courseCode) => api.post('/courses/enroll-by-code', courseCode, {
    headers: { 'Content-Type': 'text/plain' }
  }),
  updateGradingInfo: (courseId, gradingInfo) => api.put(`/courses/${courseId}/grading`, gradingInfo, {
    headers: { 'Content-Type': 'text/plain' }
  }),
  deleteCourse: (courseId) => api.delete(`/courses/${courseId}`),
};

export const announcementService = {
  getAnnouncements: (courseId) => api.get(`/courses/${courseId}/announcements`),
  createAnnouncement: (courseId, announcementData) => api.post(`/courses/${courseId}/announcements`, announcementData),
  deleteAnnouncement: (courseId, announcementId) => api.delete(`/courses/${courseId}/announcements/${announcementId}`),
};

export const adminService = {
  getStats: () => api.get('/admin/stats'),
  getAllUsers: () => api.get('/admin/users'),
  getAllCourses: () => api.get('/admin/courses'),
  getAllQuestions: () => api.get('/admin/questions'),
  getAllAnswers: () => api.get('/admin/answers'),
};

export const questionService = {
  getQuestions: (courseId, filter = null) => {
    const url = filter ? `/questions/course/${courseId}?filter=${filter}` : `/questions/course/${courseId}`;
    return api.get(url);
  },
  getGroupedQuestions: (courseId, threshold = 0.3) => {
    return api.get(`/questions/grouped/${courseId}?threshold=${threshold}`);
  },
  createQuestion: (questionData) => api.post('/questions', questionData),
  deleteQuestion: (questionId) => api.delete(`/questions/${questionId}`),
};

export const answerService = {
  getAnswers: (questionId) => api.get(`/answers/question/${questionId}`),
  createAnswer: (answerData) => api.post('/answers', answerData),
  createBatchAnswer: (questionIds, content, autoVerify = true, anonymous = false) =>
    api.post('/answers/batch', { questionIds, content, autoVerify, anonymous }),
  verifyAnswer: (answerId) => api.put(`/answers/${answerId}/verify`),
  deleteAnswer: (answerId) => api.delete(`/answers/${answerId}`),
};