import React from 'react'
import { useTable } from 'react-table'
import styled from 'styled-components/macro'

export default function Table({ columns, data, handleOnListItemClick }) {
  // Use the useTable Hook to send the columns and data to build the table
  const {
    getTableProps, // table props from react-table
    getTableBodyProps, // table body props from react-table
    headerGroups, // headerGroups, if your table has groupings
    rows, // rows for the table based on the data passed
    prepareRow, // Prepare the row (this function needs to be called for each row before getting the row props)
  } = useTable({
    columns,
    data,
  })

  // const handleOnListItemClick = listItem => {
  //   const path = `/mangel/details/${listItem.original.id}`
  //   history.push(path)
  // }

  return (
    <StyledTable {...getTableProps()}>
      <thead>
        {headerGroups.map(headerGroup => (
          <tr {...headerGroup.getHeaderGroupProps()}>
            {headerGroup.headers.map(column => (
              <th {...column.getHeaderProps()}>{column.render('Header')}</th>
            ))}
          </tr>
        ))}
      </thead>
      <tbody {...getTableBodyProps()}>
        {rows.map((row, i) => {
          prepareRow(row)
          return (
            <tr
              {...row.getRowProps()}
              onClick={() => handleOnListItemClick(row)}
            >
              {row.cells.map(cell => {
                return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
              })}
            </tr>
          )
        })}
      </tbody>
    </StyledTable>
  )
}

const StyledTable = styled.table`
  background-color: var(--background-dark);
  padding: var(--size-l);

  thead {
  }
  tbody {
    tr {
      td {
        padding: var(--size-s);
      }
    }
  }
`
