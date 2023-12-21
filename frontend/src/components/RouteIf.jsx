import React from 'react';
import { Route } from 'react-router';

const RouteIf = ({ element, rest }) => {
  return <Route {...rest} />;
};

export default RouteIf;
