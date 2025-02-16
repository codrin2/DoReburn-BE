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
    usageTime: number;
  }[];
}

interface WeekStatisticsResponse {
  data: WeekStatistics;
}

interface DayStatistics {
  totalMoveTime: number;
  totalUsageTime: number;
  feedbacks: {
    mood: string;
    memo: string;
  }[];
  categoryTodoCounts: {
    category: string;
    count: number;
  }[];
}

interface DayStatisticsResponse {
  data: DayStatistics;
}

export const getWeekStatistics = async ({ startDate }: { startDate: string }) => {
  const result = await fetchClient.get<WeekStatisticsResponse>(
    `${API_URL.weekStatistics}?startDate=${startDate}`,
  );

  return result.data;
};

export const getDayStatistics = async ({ date }: { date: string }) => {
  const result = await fetchClient.get<DayStatisticsResponse>(
    `${API_URL.dayStatistics}?date=${date}`,
  );

  return result.data;
};
