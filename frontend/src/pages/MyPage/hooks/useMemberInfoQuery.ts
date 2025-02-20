import { useQuery } from '@tanstack/react-query';

import { getMemberInfo } from '@/api/member';
import { QUERY_KEY } from '@/constants/queryKey';

export const useMemberInfoQuery = () => {
  return useQuery({ queryKey: [QUERY_KEY.memberInfo], queryFn: getMemberInfo });
};
