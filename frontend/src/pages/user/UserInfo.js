import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router';
import UserService from '../../services/UserService';

const UserInfo = () => {
  const propsParam = useParams();
  const id = propsParam.id;
  const router = useNavigate();

  const [user, setUser] = useState({
    memberId: '',
    username: '',
    nickname: '',
    memberRole: '',
  });

  useEffect(() => {
    UserService.getUserInfo(id)
      .then((response) => {
        setUser(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

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
        // onSubmit={onSubmitHandler}
      >
        <label>
          <b>아이디</b>
        </label>
        <input
          type="text"
          value={user.username}
          // onChange={onIdHandler}
          disabled
        />
        {/* <div className="errorMessageWrap">
          {!emailValid && id.length > 0 && (
            <div>이메일 형식으로 입력해주세요.</div>
          )}
        </div> */}
        <label>
          <b>닉네임</b>
        </label>
        <input
          className="checknickname"
          type="text"
          value={user.nickname || ''}
          // onChange={onNickNameHandler}
          disabled
        />
        <label style={{ marginTop: '5px' }}>
          <b>역할</b>
        </label>
        <div
        // onChange={onRoleHandler}
        >
          <input
            type="radio"
            name="role"
            id="user"
            value="USER"
            checked={user.memberRole == 'USER'}
            disabled
          />
          <label htmlFor="user">사용자</label> &nbsp;&nbsp;
          <input
            type="radio"
            name="role"
            id="admin"
            value="ADMIN"
            disabled
            checked={user.memberRole == 'ADMIN'}
          />
          <label htmlFor="admin">관리자</label>
        </div>
        <br />
        {/* <button
          // disabled={notAllow}
          type="submit"
        >
          test
        </button> */}
      </form>
    </div>
  );
};

export default UserInfo;
