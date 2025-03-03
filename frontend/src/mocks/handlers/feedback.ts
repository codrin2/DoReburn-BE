import { http, HttpResponse } from 'msw';

import MEMBER_STATUS from '../data/memberStatus.json';

import { MOCK_API_URL } from '@/constants/url';
import TODAY_ACHIEVEMENT_DATA from '@/mocks/data/todayAchievementData.json';

interface FeedbackParams {
  planId: string;
}

const getTodayAchievementHandler = () => {
  return HttpResponse.json(TODAY_ACHIEVEMENT_DATA);
};

const saveFeedbackHandler = async ({
  params,
  request,
}: {
  params: FeedbackParams;
  request: Request;
}) => {
  const { planId } = params;

  // mood, memo
  const newFeedback = await request.json();

  MEMBER_STATUS.data.status = 'STOP';

  return HttpResponse.json({ data: { feedbackId: 12345 } });
};

export const handlers = [
  http.get(MOCK_API_URL.todayAchievement, getTodayAchievementHandler),
  http.post<FeedbackParams>(MOCK_API_URL.saveFeedback, saveFeedbackHandler),
];
