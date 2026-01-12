import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, roles = [] }) => {
    const { user, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-slate-900"></div>
            </div>
        );
    }

    if (!user) {
        // Redirect to login but save the attempted location
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Role check
    if (roles.length > 0 && !roles.includes(user.role)) {
        // User authorized but doesn't have required role
        return <Navigate to="/dashboard" replace />;
    }

    return children;
};

export default ProtectedRoute;
