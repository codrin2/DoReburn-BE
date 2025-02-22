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
      <S.IconButton onClick={() => handleClickDay('prev')}>
        <Icon icon="FilledArrow" rotate={90} />
      </S.IconButton>
      <S.DayDateText>{`${month}월 ${day}일 ${dayOfWeekString}요일`}</S.DayDateText>
      <S.IconButton onClick={() => handleClickDay('next')}>
        <Icon icon="FilledArrow" rotate={270} />
      </S.IconButton>
    </S.DayDateContainer>
  );
};

export default DayDate;
