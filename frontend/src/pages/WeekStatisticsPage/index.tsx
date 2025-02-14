import { useEffect, useState } from 'react';

import WeekStatisticsHeader from './components/WeekStatisticsHeader';
import WeekStatisticsMain from './components/WeekStatisticsMain';
import { getStartOfWeek } from './utils/getStartOfWeek';
import * as S from './WeekStatisticsPage.styled';

const WeekStatisticsPage = () => {
  const today = new Date().toISOString();

  const [weekStartDate, setWeekStartDate] = useState(getStartOfWeek(today));

  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search);
    const startDate = searchParams.get('startDate');

    if (startDate) {
      setWeekStartDate(getStartOfWeek(startDate));
    }
  }, []);

  return (
    <S.WeekStatisticsPageContainer>
      <WeekStatisticsHeader />
      <WeekStatisticsMain weekStartDate={weekStartDate} setWeekStartDate={setWeekStartDate} />
    </S.WeekStatisticsPageContainer>
  );
};

export default WeekStatisticsPage;
