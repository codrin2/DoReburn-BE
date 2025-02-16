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
}

const WeekDayItem = ({ date, usageTime, isToday }: WeekDayItemProps) => {
  const today = new Date();
  const currentDate = new Date(date.year, date.month - 1, date.day);

  const isFutureDate = currentDate > today;

  return (
    <S.WeekDayItemContainer>
      <S.DayButton disabled={isFutureDate}>
        {usageTime > 0 && <Icon icon="Fire" width={16} height={16} color={theme.colors.green200} />}
        <S.Day $isToday={isToday} $isFutureDay={isFutureDate}>
          {date.day}
        </S.Day>
        {usageTime > 0 && <S.DayUsageTime>{usageTime}</S.DayUsageTime>}
      </S.DayButton>
    </S.WeekDayItemContainer>
  );
};

export default WeekDayItem;
