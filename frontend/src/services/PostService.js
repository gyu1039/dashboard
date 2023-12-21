import http from '../http-common';

const getPostList = (params) => {
  return http.get('/posts', {
    params: {
      page: params.page,
    },
  });
};

const save = (data) => {
  return http.post('/posts', data, {
    headers: {
      'Content-Type': `multipart/form-data`,
    },
  });
};

const getInfo = (id) => {
  return http.get(
    `/posts/${id}`,
    // {
    //   responseType: 'blob',
    // }
  );
};

const deletePost = (id) => {
  return http.delete(`/posts/${id}`);
};

const updatePost = (id, data) => {
  return http.put(`/posts/${id}`, data, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

const getImage = (path) => {
  return http.get(`/images/${path}`, {
    responseType: 'blob',
  });
};

const PostService = {
  getPostList,
  save,
  getInfo,
  deletePost,
  updatePost,
  getImage,
};

export default PostService;
