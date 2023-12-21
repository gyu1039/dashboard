import React from 'react';
import Pagination from 'react-js-pagination';
import './Paging.css';

const Paging = ({ page, count, setPage }) => {
  return (
    <Pagination
      activePage={page}
      itemsCountPerPage={20}
      totalItemsCount={count}
      pageRangeDisplayed={10}
      prevPageText={'<'}
      nextPageText={'>'}
      onChange={setPage}
    />
  );
};

export default Paging;
