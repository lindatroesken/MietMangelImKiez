import styled from 'styled-components/macro'
import Label from './Label'

export default function SelectAddress({
  name,
  key,
  address,
  selected,
  title,
  onChange,
  value,
  values,
  readOnly,
  ...props
}) {
  return (
    <Label {...props}>
      {title}
      <SelectStyled
        name={name}
        value={value}
        onChange={event => onChange(event, address)}
        disabled={readOnly}
      >
        {values.map(value => (
          <option key={key} value={value} disabled={!value}>
            {value}
          </option>
        ))}
      </SelectStyled>
    </Label>
  )
}

const SelectStyled = styled.select`
  width: 100%;
  font-size: 1em;
  padding: var(--size-s);
  margin-top: var(--size-s);
  border: none;
  border-radius: var(--size-s);
`
