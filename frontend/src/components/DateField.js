import Label from './Label'
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'

export default function DateField({
  value,
  onChange,
  name,
  title,
  readOnly,
  ...props
}) {
  return (
    <Label {...props}>
      {title}
      <DatePicker
        name={name}
        onChange={date => onChange(date, name)}
        selected={value}
        dateFormat="dd/MMM/yyyy HH:mm"
        showTimeSelect
        disabled={readOnly}
      />
    </Label>
  )
}
