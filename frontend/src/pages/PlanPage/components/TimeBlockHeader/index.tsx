import * as S from './TimeBlockHeader.styled';
import { TRAFFIC_ICON } from '../../PlanPage.constants';

import { Path } from '@/api/plan';
import Icon from '@/components/Icon';
import { SUBWAY_LINES } from '@/constants/subwayLines';
import { getPathColor } from '@/pages/RouteSelectPage/components/RouteResult/RouteList/RouteItem/RouteItem.utils';

interface TimeBlockHeaderProps {
  path: Path;
}

const TimeBlockHeader = ({ path }: TimeBlockHeaderProps) => {
  const { trafficType, subwayCode, startName: startStation, busNumber, busType } = path;

  const subwayLine = SUBWAY_LINES[subwayCode as keyof typeof SUBWAY_LINES];
  const pathColor = getPathColor({
    trafficType,
    subwayCode,
    busType,
  });

  return (
    <S.TransportHeader>
      <S.TransportIconWrapper $pathColor={pathColor}>
        <Icon icon={TRAFFIC_ICON[trafficType]} width={16} height={16} />
      </S.TransportIconWrapper>
      <S.TransportNumber $pathColor={pathColor}>{busNumber || subwayLine.name}</S.TransportNumber>
      <S.Station>{startStation}</S.Station>
    </S.TransportHeader>
  );
};

export default TimeBlockHeader;
