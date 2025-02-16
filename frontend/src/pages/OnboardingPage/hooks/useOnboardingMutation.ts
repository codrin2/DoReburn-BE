import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import { saveOnboarding } from '@/api/onboarding';

const useOnboardingMutation = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: saveOnboarding,
    onSuccess: () => {
      navigate('/');
    },
  });
};

export default useOnboardingMutation;
