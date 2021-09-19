import React, { useMemo } from 'react'

import Table from './Table'
import TimeAgo from 'react-timeago'

export default function MangelTable({ data, handleGoToDetails }) {
  const columns = useMemo(
    () => [
      {
        Header: 'Meine Mängelübersicht',
        columns: [
          {
            Header: 'Seit',
            accessor: 'dateNoticed',
            Cell: ({ cell: { value } }) => <TimeAgo date={value} />,
          },
          {
            Header: 'Kategorie',
            accessor: 'category',
          },
          {
            Header: 'Status',
            accessor: 'status',
          },
        ],
      },
    ],
    []
  )

  return (
    <Table
      columns={columns}
      data={data}
      handleOnListItemClick={handleGoToDetails}
    />
  )
}
