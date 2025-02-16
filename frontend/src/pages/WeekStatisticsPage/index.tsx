import WeekStatisticsHeader from './components/WeekStatisticsHeader';
import WeekStatisticsMain from './components/WeekStatisticsMain';
import * as S from './WeekStatisticsPage.styled';

const WeekStatisticsPage = () => {
  return (
    <S.WeekStatisticsPageContainer>
      <WeekStatisticsHeader />
      <WeekStatisticsMain />
    </S.WeekStatisticsPageContainer>
  );
};

export default WeekStatisticsPage;
