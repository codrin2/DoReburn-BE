import * as S from './TimeBlockHeader.styled';
import { TRAFFIC_ICON, TRAFFIC_TYPE } from '../../PlanPage.constants';

import Icon from '@/components/Icon';
import { getPathColor } from '@/pages/RouteSelectPage/components/RouteResult/RouteList/RouteItem/RouteItem.utils';

interface TimeBlockHeaderProps {
  trafficType: 'SUBWAY' | 'BUS';
  subwayCode: number | null;
}

const TimeBlockHeader = ({ trafficType, subwayCode }: TimeBlockHeaderProps) => {
  const pathColor = getPathColor({
    trafficType,
    subwayCode,
  });

  return (
    <S.TransportHeader>
      <S.TransportIconWrapper $pathColor={pathColor}>
        <Icon icon={TRAFFIC_ICON[trafficType]} width={16} height={16} />
      </S.TransportIconWrapper>
      <S.TransportType>{TRAFFIC_TYPE[trafficType]}</S.TransportType>
    </S.TransportHeader>
  );
};

export default TimeBlockHeader;
