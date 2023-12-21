import React from 'react';
import { Navigate } from 'react-router';

const NonLoginRoute = ({ isLogined, component }) => {
  return !isLogined ? (
    component
  ) : (
    <Navigate to="/" {...alert('잘못된 접근입니다.')} />
  );
};

export default NonLoginRoute;
