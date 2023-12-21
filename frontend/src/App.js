import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import { Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import SaveForm from './pages/post/SaveForm';
import UpdateForm from './pages/post/UpdateForm';
import Detail from './pages/post/Detail';
import LoginForm from './pages/user/LoginForm';
import JoinForm from './pages/user/JoinForm';
import ListForm from './pages/post/ListForm';
import {
  IsLoginContext,
  IsLoginProvider,
  useIsLoginState,
} from './components/IsLoginContext';
import UserList from './pages/user/UserList';
import CategoryList from './pages/post/CategoryList';
import Init from './pages/Init';
import UserInfo from './pages/user/UserInfo';
import PrivateRoute from './components/PrivateRoute';
import AdminRoute from './components/AdminRoute';
import NonLoginRoute from './components/NonLoginRoute';
import MyInfo from './pages/user/MyInfo';
import UserUpdate from './pages/user/UserUpdate';

function App() {
  const role = sessionStorage.getItem('role');
  const id = sessionStorage.getItem('id');

  return (
    <div>
      <IsLoginProvider>
        <Header />
        <Container>
          <Routes>
            <Route path={'/'} element={<Init />} />

            <Route path="/joinForm" element={<JoinForm />} />
            <Route path="/loginForm" element={<LoginForm />} />

            {/* <Route
              path="/loginForm"
              element={
                <NonLoginRoute isLogined={id} component={<LoginForm />} />
              }
            /> */}

            <Route
              path="/listForm"
              element={
                <PrivateRoute authenticated={id} component={<ListForm />} />
              }
            />

            <Route
              path="/saveForm"
              element={
                <PrivateRoute authenticated={id} component={<SaveForm />} />
              }
            />
            <Route
              path="/post/:id"
              element={
                <PrivateRoute authenticated={id} component={<Detail />} />
              }
            />

            {/* <Route path="/loginForm" element={<LoginForm />} /> */}
            {/* <Route path="/listForm" element={<ListForm />} /> */}
            {/* <Route path="/saveForm" element={<SaveForm />} /> */}
            {/* <Route path="/post/:id" element={<Detail />} /> */}
            {/* <Route path="/updateForm/:id" element={<UpdateForm />} /> */}
            <Route
              path="/updateForm/:id"
              element={
                <PrivateRoute authenticated={id} component={<UpdateForm />} />
              }
            />
            <Route
              path="/userList"
              element={
                <AdminRoute
                  authenticated={id}
                  role={role}
                  component={<UserList />}
                />
              }
            />
            <Route
              path="/user/:id"
              element={
                <AdminRoute
                  authenticated={id}
                  role={role}
                  component={<UserInfo />}
                />
              }
            />

            <Route path="/myInfo" element={<MyInfo />} />
            <Route path="/userUpdate" element={<UserUpdate />} />
            {/* <Route path="/userList" element={<UserList />} /> */}
            {/* <Route path="/user/:id" element={<UserInfo />} /> */}
            {/* <Route path="/" element={<Init />} /> */}
            {/* <Route path="/categoryList" element={<CategoryList />} /> */}
          </Routes>
        </Container>
      </IsLoginProvider>
    </div>
  );
}

export default App;
