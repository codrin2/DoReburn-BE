import * as S from './WeekOverview.styled';

interface WeekOverviewProps {
  moveTime: number;
  usageTime: number;
  lastWeekDiff: number;
  todoCount: number;
}

const WeekOverview = ({ moveTime, usageTime, lastWeekDiff, todoCount }: WeekOverviewProps) => {
  return (
    <S.WeekOverviewContainer>
      <S.WeekOverviewTitle>주간 현황</S.WeekOverviewTitle>

      <S.WeekOverviewContent>
        <S.WeekAchievement>
          <span>{usageTime}분 동안</span>
          <span>{todoCount}개의 할 일을 했어요</span>
        </S.WeekAchievement>

        <S.CompareLastWeek>
          <S.CompareTitle>지난 주와의 비교</S.CompareTitle>
          <S.CompareValue>
            {lastWeekDiff > 0 && '+'}
            {lastWeekDiff}분
          </S.CompareValue>
        </S.CompareLastWeek>

        <S.TimeInfoWrapper>
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 이동 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>{moveTime}분</S.TimeInfoValue>
          </S.TimeInfoBox>
          <S.Divider />
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 활용 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>{usageTime}분</S.TimeInfoValue>
          </S.TimeInfoBox>
        </S.TimeInfoWrapper>
      </S.WeekOverviewContent>
    </S.WeekOverviewContainer>
  );
};

export default WeekOverview;
