import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router';

import DayCategoryAchievement from './components/DayCategoryAchievement';
import DayDate from './components/DayDate';
import DayFeedback from './components/DayFeedback';
import DayStatisticsHeader from './components/DayStatisticsHeader';
import DayTimeOverview from './components/DayTimeOverview';
import * as S from './DayStatisticsPage.styled';
import { changeDateByDirection, getFormattedDate } from './DayStatisticsPage.utils';
import useDayStatisticsQuery from './hooks/useDayStatisticsQuery';

const DayStatisticsPage = () => {
  const navigate = useNavigate();
  const today = new Date();
  const [searchParams] = useSearchParams();
  const [date, setDate] = useState(searchParams.get('date') ?? getFormattedDate(today));

  const { data: dayStatistics } = useDayStatisticsQuery(date);

  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search);
    const date = searchParams.get('date');

    if (date) {
      setDate(date);
    }
  }, []);

  useEffect(() => {
    window.history.replaceState({}, '', `/statistics/day?date=${date}`);
  }, [date]);

  const handleClickDay = (direction: 'prev' | 'next') => {
    const newDate = changeDateByDirection(date, direction);
    setDate(newDate);
    window.history.replaceState({}, '', `/statistics/day?date=${newDate}`);
  };

  return (
    <S.DayStatisticsPageContainer>
      <DayStatisticsHeader />
      <DayDate date={date} onDayChange={handleClickDay} />

      <S.DayStatisticsContent>
        <DayTimeOverview
          moveTime={dayStatistics?.totalMoveTime ?? 0}
          usageTime={dayStatistics?.totalUsageTime ?? 0}
          isMoved={(dayStatistics?.totalMoveTime ?? 0) > 0}
        />

        {dayStatistics ? (
          <>
            <DayFeedback feedbacks={dayStatistics?.feedbacks ?? []} />
            <DayCategoryAchievement categories={dayStatistics?.categoryTodoCounts ?? []} />
          </>
        ) : (
          <S.DayEmpty>이 날은 이동을 하지 않았어요</S.DayEmpty>
        )}
      </S.DayStatisticsContent>
    </S.DayStatisticsPageContainer>
  );
};

export default DayStatisticsPage;
