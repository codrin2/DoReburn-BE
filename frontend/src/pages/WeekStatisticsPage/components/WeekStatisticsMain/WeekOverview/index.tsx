import * as S from './WeekOverview.styled';

const MOOD_IMAGES = {
  DISSATISFIED: '/images/mood/dissatisfied.png',
  NORMAL: '/images/mood/normal.png',
  SATISFIED: '/images/mood/satisfied.png',
};

interface WeekOverviewProps {
  totalAvailableTime: number;
  lastWeekDiff: number;
  totalTodoCount: number;
  moodCounts: {
    mood: string;
    count: number;
  }[];
}

const WeekOverview = ({
  totalAvailableTime,
  lastWeekDiff,
  totalTodoCount,
  moodCounts,
}: WeekOverviewProps) => {
  return (
    <S.WeekOverviewContainer>
      <S.WeekOverviewTitle>주간 현황</S.WeekOverviewTitle>

      <S.WeekOverviewContent>
        <S.WeekAchievement>
          <span>{totalAvailableTime}분 동안</span>
          <span>{totalTodoCount}개의 할 일을 했어요</span>
        </S.WeekAchievement>

        <S.CompareLastWeek>
          <S.CompareTitle>지난 주와의 비교</S.CompareTitle>
          <S.CompareValue>
            {lastWeekDiff > 0 && '+'}
            {lastWeekDiff}분
          </S.CompareValue>
        </S.CompareLastWeek>

        {/* FIXME: 이미지 넣기 */}
        <div>
          {moodCounts.map((mood) => (
            <div key={mood.mood}>
              <span>{mood.mood}</span>
              <span>{mood.count}</span>
            </div>
          ))}
        </div>
        {/* <S.TimeInfoWrapper>
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 이동 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>{totalAvailableTime}분</S.TimeInfoValue>
          </S.TimeInfoBox>
          <S.Divider />
          <S.TimeInfoBox>
            <S.TimeInfoLabel>총 활용 시간</S.TimeInfoLabel>
            <S.TimeInfoValue>{totalAvailableTime}분</S.TimeInfoValue>
          </S.TimeInfoBox>
        </S.TimeInfoWrapper> */}
      </S.WeekOverviewContent>
    </S.WeekOverviewContainer>
  );
};

export default WeekOverview;
