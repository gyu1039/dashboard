import React, { useRef, useState } from 'react';
import { Button, Form } from 'react-bootstrap';
import PostService from '../../services/PostService';
import { useNavigate } from 'react-router';

const SaveForm = () => {
  const [post, setPost] = useState({
    title: '',
    content: '',
  });
  const [file, setFile] = useState('');

  const [imgFile, setImgFile] = useState('');

  const formData = new FormData();
  const router = useNavigate();

  const changeValue = (e) => {
    setPost({
      ...post,
      [e.target.name]: e.target.value,
    });
  };

  const handleImage = (e) => {
    const file = e.target.files[0];
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

  const checkImage = (file) => {
    let err = '';

    if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
      err = '이미지 파일 형식만 저장할 수 있습니다';
    }

    return err;
  };

  const submitPost = async (e) => {
    e.preventDefault();

    formData.append(
      'post',
      new Blob([JSON.stringify(post)], { type: 'application/json' }),
    );

    formData.append('file', file);
    await PostService.save(formData)
      .then((response) => {
        console.log(response);
      })
      .catch((e) => {
        console.log(e);
      });

    router('/listForm');
  };
  return (
    <Form onSubmit={submitPost}>
      <Form.Group className="mb-3" controlId="formtitle">
        <Form.Label>제목</Form.Label>
        <Form.Control
          className="w-50"
          type="text"
          placeholder="제목을 입력하세요"
          onChange={changeValue}
          name="title"
        />
      </Form.Group>

      <Form.Group className="mb-3" controlId="formContent">
        <Form.Label>내용</Form.Label>
        <Form.Control
          as="textarea"
          placeholder="내용을 입력하세요"
          onChange={changeValue}
          name="content"
        />
      </Form.Group>

      <Form.Group controlId="formFile" className="mb-3">
        <Form.Label>파일 첨부</Form.Label>
        <Form.Control
          accept="image/*"
          type="file"
          name="uploadfile"
          placeholder="선택된 파일이 없습니다"
          onChange={(e) => handleImage(e)}
        />

        {imgFile !== undefined && imgFile !== '' ? (
          <img src={imgFile} alt="이미지가 없습니다." />
        ) : (
          <></>
        )}
      </Form.Group>

      <Button type="submit" variant="primary">
        저장하기
      </Button>
    </Form>
  );
};

export default SaveForm;
