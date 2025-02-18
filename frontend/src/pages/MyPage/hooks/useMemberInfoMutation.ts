import { useMutation, useQueryClient } from '@tanstack/react-query';

import { updateMemberInfo } from '@/api/member';

const useMemberInfoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateMemberInfo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['memberInfo'] });
    },
  });
};

export default useMemberInfoMutation;
