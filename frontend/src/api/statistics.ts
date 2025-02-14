import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';

interface WeekStatistics {
  dayUsageTimes: {
    date: string;
    usageTime: number;
  }[];
  totalTodoCount: number;
  lastWeekDiff: number;
  totalMoveTime: number;
  totalUsageTime: number;
  categoryTodoCounts: {
    category: string;
    count: number;
  }[];
}

interface WeekStatisticsResponse {
  data: WeekStatistics;
}

export const getWeekStatistics = async ({ startDate }: { startDate: string }) => {
  const result = await fetchClient.get<WeekStatisticsResponse>(
    `${API_URL.weekStatistics}?startDate=${startDate}`,
  );

  return result.data;
};
