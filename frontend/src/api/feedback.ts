import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';

interface Todo {
  category: string;
  title: string;
}

export interface TodayAchievement {
  data: {
    planId: number;
    totalSectionTime: number;
    totalTodoCount: number;
    todos: Todo[];
  };
}

export interface saveFeedbackRequest {
  mood: string;
  memo: string;
}

interface SaveFeedbackResponse {
  data: saveFeedbackRequest;
}

export const getTodayAchievement = async (): Promise<TodayAchievement> => {
  const result = await fetchClient.get<TodayAchievement>(API_URL.todayAchievement);

  return result;
};

export const saveFeedback = async (planId: number, body: saveFeedbackRequest) => {
  const result = await fetchClient.post<SaveFeedbackResponse>(API_URL.saveFeedback(planId), {
    body: {
      mood: body.mood,
      memo: body.memo,
    },
  });

  return result.data;
};
