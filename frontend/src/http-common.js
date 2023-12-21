import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

instance.interceptors.request.use(
  (config) => {
    const access = sessionStorage.getItem('access');
    const refresh = sessionStorage.getItem('refresh');
    // const refresh = getCookie('refresh');

    if (access) {
      config.headers['Authorization'] = access;
    }

    if (refresh) {
      config.headers['Authorization-refresh'] = refresh;
    }

    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  },
);

// instance.interceptors.response.use(
//   (config) => {
//     // UserService.validRefresh()
//     //   .then((response) => {
//     //     console.log(response);
//     //     const [access, refresh] = response.headers;
//     //     const { setIsLogin } = useContext(IsLoginContext);

//     //     if (refresh) {
//     //       sessionStorage.setItem('access', access);
//     //       sessionStorage.setItem('refresh', refresh);
//     //     } else {
//     //       sessionStorage.removeItem('access');
//     //       sessionStorage.removeItem('refresh');

//     //       setIsLogin(false);
//     //     }
//     //   })
//     //   .catch((e) => {
//     //     console.log(e);
//     //   });

//     return config;
//   },
//   (error) => {
//     console.log(error);
//     return Promise.reject(error);
//   },
// );

export default instance;
