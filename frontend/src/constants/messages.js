/**
 * User-facing message constants
 * Centralized for consistency and easy i18n in the future
 */

export const SUCCESS_MESSAGES = {
  COURSE_CREATED: 'Course created successfully! Share your course code with students.',
  ENROLLED: 'You\'ve successfully enrolled in the course!',
  QUESTION_POSTED: 'Your question has been posted successfully.',
  ANSWER_SUBMITTED: 'Your answer has been submitted.',
  ANSWER_VERIFIED: 'Answer verified successfully.',
  ANNOUNCEMENT_POSTED: 'Announcement posted and students have been notified.',
  GRADING_UPDATED: 'Grading information updated successfully.',
};

export const CONFIRMATION_MESSAGES = {
  DELETE_QUESTION: 'Are you sure you want to delete this question? This action cannot be undone.',
  DELETE_ANSWER: 'Are you sure you want to delete this answer? This action cannot be undone.',
  LOGOUT: 'Are you sure you want to log out?',
};

export const PLACEHOLDER_TEXT = {
  SEARCH_COURSES: 'Search courses by name or code...',
  COURSE_NAME: 'e.g., Introduction to Computer Science',
  COURSE_DESCRIPTION: 'Brief description of what students will learn...',
  COURSE_CODE: 'Enter 8-character code',
  QUESTION_TITLE: 'What would you like to ask?',
  QUESTION_CONTENT: 'Provide details about your question...',
  ANSWER_CONTENT: 'Share your knowledge to help answer this question...',
  ANNOUNCEMENT_TITLE: 'Announcement title',
  ANNOUNCEMENT_CONTENT: 'What would you like to announce to the class?',
  GRADING_INFO: 'Describe grading criteria, assignment weights, etc.',
};

export const LABELS = {
  ANONYMOUS_QUESTION: 'Post anonymously',
  REQUIRED_FIELD: 'Required',
  OPTIONAL_FIELD: 'Optional',
};
