import * as S from './WeekOverview.styled';

const WeekOverview = () => {
  return (
    <S.WeekOverviewContainer>
      <S.WeekOverviewTitle>주간 현황</S.WeekOverviewTitle>
      <S.WeekOverviewContent>
        <S.WeekAchievement>
          <span>425분 동안</span>
          <span>20개의 할 일을 했어요</span>
        </S.WeekAchievement>

        <S.CompareLastWeek>
          <S.CompareTitle>지난 주와의 비교</S.CompareTitle>
          <S.CompareValue>+ 47분</S.CompareValue>
        </S.CompareLastWeek>

        <S.TimeInfoWrapper>
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 이동 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>520분</S.TimeInfoValue>
          </S.TimeInfoBox>
          <S.Divider />
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 활용 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>425분</S.TimeInfoValue>
          </S.TimeInfoBox>
        </S.TimeInfoWrapper>
      </S.WeekOverviewContent>
    </S.WeekOverviewContainer>
  );
};

export default WeekOverview;
