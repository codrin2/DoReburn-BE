import { useMutation } from '@tanstack/react-query';

import { saveFeedback, saveFeedbackRequest } from '@/api/feedback';

const useSaveFeedbackMutation = (openModal: () => void) => {
  const { mutate: saveFeedbackMutate } = useMutation({
    mutationFn: ({ planId, body }: { planId: number; body: saveFeedbackRequest }) =>
      saveFeedback(planId, body),
    onSuccess: () => {
      openModal();
    },
  });

  return { saveFeedbackMutate };
};

export default useSaveFeedbackMutation;
