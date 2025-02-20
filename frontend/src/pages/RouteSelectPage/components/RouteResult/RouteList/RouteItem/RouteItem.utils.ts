import { BUS_TYPE } from '@/constants/busType';
import { SUBWAY_LINES } from '@/constants/subwayLines';
import { PathType } from '@/pages/RouteSelectPage/RouteSelectPage.types';
import theme from '@/styles/theme';

export const getPathBarWidth = (path: PathType, totalTime: number) => {
  if (path.sectionTime === 0) return 0;

  return (path.sectionTime / totalTime) * 100;
};

interface GetPathColorProps {
  trafficType: string;
  subwayCode?: number | null;
  busType?: number | null;
}

export const getPathColor = ({ trafficType, subwayCode, busType }: GetPathColorProps) => {
  if (trafficType === 'BUS') {
    const busColor = BUS_TYPE[busType as keyof typeof BUS_TYPE]?.color;

    return busColor || theme.colors.gray950;
  }
  if (trafficType === 'SUBWAY') {
    const subwayColor = SUBWAY_LINES[subwayCode as keyof typeof SUBWAY_LINES]?.color;

    return subwayColor || theme.colors.Subway;
  }

  return 'transparent';
};
