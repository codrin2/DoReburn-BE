import { useQuery } from '@tanstack/react-query';

import { getDayStatistics } from '@/api/statistics';
import { QUERY_KEY } from '@/constants/queryKey';

const useDayStatisticsQuery = (date: string) => {
  return useQuery({
    queryKey: [QUERY_KEY.dayStatistics, date],
    queryFn: () => getDayStatistics({ date }),
  });
};

export default useDayStatisticsQuery;
