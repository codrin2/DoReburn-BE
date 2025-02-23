import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';

interface WeekStatistics {
  memberCreateDate: string;
  dayAvailableTimes: {
    date: string;
    availableTime: number;
  }[];
  totalTodoCount: number;
  lastWeekDiff: number;
  totalAvailableTime: number;
  moodCounts: {
    mood: string;
    count: number;
  }[];
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
  memberCreateDate: string;
  totalUsageTime: number;
  totalTodoCount: number;
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
