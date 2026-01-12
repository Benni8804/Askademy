import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';
import { parseErrorMessage } from '../utils/errorMessages';

const AuthContext = createContext();

export const useAuth = () => {
  return useContext(AuthContext);
};

// Decode JWT token (simple base64 decode without validation)
const decodeToken = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    if (process.env.NODE_ENV === 'development') {
      console.warn('Failed to decode JWT token - token may be malformed');
    }
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(sessionStorage.getItem('token') || null);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      // Decode token to restore user info
      const decoded = decodeToken(token);
      if (decoded && decoded.sub) {
        // JWT 'sub' field contains the email, role is in 'role' field
        setUser({
          email: decoded.sub,
          role: decoded.role,
          id: decoded.userId,
        });
      }
    }
    setLoading(false);
  }, [token]);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token: newToken, id, email: userEmail, role } = response.data;
      setToken(newToken);
      setUser({ id, email: userEmail, role });
      sessionStorage.setItem('token', newToken);
      return { success: true };
    } catch (error) {
      const errorMessage = parseErrorMessage(error);
      return { success: false, message: errorMessage };
    }
  };

  const register = async (userData) => {
    try {
      await api.post('/auth/register', userData);
      return { success: true };
    } catch (error) {
      const errorMessage = parseErrorMessage(error);
      return { success: false, message: errorMessage };
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    sessionStorage.removeItem('token');
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};