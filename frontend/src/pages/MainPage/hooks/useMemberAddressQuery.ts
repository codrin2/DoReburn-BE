import { useQuery } from '@tanstack/react-query';

import { getMemberAddress } from '@/api/member';
import { QUERY_KEY } from '@/constants/queryKey';

const useMemberAddressQuery = () => {
  return useQuery({
    queryKey: [QUERY_KEY.memberAddress],
    queryFn: getMemberAddress,
    staleTime: Infinity,
    gcTime: Infinity,
  });
};

export default useMemberAddressQuery;
