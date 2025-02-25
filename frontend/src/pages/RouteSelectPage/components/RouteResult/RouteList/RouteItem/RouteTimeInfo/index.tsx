import * as S from './RouteTimeInfo.styled';

import Icon from '@/components/Icon';
import theme from '@/styles/theme';
import { minutesToHours } from '@/utils/time';

interface RouteTimeInfoProps {
  totalTime: number;
  totalSectionTime: number;
}

const RouteTimeInfo = ({ totalTime, totalSectionTime }: RouteTimeInfoProps) => {
  return (
    <S.TimeContainer>
      <S.TotalTime>{minutesToHours(totalTime)}</S.TotalTime>

      <S.TotalSectionTimeBox>
        <Icon icon="Fire" width={16} height={16} color={theme.colors.green600} />
        <span>활용 가능 시간</span>
        <S.TotalSectionTime>{minutesToHours(totalSectionTime)}</S.TotalSectionTime>
      </S.TotalSectionTimeBox>
    </S.TimeContainer>
  );
};

export default RouteTimeInfo;
