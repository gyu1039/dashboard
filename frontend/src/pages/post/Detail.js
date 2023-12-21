import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import PostService from '../../services/PostService';
import { Button, Form } from 'react-bootstrap';

const Detail = () => {
  const propsParam = useParams();
  const id = propsParam.id;
  const router = useNavigate();
  const [imgFile, setImgFile] = useState('');
  const [file, setFile] = useState(null);

  const [post, setPost] = useState({
    postId: '',
    title: '',
    category: '',
    content: '',
    writerDTO: '',
    filePath: '',
    commentInfoDTOList: [],
  });

  const getPost = async () => {
    await PostService.getInfo(id)
      .then((response) => {
        const data = response.data;
        setPost(data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    if (!post.filePath) {
      getPost();
    }

    console.log(post.filePath);
    const reader = new FileReader();
    if (post.filePath) {
      const path = post.filePath.replace(
        'C:\\Users\\Administrator\\Desktop\\tmp\\',
        '',
      );
      PostService.getImage(path)
        .then((response) => {
          setFile(new File([response.data], 'file'));

          reader.readAsDataURL(response.data);
          reader.onloadend = () => {
            setImgFile(reader.result);
          };
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [post.filePath]);

  const deletePost = () => {
    PostService.deletePost(post.postId)
      .then((response) => {
        console.log(response);
        alert('삭제 되었습니다');
        window.location.replace('/listForm');
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const updatePost = (post) => {
    router(`/updateForm/${post.postId}`, {
      state: { post: post },
    });
  };

  return (
    <div>
      <h1>게시글 상세보기</h1> <br />
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
            value={post.title || ''}
            disabled
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formContent">
          <Form.Label>내용</Form.Label>
          <Form.Control
            as="textarea"
            name="content"
            value={post.content || ''}
            disabled
          />
        </Form.Group>

        <Form.Group controlId="formFile" className="mb-3">
          {imgFile !== undefined && imgFile !== '' ? (
            <img src={imgFile} alt="이미지가 없습니다." />
          ) : (
            <></>
          )}
        </Form.Group>

        {sessionStorage.getItem('id') == post.writerDTO.username ||
        sessionStorage.getItem('role') === 'ADMIN' ? (
          <>
            <Button variant="warning" onClick={() => updatePost(post)}>
              수정
            </Button>
          </>
        ) : (
          <></>
        )}
        {sessionStorage.getItem('role') === 'ADMIN' && (
          <>
            <Button variant="danger" onClick={deletePost}>
              삭제
            </Button>
          </>
        )}

        <Button
          onClick={() => {
            router('/listForm');
          }}
        >
          목록
        </Button>
      </Form>
    </div>
  );
};

export default React.memo(Detail);
