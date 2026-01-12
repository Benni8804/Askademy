/**
 * Human-readable error messages for better UX
 * Maps technical errors to friendly, actionable messages
 */

export const ERROR_MESSAGES = {
  // Network Errors
  NETWORK_ERROR: "We're having trouble connecting to the server. Please check your internet connection and try again.",
  TIMEOUT_ERROR: "The request is taking longer than expected. Please try again in a moment.",

  // Authentication Errors
  INVALID_CREDENTIALS: "The email or password you entered is incorrect. Please double-check and try again.",
  UNAUTHORIZED: "Your session has expired. Please log in again to continue.",
  TOKEN_EXPIRED: "Your login session has expired for security reasons. Please sign in again.",

  // Course Errors
  COURSE_NOT_FOUND: "We couldn't find that course. It may have been removed or you may not have access.",
  ENROLLMENT_FAILED: "We couldn't enroll you in this course. Please verify the course code and try again.",
  INVALID_COURSE_CODE: "The course code you entered is invalid. Course codes are 8 characters long.",
  ALREADY_ENROLLED: "You're already enrolled in this course!",

  // Question/Answer Errors
  QUESTION_CREATION_FAILED: "We couldn't post your question right now. Please try again in a moment.",
  ANSWER_SUBMISSION_FAILED: "We couldn't submit your answer. Please check your connection and try again.",
  DELETE_FAILED: "We couldn't delete this item. You may not have permission to do this.",

  // Announcement Errors
  ANNOUNCEMENT_FAILED: "We couldn't post this announcement. Please try again.",
  FETCH_ANNOUNCEMENTS_FAILED: "We couldn't load announcements right now. Please refresh the page.",

  // Validation Errors
  REQUIRED_FIELD: "This field is required.",
  INVALID_EMAIL: "Please enter a valid email address.",
  PASSWORD_TOO_SHORT: "Password must be at least 6 characters long.",

  // Generic Errors
  SERVER_ERROR: "Something went wrong on our end. We're working to fix it. Please try again later.",
  UNKNOWN_ERROR: "An unexpected error occurred. If this persists, please contact support.",
  PERMISSION_DENIED: "You don't have permission to perform this action.",
};

/**
 * Maps HTTP status codes to user-friendly messages
 */
export const getErrorMessageByStatus = (status) => {
  const statusMessages = {
    400: "The information you provided isn't quite right. Please review and try again.",
    401: ERROR_MESSAGES.UNAUTHORIZED,
    403: ERROR_MESSAGES.PERMISSION_DENIED,
    404: "We couldn't find what you're looking for. It may have been moved or deleted.",
    408: ERROR_MESSAGES.TIMEOUT_ERROR,
    500: ERROR_MESSAGES.SERVER_ERROR,
    502: "Our server is temporarily unavailable. Please try again in a few minutes.",
    503: "We're currently performing maintenance. Please check back soon.",
  };

  return statusMessages[status] || ERROR_MESSAGES.UNKNOWN_ERROR;
};

/**
 * Extracts a user-friendly error message from an error object
 */
export const parseErrorMessage = (error) => {
  // Network error (no response)
  if (!error.response) {
    return ERROR_MESSAGES.NETWORK_ERROR;
  }

  const { status, data } = error.response;

  // Handle plain string responses (our backend returns these for auth errors)
  if (typeof data === 'string' && data.length > 0) {
    // Map common backend messages to friendly ones
    if (data.includes('Invalid email or password')) {
      return ERROR_MESSAGES.INVALID_CREDENTIALS;
    }
    if (data.includes('Email already exists')) {
      return "An account with this email already exists. Please sign in instead.";
    }
    if (data.includes('Registration failed')) {
      return "Registration failed. Please check your information and try again.";
    }
    // Return the backend message if it seems user-friendly
    return data;
  }

  // Check for error message object from backend (data.message format)
  if (data?.message) {
    // Map common backend messages to friendly ones
    if (data.message.includes('not found')) {
      return ERROR_MESSAGES.COURSE_NOT_FOUND;
    }
    if (data.message.includes('Invalid credentials')) {
      return ERROR_MESSAGES.INVALID_CREDENTIALS;
    }
    if (data.message.includes('already enrolled')) {
      return ERROR_MESSAGES.ALREADY_ENROLLED;
    }

    // Return backend message if it's user-friendly
    return data.message;
  }

  // Fall back to status code mapping
  return getErrorMessageByStatus(status);
};
