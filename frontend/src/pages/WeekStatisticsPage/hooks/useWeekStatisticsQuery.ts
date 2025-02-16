import { useQuery } from '@tanstack/react-query';

import { getWeekStatistics } from '@/api/statistics';
import { QUERY_KEY } from '@/constants/queryKey';

const useWeekStatisticsQuery = (startDate: string) => {
  return useQuery({
    queryKey: [QUERY_KEY.weekStatistics, startDate],
    queryFn: () => getWeekStatistics({ startDate }),
  });
};

export default useWeekStatisticsQuery;
