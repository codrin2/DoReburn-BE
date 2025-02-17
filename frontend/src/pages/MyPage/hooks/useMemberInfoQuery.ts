import { useQuery } from '@tanstack/react-query';

import { getMemberInfo } from '@/api/member';

export const useMemberInfoQuery = () => {
  return useQuery({ queryKey: ['memberInfo'], queryFn: getMemberInfo });
};
