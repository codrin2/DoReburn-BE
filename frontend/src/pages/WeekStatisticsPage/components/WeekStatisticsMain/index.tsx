import WeekDate from './WeekDate';
import WeekOverview from './WeekOverview';
import * as S from './WeekStatisticsMain.styled';
import WeekTodos from './WeekTodos';

interface WeekStatisticsMainProps {
  weekStartDate: string;
  setWeekStartDate: (date: string) => void;
}

const WeekStatisticsMain = ({ weekStartDate, setWeekStartDate }: WeekStatisticsMainProps) => {
  return (
    <S.WeekStatisticsMainContainer>
      <WeekDate weekStartDate={weekStartDate} setWeekStartDate={setWeekStartDate} />
      <WeekOverview />
      <WeekTodos />
    </S.WeekStatisticsMainContainer>
  );
};

export default WeekStatisticsMain;
