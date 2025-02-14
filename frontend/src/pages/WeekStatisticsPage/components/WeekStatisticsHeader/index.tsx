import Header from '@/components/Header';

const WeekStatisticsHeader = () => {
  return (
    <Header>
      <Header.Left>
        <Header.BackButton />
      </Header.Left>
      <Header.Center>
        <Header.Title>주간 통계</Header.Title>
      </Header.Center>
      <Header.Right>
        <Header.MenuButton />
      </Header.Right>
    </Header>
  );
};

export default WeekStatisticsHeader;
