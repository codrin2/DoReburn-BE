import { SUBWAY_LINES } from '@/constants/subwayLines';
import { PathType } from '@/pages/RouteSelectPage/RouteSelectPage.types';
import theme from '@/styles/theme';

export const getPathBarWidth = (path: PathType, totalTime: number) => {
  if (path.sectionTime === 0) return 0;

  return (path.sectionTime / totalTime) * 100;
};

export const getPathColor = (trafficType: string, subwayCode: number | null) => {
  if (trafficType === 'BUS') {
    return theme.colors.Bus;
  }
  if (trafficType === 'SUBWAY') {
    if (subwayCode && subwayCode in SUBWAY_LINES) {
      const subwayLine = SUBWAY_LINES[subwayCode as keyof typeof SUBWAY_LINES];

      return subwayLine.color;
    }

    return theme.colors.Subway;
  }

  return 'transparent';
};
