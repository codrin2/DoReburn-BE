import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router';

import DayCategoryAchievement from './components/DayCategoryAchievement';
import DayDate from './components/DayDate';
import DayFeedback from './components/DayFeedback';
import DayStatisticsHeader from './components/DayStatisticsHeader';
import DayTimeOverview from './components/DayTimeOverview';
import * as S from './DayStatisticsPage.styled';
import useDayStatisticsQuery from './hooks/useDayStatisticsQuery';

const DayStatisticsPage = () => {
  const navigate = useNavigate();
  const today = new Date();
  const todayYear = today.getFullYear();
  const todayMonth = (today.getMonth() + 1).toString().padStart(2, '0');
  const todayDate = today.getDate().toString().padStart(2, '0');

  const [searchParams] = useSearchParams();
  const [date, setDate] = useState(
    searchParams.get('date') ?? `${todayYear}-${todayMonth}-${todayDate}`,
  );

  const { data: dayStatistics } = useDayStatisticsQuery(date);

  const handleClickDay = (direction: 'prev' | 'next') => {
    const newDate = new Date(date);
    newDate.setDate(newDate.getDate() + (direction === 'prev' ? -1 : 1));
    setDate(newDate.toISOString().split('T')[0]);
    navigate(`/statistics/day?date=${newDate.toISOString().split('T')[0]}`);
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
