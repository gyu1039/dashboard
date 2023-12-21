import React, { useEffect, useState } from 'react';
import { Button, Form } from 'react-bootstrap';
import { useLocation, useNavigate } from 'react-router';
import PostService from '../../services/PostService';

const UpdateForm = () => {
  const [post, setPost] = useState({
    postId: '',
    title: '',
    // category: '',
    content: '',
    writerDTO: '',
    commentInfoDTOList: [],
  });

  const [file, setFile] = useState('');

  const [imgFile, setImgFile] = useState('');

  const formData = new FormData();

  const router = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!post.filePath) {
      setPost(location.state.post);
    }

    const reader = new FileReader();
    console.log(post.filePath);
    if (post.filePath) {
      const path = post.filePath.replace(
        'C:\\Users\\Administrator\\Desktop\\tmp\\',
        '',
      );
      PostService.getImage(path)
        .then((response) => {
          reader.readAsDataURL(response.data);
          reader.onloadend = () => {
            setImgFile(reader.result);
          };
          console.log(response);
          setFile(new File([response.data], 'file'));
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [post.filePath]);

  const deleteImage = () => {
    setImgFile('');
    setFile(null);
  };

  const handleImage = (e) => {
    const file = e.target.files[0];
    console.log(1, file);

    if (!file) {
      setImgFile('');
      return;
    }

    setFile(file);

    const reader = new FileReader();
    reader.readAsDataURL(file);

    reader.onloadend = () => {
      setImgFile(reader.result);
    };
  };

  const updatePost = () => {
    formData.append(
      'post',
      new Blob([JSON.stringify(post)], { type: 'application/json' }),
    );
    formData.append('file', file);
    PostService.updatePost(post.postId, formData)
      .then((response) => {
        console.log(response);

        alert('수정되었습니다');

        router(`/post/${post.postId}`);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const chageValue = (e) => {
    setPost({
      ...post,
      [e.target.name]: e.target.value,
    });
  };
  return (
    <div>
      <h1>게시글 수정하기</h1>
      <br />
      <Form>
        <Form.Group controlId="formWriter" className="mb-3">
          <Form.Label>작성자</Form.Label>
          <Form.Control
            className="w-50"
            type="text"
            name="writer"
            value={post.writerDTO.nickname || ''}
            disabled
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formTitle">
          <Form.Label>제목</Form.Label>
          <Form.Control
            className="w-50"
            type="text"
            value={post.title}
            onChange={chageValue}
            name="title"
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formContent">
          <Form.Label>내용</Form.Label>
          <Form.Control
            as="textarea"
            name="content"
            value={post.content}
            onChange={chageValue}
          />
        </Form.Group>

        <Form.Group controlId="formFile" className="mb-3">
          {!imgFile ? (
            <>
              <Form.Label>파일 첨부</Form.Label>
              <Form.Control
                accept="image/*"
                type="file"
                name="formData"
                placeholder="선택된 파일이 없습니다"
                onChange={(e) => handleImage(e)}
              />
            </>
          ) : (
            <></>
          )}

          {imgFile !== undefined && imgFile !== '' ? (
            <>
              <img src={imgFile} alt="이미지가 없습니다." />
              <Button onClick={deleteImage}>이미지 삭제</Button>
            </>
          ) : (
            <></>
          )}
        </Form.Group>

        <Button variant="warning" onClick={updatePost}>
          수정하기
        </Button>

        <Button
          onClick={() => {
            router('/listForm');
          }}
        >
          취소
        </Button>
      </Form>
    </div>
  );
};

export default UpdateForm;
