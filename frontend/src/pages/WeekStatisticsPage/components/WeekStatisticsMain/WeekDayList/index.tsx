import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';

import WeekDayItem from './WeekDayItem';
import * as S from './WeekDayList.styled';

import Icon from '@/components/Icon';
import { DayInfo } from '@/pages/WeekStatisticsPage/WeekStatisticsPage.types';
import {
  getStartOfWeek,
  getWeekDateRange,
  isToday,
  isTodayInWeek,
} from '@/pages/WeekStatisticsPage/WeekStatisticsPage.utils';
import theme from '@/styles/theme';

const WEEK_DAYS = 7;

interface WeekDateProps {
  weekStartDate: string;
  setWeekStartDate: (date: string) => void;
  dayUsageTime: {
    date: string;
    usageTime: number;
  }[];
}

const WeekDayList = ({ weekStartDate, setWeekStartDate, dayUsageTime }: WeekDateProps) => {
  const navigate = useNavigate();
  const [weekDays, setWeekDays] = useState<DayInfo[]>([]);

  useEffect(() => {
    const calculatedDays = Array.from({ length: WEEK_DAYS }, (_, index) => {
      const date = new Date(weekStartDate);
      date.setDate(date.getDate() + index);

      return {
        year: date.getFullYear(),
        month: date.getMonth() + 1,
        day: date.getDate(),
      };
    });
    setWeekDays(calculatedDays);
  }, [weekStartDate]);

  const getUsageTimeForDay = (day: DayInfo) => {
    const dateString = `${day.year}-${String(day.month).padStart(2, '0')}-${String(day.day).padStart(2, '0')}`;
    const dayData = dayUsageTime.find((item) => item.date === dateString);

    return dayData ? dayData.usageTime : 0;
  };

  const handleWeekChange = (offset: number) => {
    const currentDate = new Date(weekStartDate);
    currentDate.setDate(currentDate.getDate() + offset);
    const newWeekStartDate = getStartOfWeek(currentDate.toISOString());
    setWeekStartDate(newWeekStartDate);
    navigate(`/statistics/week?startDate=${newWeekStartDate}`);
  };

  return (
    <S.WeekDateContainer>
      <S.WeekDateInfoContainer>
        <button onClick={() => handleWeekChange(-WEEK_DAYS)}>
          <Icon icon="FilledArrow" rotate={90} />
        </button>
        <div>{getWeekDateRange(weekDays)}</div>
        <button onClick={() => handleWeekChange(WEEK_DAYS)} disabled={isTodayInWeek(weekStartDate)}>
          <Icon
            icon="FilledArrow"
            rotate={-90}
            color={isTodayInWeek(weekStartDate) ? theme.colors.gray200 : ''}
          />
        </button>
      </S.WeekDateInfoContainer>

      <S.WeekDayList>
        {weekDays.map((date) => {
          const usageTime = getUsageTimeForDay(date);

          return (
            <WeekDayItem
              key={`${date.year}/${date.month}/${date.day}`}
              date={date}
              usageTime={usageTime}
              isToday={isToday(date)}
            />
          );
        })}
      </S.WeekDayList>
    </S.WeekDateContainer>
  );
};

export default WeekDayList;
