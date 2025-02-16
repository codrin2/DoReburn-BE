import * as S from './DayDate.styled';

import Icon from '@/components/Icon';

interface DayDateProps {
  date: string;
  onDayChange: (direction: 'prev' | 'next') => void;
}

const DayDate = ({ date, onDayChange }: DayDateProps) => {
  const dateObj = new Date(date);
  const month = dateObj.getMonth() + 1;
  const day = dateObj.getDate();
  const dayOfWeek = dateObj.getDay();
  const dayOfWeekString = ['일', '월', '화', '수', '목', '금', '토'][dayOfWeek];

  const handleClickDay = (direction: 'prev' | 'next') => {
    onDayChange(direction);
  };

  return (
    <S.DayDateContainer>
      <button onClick={() => handleClickDay('prev')}>
        <Icon icon="FilledArrow" rotate={90} />
      </button>
      <S.DayDateText>{`${month}월 ${day}일 ${dayOfWeekString}요일`}</S.DayDateText>
      <button onClick={() => handleClickDay('next')}>
        <Icon icon="FilledArrow" rotate={270} />
      </button>
    </S.DayDateContainer>
  );
};

export default DayDate;
