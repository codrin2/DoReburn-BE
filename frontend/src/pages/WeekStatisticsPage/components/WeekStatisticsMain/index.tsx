import { useEffect, useState } from 'react';

import WeekCategoryList from './WeekCategoryList';
import WeekDayList from './WeekDayList';
import WeekOverview from './WeekOverview';
import * as S from './WeekStatisticsMain.styled';

import useWeekStatisticsQuery from '@/pages/WeekStatisticsPage/hooks/useWeekStatisticsQuery';
import { getStartOfWeek } from '@/pages/WeekStatisticsPage/WeekStatisticsPage.utils';

const WeekStatisticsMain = () => {
  const today = new Date().toISOString();
  const [weekStartDate, setWeekStartDate] = useState(getStartOfWeek(today));

  const { data: weekStatistics } = useWeekStatisticsQuery(weekStartDate);

  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search);
    const startDate = searchParams.get('startDate');

    if (startDate) {
      setWeekStartDate(getStartOfWeek(startDate));
    }
  }, []);

  useEffect(() => {
    window.history.pushState({}, '', `/statistics/week?startDate=${weekStartDate}`);
  }, [weekStartDate]);

  const overviewProps = {
    moveTime: weekStatistics?.totalMoveTime ?? 0,
    usageTime: weekStatistics?.totalUsageTime ?? 0,
    lastWeekDiff: weekStatistics?.lastWeekDiff ?? 0,
    todoCount: weekStatistics?.totalTodoCount ?? 0,
  };

  return (
    <S.WeekStatisticsMainContainer>
      <WeekDayList
        weekStartDate={weekStartDate}
        setWeekStartDate={setWeekStartDate}
        dayUsageTime={weekStatistics?.dayUsageTimes ?? []}
      />
      <S.WeekAchievementContainer>
        <WeekOverview {...overviewProps} />
        <WeekCategoryList categoryRanking={weekStatistics?.categoryTodoCounts ?? []} />
      </S.WeekAchievementContainer>
    </S.WeekStatisticsMainContainer>
  );
};

export default WeekStatisticsMain;
