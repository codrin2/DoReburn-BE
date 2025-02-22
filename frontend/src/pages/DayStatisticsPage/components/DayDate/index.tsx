import * as S from './DayDate.styled';

import Icon from '@/components/Icon';
import theme from '@/styles/theme';

interface DayDateProps {
  date: string;
  memberCreateDate: string;
  onDayChange: (direction: 'prev' | 'next') => void;
}

const DayDate = ({ date, memberCreateDate, onDayChange }: DayDateProps) => {
  const dateObj = new Date(date);
  const todayObj = new Date();
  dateObj.setHours(0, 0, 0, 0);
  todayObj.setHours(0, 0, 0, 0);
  const month = dateObj.getMonth() + 1;
  const day = dateObj.getDate();
  const dayOfWeek = dateObj.getDay();
  const dayOfWeekString = ['일', '월', '화', '수', '목', '금', '토'][dayOfWeek];
  const isBeforeCreated = dateObj <= new Date(memberCreateDate);
  const isFutureDate = dateObj >= todayObj;
  const handleClickDay = (direction: 'prev' | 'next') => {
    onDayChange(direction);
  };

  return (
    <S.DayDateContainer>
      <S.IconButton onClick={() => handleClickDay('prev')} disabled={isBeforeCreated}>
        <Icon icon="FilledArrow" rotate={90} color={isBeforeCreated ? theme.colors.gray200 : ''} />
      </S.IconButton>
      <S.DayDateText>{`${month}월 ${day}일 ${dayOfWeekString}요일`}</S.DayDateText>
      <S.IconButton onClick={() => handleClickDay('next')} disabled={isFutureDate}>
        <Icon icon="FilledArrow" rotate={270} color={isFutureDate ? theme.colors.gray200 : ''} />
      </S.IconButton>
    </S.DayDateContainer>
  );
};

export default DayDate;
