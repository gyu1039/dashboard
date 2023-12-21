import React, { createContext, useContext, useMemo, useState } from 'react';

const access = sessionStorage.getItem('access');

export const IsLoginContext = createContext({
  isLogin: access ? true : false,
});

export const IsLoginProvider = (props) => {
  const { children } = props;

  const [isLogin, setIsLogin] = useState(access !== null ? true : false);
  const [role] = useState(sessionStorage.getItem('role'));

  const value = useMemo(
    () => ({ isLogin, setIsLogin, role }),
    [isLogin, setIsLogin, role],
  );

  return (
    <IsLoginContext.Provider value={value}>{children}</IsLoginContext.Provider>
  );
};

export function useIsLoginState() {
  const context = useContext(IsLoginContext);
  if (!context) {
    throw new Error('Cannot find IsLoginProvide');
  }

  return context.isLogin;
}
