import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router';

const UserUpdate = () => {
  const [user, setUser] = useState({
    memberId: '',
    username: '',
    nickname: '',
    memberRole: '',
  });

  const location = useLocation();

  useEffect(() => {
    setUser(location.state.user);
  });

  const updateInfo = () => {};

  const updatePassword = () => {};

  const changeValue = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
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
      <form style={{ display: 'flex', flexDirection: 'column' }}>
        <label>
          <b>아이디</b>
        </label>
        <input type="text" value={user.username} disabled />
        <label>
          <b>닉네임</b>
        </label>
        <input
          className="checknickname"
          type="text"
          value={user.nickname}
          placeholder="닉네임"
        />
        <label style={{ marginTop: '5px' }}>
          <b>역할</b>
        </label>
        <div onChange={changeValue}>
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
        {/* <label style={{ marginTop: '10px' }}>
          <b>비밀번호</b>
        </label>
        <input
          type="password"
          value={user.password}
          onChange={onPasswordHandler}
          placeholder="비밀번호"
        /> */}
        {/* <div className="errorMessageWrap">
          {!pwValid && password.length > 0 && (
            <div>영문, 숫자, 특수문자 포함 8자 이상 입력해주세요.</div>
          )}
        </div> */}
        <br />
        <div>
          <button onClick={updateInfo}>수정하기</button>
          <button onClick={updatePassword}>비밀번호 변경</button>
        </div>
      </form>
    </div>
  );
};

export default UserUpdate;
