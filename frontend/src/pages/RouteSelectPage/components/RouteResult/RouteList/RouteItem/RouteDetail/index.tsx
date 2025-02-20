import { getPathColor } from '../RouteItem.utils';
import * as S from './RouteDetail.styled';

import Icon, { IconType } from '@/components/Icon';
import { BUS_TYPE } from '@/constants/busType';
import { SUBWAY_LINES } from '@/constants/subwayLines';
import { PathType, RouteType } from '@/pages/RouteSelectPage/RouteSelectPage.types';
import theme from '@/styles/theme';

const DEFAULT_PATH_NAME = {
  BUS: '버스',
  SUBWAY: '지하철',
};

const ICON_MAPPER: Record<string, IconType> = {
  BUS: 'Bus',
  SUBWAY: 'Subway',
};

const renderRouteItem = (path: PathType, index: number) => {
  const pathColor = getPathColor({
    trafficType: path.trafficType,
    subwayCode: path.subwayCode,
    busType: path.busType,
  });
  const isSubway = path.trafficType === 'SUBWAY';
  const pathName = isSubway
    ? (SUBWAY_LINES[path.subwayCode as keyof typeof SUBWAY_LINES]?.name ?? DEFAULT_PATH_NAME.SUBWAY)
    : (BUS_TYPE[path.busType as keyof typeof BUS_TYPE]?.name ?? DEFAULT_PATH_NAME.BUS);

  return (
    <S.RouteDetailItem key={index}>
      <S.DetailItemLabel>
        <Icon
          icon={ICON_MAPPER[path.trafficType as keyof typeof ICON_MAPPER]}
          width={16}
          height={16}
          color={pathColor}
        />
        <S.DetailItemType $color={pathColor}>{pathName}</S.DetailItemType>
      </S.DetailItemLabel>
      <S.DetailContentWrapper>
        <S.DetailItemPath>{path.startName}</S.DetailItemPath>
        {path.trafficType === 'BUS' && (
          <S.BusNumber $color={pathColor}>{path.busNumber}</S.BusNumber>
        )}
      </S.DetailContentWrapper>
    </S.RouteDetailItem>
  );
};

const RouteDetail = ({ route }: { route: RouteType }) => {
  const filteredPaths = route.paths.filter((path) => path.trafficType !== 'WALK');
  const lastPath = filteredPaths[filteredPaths.length - 1];

  return (
    <S.RouteDetailList>
      {filteredPaths.map(renderRouteItem)}
      {lastPath && (
        <S.RouteDetailItem>
          <S.DetailItemLabel>
            <Icon icon="Map" width={16} height={16} color={theme.colors.gray700} />
            <S.DetailItemType $color={theme.colors.gray700}>하차</S.DetailItemType>
          </S.DetailItemLabel>
          <S.DetailItemPath>{lastPath.endName}</S.DetailItemPath>
        </S.RouteDetailItem>
      )}
    </S.RouteDetailList>
  );
};

export default RouteDetail;
