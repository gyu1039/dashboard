import http from '../http-common';

const register = (params) => {
  return http.post('/signup', params);
};

const isDuplicatedId = (username) => {
  return http.get(`/checkid/${username}`);
};

const login = (params) => {
  return http.post('/login', params);
};

const logout = async (refresh) => {
  return await http.get('/logout', {
    params: { refresh: refresh },
  });
};

const validRefresh = () => {
  return http.get('/refresh');
};

const getUsers = (page) => {
  return http.get('/members', {
    params: {
      page: page,
    },
  });
};

const getUserInfo = (id) => {
  return http.get(`/member/${id}`);
};

const getMyInfo = () => {
  return http.get('/member');
};

const UserService = {
  register,
  isDuplicatedId,
  login,
  logout,
  validRefresh,
  getUsers,
  getUserInfo,
  getMyInfo,
};

export default UserService;
