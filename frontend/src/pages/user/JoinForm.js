import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import UserService from '../../services/UserService';
import { Button } from 'react-bootstrap';

const JoinForm = () => {
  const [id, setId] = useState('');
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isDupliacted, setIsDuplicate] = useState(true);
  const [emailValid, setEmailValid] = useState(false);
  const [idCheck, setIdCheck] = useState(false);
  const [pwValid, setPwValid] = useState(false);
  const [pweq, setPweq] = useState(false);
  const [notAllow, setNotAllow] = useState(true);
  const [role, setRole] = useState('USER');

  const onIdHandler = (e) => {
    setId(e.target.value);
    setIdCheck(false);

    const regex = /^[\w.-]+@[\w.-]+\.[a-zA-Z]{1,3}$/;

    if (regex.test(id)) {
      setEmailValid(true);
    } else {
      setEmailValid(false);
    }
  };

  const onNickNameHandler = (e) => {
    setNickname(e.target.value);
  };

  const onRoleHandler = (e) => {
    setRole(e.target.value);
  };

  const onPasswordHandler = (e) => {
    const curPassword = e.target.value;
    setPassword(curPassword);

    if (curPassword === confirmPassword) {
      setPweq(true);
    } else {
      setPweq(false);
    }

    const passwordRegex =
      /^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)\-_=+])(?!.*[^a-zA-z0-9$`~!@$!%*#^?&\\(\\)\-_=+]).{8,20}$/;

    if (passwordRegex.test(curPassword)) {
      setPwValid(true);
    } else {
      setPwValid(false);
    }
  };

  const onConfirmPasswordHandler = (e) => {
    const value = e.target.value;
    setConfirmPassword(value);

    if (password === value) {
      setPweq(true);
      return;
    }

    setPweq(false);
  };

  useEffect(() => {
    if (emailValid && pweq && idCheck && nickname && pwValid) {
      setNotAllow(false);
      return;
    }

    setNotAllow(true);
  }, [emailValid, pweq, idCheck, nickname, pwValid]);

  const checkDuplicated = async (e) => {
    e.preventDefault();

    if (!emailValid) {
      return alert('아이디를 이메일 형식으로 작성해 주세요.');
    }

    const response = await UserService.isDuplicatedId(id.trim());

    if (response.data) {
      alert('아이디가 중복됩니다. 다른 아이디를 사용해주세요.');
    } else {
      alert('사용하실 수 있는 아이디입니다.');
      setIdCheck(true);
    }

    setIsDuplicate(response.data);
  };

  const onSubmitHandler = (e) => {
    e.preventDefault();

    if (!id.trim()) {
      return alert('아이디를 입력해주세요.');
    }

    if (!nickname.trim()) {
      return alert('닉네임을 입력해주세요.');
    }

    if (!password.trim() || !confirmPassword.trim()) {
      return alert('비밀번호를 입력해주세요.');
    }

    if (isDupliacted) {
      return alert('아이디를 확인해주세요.');
    }

    let params = {
      username: id,
      password: password,
      nickname: nickname,
      role: role,
    };

    UserService.register(params)
      .then((response) => {
        console.log(response);
        alert('회원가입이 완료되었습니다.');
        (() => window.location.replace('http://localhost:3000'))();
      })
      .catch((e) => {
        console.log(e);
        alert('회원가입 실패.');
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
        <label>
          <b>아이디</b>
        </label>
        <input
          type="text"
          value={id}
          onChange={onIdHandler}
          placeholder="test@gmail.com"
        />
        <div className="errorMessageWrap">
          {!emailValid && id.length > 0 && (
            <div>이메일 형식으로 입력해주세요.</div>
          )}
        </div>
        <Button
          className="checkbutton"
          variant="primary"
          onClick={checkDuplicated}
        >
          아이디 확인
        </Button>
        <label>
          <b>닉네임</b>
        </label>
        <input
          className="checknickname"
          type="text"
          value={nickname}
          onChange={onNickNameHandler}
          placeholder="닉네임"
        />
        <label style={{ marginTop: '5px' }}>
          <b>역할</b>
        </label>
        <div onChange={onRoleHandler}>
          <input
            type="radio"
            name="role"
            id="user"
            value="USER"
            defaultChecked
          />
          <label htmlFor="user">사용자</label> &nbsp;&nbsp;
          <input type="radio" name="role" id="admin" value="ADMIN" />
          <label htmlFor="admin">관리자</label>
        </div>
        <label style={{ marginTop: '10px' }}>
          <b>비밀번호</b>
        </label>
        <input
          type="password"
          value={password}
          onChange={onPasswordHandler}
          placeholder="비밀번호"
        />
        <div className="errorMessageWrap">
          {!pwValid && password.length > 0 && (
            <>영문, 숫자, 특수문자 포함 8자 이상 입력해주세요.</>
          )}
        </div>
        <label style={{ marginTop: '5px' }}>
          <b>비밀번호 확인</b>
        </label>
        <input
          type="password"
          value={confirmPassword}
          onChange={onConfirmPasswordHandler}
          placeholder="비밀번호 확인"
        />
        <div className="errorMessageWrap">
          {confirmPassword.length > 0 && password !== confirmPassword && (
            <div>비밀번호를 다시 확인해주세요.</div>
          )}
        </div>
        <br />
        <button disabled={notAllow} type="submit">
          회원가입
        </button>
      </form>
    </div>
  );
};

export default JoinForm;
