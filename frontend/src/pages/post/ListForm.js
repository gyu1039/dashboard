import React, { useEffect, useMemo, useState } from 'react';
import PostService from '../../services/PostService';

import { useTable } from 'react-table';
import Pagination from 'react-js-pagination';
import { useNavigate } from 'react-router';

const ListForm = () => {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [count, setCount] = useState(0);
  const router = useNavigate();

  const retrievePosts = () => {
    const params = {
      page: page - 1,
    };

    PostService.getPostList(params)
      .then((response) => {
        const totalPages = response.data;

        setPosts(totalPages.simpleDTOList);
        setCount(totalPages.totalElementCount);
      })
      .catch((e) => {
        console.log(e);
      });
  };

  useEffect(retrievePosts, [page]);

  const handlePageChange = (page) => {
    console.log(page);
    setPage(page);
  };

  const handleRowClick = (postId) => {
    router(`/post/${postId}`);
  };

  const columns = useMemo(
    () => [
      {
        Header: 'No',
        accessor: 'postId',
      },
      {
        Header: 'Title',
        accessor: 'title',
      },
      {
        Header: 'writer',
        accessor: 'writerName',
      },
    ],
    [],
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } =
    useTable({ columns, data: posts || [] });

  return (
    <>
      <div className="list row">
        <table
          className="table table-striped table-bordered"
          {...getTableProps()}
        >
          <thead>
            {headerGroups.map((headerGroup) => (
              <tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map((column) => (
                  <th {...column.getHeaderProps()}>
                    {column.render('Header')}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
            {rows.map((row, i) => {
              prepareRow(row);
              return (
                <tr
                  {...row.getRowProps()}
                  onClick={() => handleRowClick(row.original.postId)}
                >
                  {row.cells.map((cell) => {
                    return (
                      <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>

        <Pagination
          activePage={page}
          itemsCountPerPage={20}
          totalItemsCount={count}
          pageRangeDisplayed={10}
          prevPageText={'<'}
          nextPageText={'>'}
          onChange={handlePageChange}
        />
      </div>
    </>
  );
};

export default React.memo(ListForm);
