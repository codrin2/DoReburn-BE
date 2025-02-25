import * as S from './WeekOverview.styled';

import MOOD_DISSATISFIED from '@/assets/images/moodDissatisfied.webp';
import MOOD_MODERATE from '@/assets/images/moodModerate.webp';
import MOOD_SATISFIED from '@/assets/images/moodSatisfied.webp';
import { minutesToHours } from '@/utils/time';

const MOOD_IMAGES: Record<string, string> = {
  DISSATISFIED: MOOD_DISSATISFIED,
  MODERATE: MOOD_MODERATE,
  SATISFIED: MOOD_SATISFIED,
};

const MOOD_LABELS: Record<string, string> = {
  DISSATISFIED: '"아쉬워요"',
  MODERATE: '"보통이에요"',
  SATISFIED: '"만족해요"',
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
          <span>{minutesToHours(totalAvailableTime)} 동안</span>
          <span>{totalTodoCount}개의 할 일을 했어요</span>
        </S.WeekAchievement>

        <S.CompareLastWeek>
          <S.CompareTitle>지난 주와의 비교</S.CompareTitle>
          <S.CompareValue>
            {lastWeekDiff > 0 && '+'}
            {minutesToHours(lastWeekDiff)}
          </S.CompareValue>
        </S.CompareLastWeek>

        <S.MoodContainer>
          {moodCounts.map((mood) => (
            <S.MoodItem key={mood.mood}>
              <S.MoodImage src={MOOD_IMAGES[mood.mood]} alt={mood.mood} />
              <S.MoodAnimationLable>{MOOD_LABELS[mood.mood]}</S.MoodAnimationLable>
              <S.MoodCount>{mood.count} 개</S.MoodCount>
            </S.MoodItem>
          ))}
        </S.MoodContainer>
      </S.WeekOverviewContent>
    </S.WeekOverviewContainer>
  );
};

export default WeekOverview;
