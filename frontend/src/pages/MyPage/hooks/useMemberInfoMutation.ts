import { useMutation, useQueryClient } from '@tanstack/react-query';

import { updateMemberInfo } from '@/api/member';
import { QUERY_KEY } from '@/constants/queryKey';

const useMemberInfoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateMemberInfo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.memberInfo] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.memberAddress] });
    },
  });
};

export default useMemberInfoMutation;
