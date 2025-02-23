import { useNavigate } from 'react-router';

import * as S from './WeekDayItem.styled';

import Icon from '@/components/Icon';
import theme from '@/styles/theme';

interface WeekDayItemProps {
  date: {
    year: number;
    month: number;
    day: number;
  };
  usageTime: number;
  isToday: boolean;
  memberCreateDate: string;
}

const WeekDayItem = ({ date, usageTime, isToday, memberCreateDate }: WeekDayItemProps) => {
  const navigate = useNavigate();
  const today = new Date();
  const currentDate = new Date(date.year, date.month - 1, date.day);
  const memberCreateDateObj = new Date(memberCreateDate);
  currentDate.setHours(0, 0, 0, 0);
  memberCreateDateObj.setHours(0, 0, 0, 0);
  const isFutureDate = currentDate > today;
  const isBeforeCreated = currentDate < memberCreateDateObj;

  const handleClickDay = () => {
    navigate(
      `/statistics/day?date=${date.year}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}`,
    );
  };

  return (
    <S.WeekDayItemContainer>
      <S.DayButton disabled={isFutureDate || isBeforeCreated} onClick={handleClickDay}>
        {usageTime > 0 && <Icon icon="Fire" width={16} height={16} color={theme.colors.green200} />}
        <S.Day $isToday={isToday} $isDisabledDay={isFutureDate || isBeforeCreated}>
          {date.day}
        </S.Day>
        {usageTime > 0 && <S.DayUsageTime>{usageTime}</S.DayUsageTime>}
      </S.DayButton>
    </S.WeekDayItemContainer>
  );
};

export default WeekDayItem;
