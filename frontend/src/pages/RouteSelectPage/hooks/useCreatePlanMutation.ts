import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import { CreatePlanRequest, createPlan } from '@/api/plan';

const useCreatePlanMutation = (startX: string, startY: string, endX: string, endY: string) => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (request: CreatePlanRequest) => createPlan(request, { startX, startY, endX, endY }),
    onSuccess: () => {
      navigate('/plan');
    },
  });
};

export default useCreatePlanMutation;
