import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';

import * as S from './WeekDate.styled';
import WeekDayItem from './WeekDayItem';

import Icon from '@/components/Icon';
import { getStartOfWeek } from '@/pages/WeekStatisticsPage/utils/getStartOfWeek';

interface WeekDateProps {
  weekStartDate: string;
  setWeekStartDate: (date: string) => void;
}

interface DayInfo {
  year: number;
  month: number;
  day: number;
}

const WeekDate = ({ weekStartDate, setWeekStartDate }: WeekDateProps) => {
  const navigate = useNavigate();
  const [weekDays, setWeekDays] = useState<DayInfo[]>([]);

  useEffect(() => {
    const calculatedDays = Array.from({ length: 7 }, (_, index) => {
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

  const handlePrevWeekClick = () => {
    const currentDate = new Date(weekStartDate);
    currentDate.setDate(currentDate.getDate() - 7);
    const prevWeekStartDate = getStartOfWeek(currentDate.toISOString());
    setWeekStartDate(prevWeekStartDate);
    navigate(`/statistics/week?startDate=${prevWeekStartDate}`);
  };

  const handleNextWeekClick = () => {
    const currentDate = new Date(weekStartDate);
    currentDate.setDate(currentDate.getDate() + 7);
    const nextWeekStartDate = getStartOfWeek(currentDate.toISOString());
    setWeekStartDate(nextWeekStartDate);
    navigate(`/statistics/week?startDate=${nextWeekStartDate}`);
  };

  return (
    <S.WeekDateContainer>
      <S.WeekDateInfoContainer>
        <button onClick={handlePrevWeekClick}>
          <Icon icon="FilledArrow" rotate={90} />
        </button>
        <div>
          {weekDays.length > 0 && weekDays[0].month}월 {weekDays.length > 0 && weekDays[0].day}일
          (월) - {weekDays.length > 0 && weekDays[6].month}월{' '}
          {weekDays.length > 0 && weekDays[6].day}일 (일)
        </div>
        <button onClick={handleNextWeekClick}>
          <Icon icon="FilledArrow" rotate={-90} />
        </button>
      </S.WeekDateInfoContainer>

      <S.WeekDayList className="week-day-list">
        {weekDays.map((day) => (
          <WeekDayItem key={`${day.year}/${day.month}/${day.day}`} day={day.day} />
        ))}
      </S.WeekDayList>
    </S.WeekDateContainer>
  );
};

export default WeekDate;
