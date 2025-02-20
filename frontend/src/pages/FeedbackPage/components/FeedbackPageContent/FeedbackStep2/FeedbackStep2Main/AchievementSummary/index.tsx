import * as S from './AchievementSummary.styled';

import { useMemberInfoQuery } from '@/pages/MyPage/hooks/useMemberInfoQuery';

interface AchievementSummaryProps {
  time: number;
  count: number;
}

const AchievementSummary = ({ time, count }: AchievementSummaryProps) => {
  const { data: memberInfo } = useMemberInfoQuery();

  return (
    <S.SummaryBox>
      <S.SummaryText>{memberInfo?.nickname}&nbsp;님은 오늘</S.SummaryText>
      <S.SummaryText>
        <S.SummaryKeyword>{time}</S.SummaryKeyword>분 동안
        <S.SummaryKeyword>{count}</S.SummaryKeyword>개를 달성했어요
      </S.SummaryText>
    </S.SummaryBox>
  );
};

export default AchievementSummary;
