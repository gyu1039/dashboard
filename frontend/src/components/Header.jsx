import React, { useContext } from 'react';
import { Button, Container, Nav, Navbar } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';

import UserService from '../services/UserService';
import { IsLoginContext } from './IsLoginContext';

const Header = () => {
  const router = useNavigate();
  const { isLogin, setIsLogin, role } = useContext(IsLoginContext);
  const refresh = sessionStorage.getItem('refresh');

  const logout = () => {
    UserService.logout(refresh)
      .then((res) => {
        sessionStorage.removeItem('access');
        sessionStorage.removeItem('refresh');
        sessionStorage.removeItem('id');
        sessionStorage.removeItem('role');
        setIsLogin(false);
        router('/', isLogin);
      })
      .catch();
  };

  return (
    <>
      <Navbar bg="dark" data-bs-theme="dark">
        <Container>
          <Link to="/" className="navbar-brand">
            게시판
          </Link>
          <Nav className="me-auto">
            {!isLogin ? (
              <>
                <Link to="/joinForm" className="nav-link">
                  회원가입
                </Link>
                <Link to="/loginForm" className="nav-link">
                  로그인
                </Link>
              </>
            ) : (
              <>
                {role === 'USER' ? (
                  <>
                    <Link to="/saveForm" className="nav-link">
                      글쓰기
                    </Link>
                    <Link to="/listForm" className="nav-link">
                      게시글 목록
                    </Link>
                  </>
                ) : role === 'ADMIN' ? (
                  <>
                    <Link to="/listForm" className="nav-link">
                      게시글 관리
                    </Link>
                    <Link to="/userList" className="nav-link">
                      회원 관리
                    </Link>
                    {/* <Link to="/categoryList" className="nav-link">
                      게시판 관리
                    </Link> */}
                  </>
                ) : (
                  <></>
                )}
                {/* <Link to="/myInfo" className="nav-link">
                  내 정보
                </Link> */}
              </>
            )}
          </Nav>
          {isLogin ? (
            <>
              <Button onClick={logout}>로그아웃</Button>
              {/* <Form inline="true">
                <Row>
                  <Col xs="auto">
                    <Form.Control
                      type="text"
                      placeholder="Search"
                      className=" mr-sm-2"
                    />
                  </Col>
                  <Col xs="auto">
                    <Button type="button">Submit</Button>
                  </Col>
                </Row>
              </Form> */}
            </>
          ) : (
            <></>
          )}
        </Container>
      </Navbar>
      <br />
    </>
  );
};

export default Header;
