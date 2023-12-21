import React from 'react';
import { Navigate } from 'react-router';

const AdminRoute = ({ authenticated, role, component }) => {
  return authenticated && role === 'ADMIN' ? (
    component
  ) : (
    <Navigate to="/" {...alert('접근할 수 없는 페이지입니다.')} />
  );
};

export default AdminRoute;
