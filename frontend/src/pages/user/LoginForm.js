import React, { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import UserService from '../../services/UserService';
import { IsLoginContext } from '../../components/IsLoginContext';

const LoginForm = () => {
  const { setIsLogin } = useContext(IsLoginContext);
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (sessionStorage.getItem('id')) {
      navigate('/');
    }
  }, []);

  const onIdHandler = (e) => {
    setId(e.target.value);
  };

  const onPasswordHandler = (e) => {
    setPassword(e.target.value);
  };

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    if (!id.trim()) {
      return alert('아이디를 입력하세요.');
    }

    if (!password) {
      return alert('비밀번호를 입력하세요.');
    }

    let params = {
      username: id,
      password: password,
    };

    await UserService.login(params)
      .then((response) => {
        const data = response.headers;
        const { access, refresh, role, id } = data;
        console.log(data);
        // setCookie('refresh', refresh);

        console.log(access, refresh);
        sessionStorage.setItem('access', access);
        sessionStorage.setItem('refresh', refresh);
        sessionStorage.setItem('role', role);
        sessionStorage.setItem('id', id);

        setIsLogin(true);
        window.location.replace('/listForm');
      })
      .catch((e) => {
        console.log('로그인 에러', e);
        return alert('아이디나 비밀번호를 확인해주세요  ');
      });
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        width: '100%',
        height: '100%',
      }}
    >
      <form
        style={{ display: 'flex', flexDirection: 'column' }}
        onSubmit={onSubmitHandler}
      >
        <label>아이디</label>
        <input type="text" value={id} onChange={onIdHandler} />
        <label>비밀번호</label>
        <input type="password" value={password} onChange={onPasswordHandler} />
        <br />
        <button formAction="">Login</button>

        <Link to="/joinForm">회원가입 하기</Link>
      </form>
    </div>
  );
};

export default LoginForm;
