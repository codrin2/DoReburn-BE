import { http, HttpResponse } from 'msw';

import WEEK_STATISTICS_DATA from '../data/weekStatistics.json';

import { MOCK_API_URL } from '@/constants/url';

export const handlers = [
  http.get(`${MOCK_API_URL.weekStatistics}`, () => {
    return HttpResponse.json(WEEK_STATISTICS_DATA);
  }),
];
