import React, { useEffect, useMemo, useState } from 'react';
import UserService from '../../services/UserService';
import { useNavigate } from 'react-router';
import { useTable } from 'react-table';
import Pagination from 'react-js-pagination';

const UserList = () => {
  const [users, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [count, setCount] = useState(0);
  const router = useNavigate();

  const retrieveUsers = () => {
    UserService.getUsers(page - 1)
      .then((response) => {
        const users = response.data;
        setPosts(users.memberList);
        setCount(users.totalElementCount);
      })
      .catch((e) => {
        console.log(e);
      });
  };

  useEffect(retrieveUsers, [page]);

  const handlePageChange = (page) => {
    console.log(page);
    setPage(page);
  };

  const handleRowClick = (memberId) => {
    router(`/user/${memberId}`);
  };

  const columns = useMemo(
    () => [
      {
        Header: 'No',
        accessor: 'memberId',
      },
      {
        Header: '아이디',
        accessor: 'username',
      },
    ],
    [],
  );

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } =
    useTable({ columns, data: users || [] });

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
                  onClick={() => handleRowClick(row.original.memberId)}
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

export default UserList;
