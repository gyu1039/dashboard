import React from 'react';

const Init = () => {
  return (
    <div>
      <img
        src={process.env.PUBLIC_URL + '/logo192.png'}
        alt="이미지를 불러올수 없습니다"
      />
    </div>
  );
};

export default Init;
