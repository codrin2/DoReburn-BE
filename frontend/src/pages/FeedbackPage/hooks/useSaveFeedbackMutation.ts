import { useMutation, useQueryClient } from '@tanstack/react-query';

import { saveFeedback, saveFeedbackRequest } from '@/api/feedback';
import { QUERY_KEY } from '@/constants/queryKey';

const useSaveFeedbackMutation = () => {
  const queryClient = useQueryClient();

  const { mutate: saveFeedbackMutate } = useMutation({
    mutationFn: ({ planId, body }: { planId: number; body: saveFeedbackRequest }) =>
      saveFeedback(planId, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.memberStatus] });
    },
  });

  return { saveFeedbackMutate };
};

export default useSaveFeedbackMutation;
